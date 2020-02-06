/*
 * Copyright 2020 looseBoxes.com
 *
 * Licensed under the looseBoxes Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.looseboxes.cometd.chat.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.common.HashMapMessage;
import org.cometd.server.ServerMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>
 * <pre>
 * String channel = "/service/privatechat";
 * String room = "/chat/johnsfamily";
 * ClientSession johnClient = getClientSession();
 * ChatSession john = new ChatSessionImpl(johnClient, new ChatConfig(channel, room, "John");
 * 
 * // Join John to chat
 * Future&lt;Message&gt; johnJoin = john.join();
 * 
 * // Seperate client for each user
 * ClientSession maryClient = getClientSession();
 * ChatSession mary = new ChatSessionImpl(maryClient, new ChatConfig(channel, room, "Mary");
 * 
 * // Join Mary to chat
 * Future&lt;Message&gt; maryJoin = mary.join();
 * 
 * // Wait for John and Mary joining chat to return
 * long timeout = 5_000;
 * johnJoin.get(timeout, TimeUnit.MILLISECONDS);
 * maryJoin.get(timeout, TimeUnit.MILLISECONDS);
 * 
 * john.send("Hi", "Mary");
 * 
 * mary.end("Hi John", "John");
 * 
 * john.leave();
 * mary.leave();
 * </pre>
 * </code>
 * @author USER
 */
public final class ChatSessionImpl implements ChatSession {
    
    private static final String REMOVE = "";
    
    private static final Logger LOG = LoggerFactory.getLogger(ChatSessionImpl.class);
    
    private final ClientSession clientSession;
    private final ChatConfig chatConfig;
    private final StatusBean status;

    private CompletableFuture<Message> handshakeFuture;
    private CompletableFuture<Message> disconnectFuture;
    
    private final ClientSessionChannel.MessageListener handshakeListener;
    private final ClientSessionChannel.MessageListener connectListener;
    private final ClientSessionChannel.MessageListener disconnectListener;
    
    public ChatSessionImpl(ClientSession client, ChatConfig chatConfig) {
    
        this.clientSession = Objects.requireNonNull(client);
        this.chatConfig = chatConfig.forUser(chatConfig.getUser());
        this.status = new StatusBean();
        
        this.handshakeListener = (ClientSessionChannel csc, Message msg) -> {
                metaHandshake(csc, msg);
        };
        client.getChannel(Channel.META_HANDSHAKE).addListener(this.handshakeListener);
        
        this.connectListener = (ClientSessionChannel csc, Message msg) -> {
                metaConnect(csc, msg);
        };
        client.getChannel(Channel.META_CONNECT).addListener(this.connectListener);

        this.disconnectListener = (ClientSessionChannel csc, Message msg) -> {
                metaDisconnect(csc, msg);
        };
        client.getChannel(Channel.META_DISCONNECT).addListener(this.disconnectListener);
    }

    /**
     * Send the text message to peer .
     * @param textMessage The message to send. Null or empty text not allowed.
     * @param peerUserName
     */
    @Override
    public Future<Message> send(String textMessage, String peerUserName) {
        
        final CompletableFuture<Message> future = new CompletableFuture<>();
        
        this.send(textMessage, peerUserName, (Message msg) -> {
            this.update("response to send request", msg, future);
        });
        
        return future;
    }

    /**
     * Send the text message to peer .
     * @param textMessage The message to send. Null or empty text not allowed.
     * @param peerUserName
     */
    @Override
    public void send(String textMessage, String peerUserName, 
            ClientSession.MessageListener messageListener) {

        LOG.debug("send(\"{}\", \"{}\") sender: {}", textMessage, peerUserName, chatConfig.getUser());

        requireNonNullOrEmpty(textMessage);
        requireNonNullOrEmpty(peerUserName);

        final Message msg = new HashMapMessage();
        msg.put(Chat.ROOM, this.chatConfig.getRoom());
        msg.put(Chat.USER, this.chatConfig.getUser());
        msg.put(Chat.CHAT, textMessage);
        msg.put(Chat.PEER, peerUserName);
        
        clientSession.getChannel(this.chatConfig.getChannel()).publish(msg, messageListener);
    }
    
    /**
     * Handshake with the server. When user logging into your system, you can call this method
     * to connect that user to cometd server.
     * Calls {@link #connect()} then {@link #subscribe()} asynchronously
     * @see #connect() 
     * @see #subscribe() 
     * @return 
     */
    @Override
    public CompletableFuture<Message> join() {
        
        LOG.debug("join(), user: {}", chatConfig.getUser());
        
        return this.connect().thenComposeAsync(msg -> {
            if(msg.isSuccessful()) {
                if(ChatSession.BUG_001_FIXED) {
                    return subscribe();
                }else{
                    subscribe();
                }
            }
            return CompletableFuture.completedFuture(msg);
        });
    }
    
    @Override
    public CompletableFuture<Message> connect() {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("connect(), user: {}", u);
        
        this.handshakeFuture = this.requireNullThenCreateNew(this.handshakeFuture, "connect()");

        this.status.setDisconnecting(false);

        final Map<String, Object> template = new HashMap<>();
        template.put(Chat.WEBSOCKET_ENABLED, this.chatConfig.isWebsocketEnabled());
        template.put(Chat.LOG_LEVEL, this.chatConfig.getLogLevel());

        clientSession.handshake(template, (Message msg) -> {
            this.update("response to handshake request", msg, handshakeFuture);
        });
        
        return handshakeFuture;
    }
    
    @Override
    public CompletableFuture<Message> subscribe() {
        LOG.debug(REMOVE+"subscribe(), user: {}", chatConfig.getUser());
        
        final CompletableFuture<Message> future = new CompletableFuture<>();

        clientSession.batch(() -> {
            
            final Consumer<Message> onChatSuccess = (msg) -> {
                this.status.setSubscribedToChat(true);
                receive(msg);
            };
            subscribe(chatConfig.getChannel(), onChatSuccess, future);
            
            final Consumer<Message> onMembersSuccess = (msg) -> {
                this.status.setSubscribedToMembers(true);
                members(msg);
            };
            subscribe(chatConfig.getMembersServiceChannel(), onMembersSuccess);
        });
        
        return future;
    }

    private void subscribe(String channel, Consumer<Message> onSuccess) {
        subscribe(channel, onSuccess, null);
    }
    
    private void subscribe(String channel, 
            Consumer<Message> onSuccess, CompletableFuture<Message> future) {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("[{}] Subscribing to channel: {}", u, channel);
        
        clientSession.getChannel(channel).subscribe((csc, msg) -> {
            this.update("response to subscribe request", msg, onSuccess, future);
        });
    }

    /**
     * This method can be invoked to disconnect from the chat server.
     * When user logging off or user close the browser window, user should
     * be disconnected from cometd server.
     */
    @Override
    public Future<Message> leave() {
        LOG.debug("leave(), user: {}", chatConfig.getUser());
        
        this.unsubscribe();

        return this.disconnect();
    };
    
    @Override
    public CompletableFuture<Message> unsubscribe() {
        LOG.debug("unsubscribe(), user: {}", chatConfig.getUser());
        
        final CompletableFuture<Message> future = new CompletableFuture<>();

        clientSession.batch(() -> {
            
            unsubscribe(chatConfig.getChannel(), 
                    (msg) -> status.setSubscribedToChat(false),
                    future);
            
            unsubscribe(chatConfig.getMembersServiceChannel(), 
                    (msg) -> status.setSubscribedToMembers(false));
        });
        
        return future;
    }
    
    private void unsubscribe(String channel, Consumer<Message> onSuccess) {
        this.unsubscribe(channel, onSuccess, null);
    }
    
    private void unsubscribe(String channel, 
            Consumer<Message> onSuccess, CompletableFuture<Message> future) {
        final String u = chatConfig.getUser();
        
        LOG.debug("[{}] Unsubscribing from channel: {}", u, channel);

        this.clientSession.getChannel(channel).unsubscribe((csc, msg) -> {
            this.update("response to unsubscribe request", msg, onSuccess, future);
        });
    }
    
    @Override
    public CompletableFuture<Message> disconnect() {
        LOG.debug("disconnect(), user: {}", chatConfig.getUser());
        
        this.disconnectFuture = this.requireNullThenCreateNew(this.disconnectFuture, "disconnect()");
        
        clientSession.disconnect(msg -> {

            this.update("response to disconnect request", msg, disconnectFuture);

            this.removeListeners();
        });

        this.status.setDisconnecting(true);

        return disconnectFuture;
    }
    
    private void removeListeners(){
        clientSession.getChannel(Channel.META_HANDSHAKE).removeListener(this.handshakeListener);
        clientSession.getChannel(Channel.META_CONNECT).removeListener(this.connectListener);
        clientSession.getChannel(Channel.META_DISCONNECT).removeListener(this.disconnectListener);
    }
    
    private void metaHandshake(ClientSessionChannel metaHandshake, Message msg) {
        this.update("metaHandshake(..)", msg, handshakeFuture);
    }
    
    private void metaConnect(ClientSessionChannel metaConnect, Message msg) {
        log("metaConnect(..)", msg);
         
        if (this.status.isDisconnecting()) {
            this.status.setConnected(false);
            this.connectionClosed(metaConnect);
        } else {
            final boolean wasConnected = this.status.isConnected();
            this.status.setConnected(msg.isSuccessful());
            if (!wasConnected && this.status.isConnected()) {
                this.connectionEstablished(metaConnect);
            } else if (wasConnected && !this.status.isConnected()) {
                this.connectionBroken(metaConnect);
            }
        }
    }
    
    private void metaDisconnect(ClientSessionChannel metaDisconnect, Message msg) {
        this.update("metaDisconnect(..)", msg, (message) -> {
            this.status.setConnected(false);
            this.status.setDisconnecting(false);
        }, this.disconnectFuture);
    }
    
    /**
     * This function is invoked each time a message arrives on the chat channel
     * @param message
     */
    private void receive(Message message) { 
        log("receive(..)", message);
    }

    /**
     * This function is called each time a message arrives on the members channel
     * @param message
     */
    private void members(Message message) {
        log("members(..)", message);
    }

    private void connectionClosed(ClientSessionChannel metaConnect) {
        LOG.debug(REMOVE+"connectionClosed(..) user: {}", this.chatConfig.getUser());
//        final Message msg = new HashMapMessage();
//        msg.put(Chat.USER, "system");
//        msg.put(Chat.ROOM, "Connection to Server Closed");
//        receive(msg);
    };

    private void connectionBroken(ClientSessionChannel metaConnect){
        LOG.debug(REMOVE+"connectionBroken(..) user: {}", this.chatConfig.getUser());
//        chatUtil.clearMemberListHtml();
    };

    private void connectionEstablished(ClientSessionChannel metaConnect){
        LOG.debug(REMOVE+"connectionEstablished(..) user: {}", this.chatConfig.getUser());
        // connection establish (maybe not for first time), so just
        // tell local user and update membership
        final String channel = this.chatConfig.getMembersServiceChannel();
        final String room = "/members/" + this.chatConfig.getRoom().substring("/chat/".length());
        LOG.debug(REMOVE+"Chat room: {}, members room: {}", this.chatConfig.getRoom(), room);
        final ServerMessageImpl msg = new ServerMessageImpl();
        msg.setChannel(channel);
        msg.setClientId(clientSession.getId());
        final Message data = new HashMapMessage();
        data.put(Chat.USER, this.chatConfig.getUser());
        data.put(Chat.ROOM, room);
        msg.setData(data);
        metaConnect.getSession().getChannel(channel).publish(msg);
    };
    
    private void update(String ID, Message msg, Consumer<Message> onSuccess){
        update(ID, msg, onSuccess, null);
    }
    private void update(String ID, Message msg, CompletableFuture<Message> future){
        update(ID, msg, (message) -> {}, future);
    }
    private void update(String ID, Message msg, 
            Consumer<Message> onSuccess, CompletableFuture<Message> future){
        ID = ID.toUpperCase();
        log(ID, msg);
        if(msg.isSuccessful()) {
            onSuccess.accept(msg);
            if(future != null) {
                LOG.debug(REMOVE+"Completing " + ID + ", for user: {}", chatConfig.getUser());
                future.complete(msg);
            }
        }else{
            if(future != null) {
                LOG.debug(REMOVE+"Cancelling " + ID + ", for user: {}", chatConfig.getUser());
                future.completeExceptionally(newCancellationException());
            }
        }
    }
    private void log(String ID, Message msg) {
        ID = ID.toUpperCase();
        LOG.debug(REMOVE+ID+", user: {}, success: {}, message: {}", 
                chatConfig.getUser(), msg.isSuccessful(), msg);
    }
    
    private CompletableFuture<Message> requireNullThenCreateNew(CompletableFuture<Message> f, String method) {
        if(f != null) {
            throw new IllegalStateException(method + " method may only be called once");
        }else{
            return new CompletableFuture();
        }
    }
    
    private CancellationException newCancellationException() {
        return new CancellationException("\n"+this.status + "\n" + this.chatConfig);
    }

    private void requireNonNullOrEmpty(String s) {
        Objects.requireNonNull(s);
        if(s.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public ClientSession getClientSession() {
        return clientSession;
    }

    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }
    
    private static final class StatusBean implements Status, Serializable{
        private final AtomicBoolean connected = new AtomicBoolean(false);
        private final AtomicBoolean disconnecting = new AtomicBoolean(false);
        private final AtomicBoolean subscribedToChat = new AtomicBoolean(false);
        private final AtomicBoolean subscribedToMembers = new AtomicBoolean(false);
        private StatusBean() { }
        public void setConnected(boolean flag) {
            connected.compareAndSet(!flag, flag);
        }
        @Override
        public boolean isConnected() {
            return connected.get();
        }
        public void setDisconnecting(boolean flag) {
            disconnecting.compareAndSet(!flag, flag);
        }
        @Override
        public boolean isDisconnecting() {
            return disconnecting.get();
        }
        public void setSubscribedToChat(boolean flag) {
            subscribedToChat.compareAndSet(!flag, flag);
        }
        @Override
        public boolean isSubscribedToChat() {
            return subscribedToChat.get();
        }
        public void setSubscribedToMembers(boolean flag) {
            subscribedToMembers.compareAndSet(!flag, flag);
        }
        @Override
        public boolean isSubscribedToMembers() {
            return subscribedToMembers.get();
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 13 * hash + Objects.hashCode(this.connected);
            hash = 13 * hash + Objects.hashCode(this.disconnecting);
            hash = 13 * hash + Objects.hashCode(this.subscribedToChat);
            hash = 13 * hash + Objects.hashCode(this.subscribedToMembers);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StatusBean other = (StatusBean) obj;
            if (!Objects.equals(this.connected, other.connected)) {
                return false;
            }
            if (!Objects.equals(this.disconnecting, other.disconnecting)) {
                return false;
            }
            if (!Objects.equals(this.subscribedToChat, other.subscribedToChat)) {
                return false;
            }
            if (!Objects.equals(this.subscribedToMembers, other.subscribedToMembers)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ServiceStatus{" + "connected=" + connected + ", disconnecting=" + disconnecting + ", subscribedToChat=" + subscribedToChat + ", subscribedToMembers=" + subscribedToMembers + '}';
        }
    }
}
