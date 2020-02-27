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
import java.util.function.BiConsumer;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(ChatSessionImpl.class);
    
    private final ClientSession clientSession;
    private final ChatConfig chatConfig;
    private final StatusBean status;

    private CompletableFuture<Message> handshakeFuture;
    private CompletableFuture<Message> disconnectFuture;
    
    private final ClientSessionChannel.MessageListener handshakeListener;
    private final ClientSessionChannel.MessageListener connectListener;
    private final ClientSessionChannel.MessageListener subscribeListener;
    private final ClientSessionChannel.MessageListener unsubscribeListener;
    private final ClientSessionChannel.MessageListener disconnectListener;
    
    private final ClientSessionChannel.MessageListener chatListener;

    private final ChatListenerManager chatListenerManager;
    
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

        this.subscribeListener = (ClientSessionChannel csc, Message msg) -> {
                metaSubscribe(csc, msg);
        };
        client.getChannel(Channel.META_SUBSCRIBE).addListener(this.subscribeListener);

        this.unsubscribeListener = (ClientSessionChannel csc, Message msg) -> {
                metaUnsubscribe(csc, msg);
        };
        client.getChannel(Channel.META_UNSUBSCRIBE).addListener(this.unsubscribeListener);

        this.disconnectListener = (ClientSessionChannel csc, Message msg) -> {
                metaDisconnect(csc, msg);
        };
        client.getChannel(Channel.META_DISCONNECT).addListener(this.disconnectListener);

        this.chatListener = (ClientSessionChannel csc, Message msg) -> {
                chatReceived(csc, msg);
        };
        client.getChannel(this.chatConfig.getChannel()).addListener(this.chatListener);

        this.chatListenerManager = new ChatListenerManagerImpl();
    }
    
    @Override
    public boolean addListener(ChatListener listener) {
        return this.chatListenerManager.addListener(listener);
    }
    
    @Override
    public boolean removeListener(ChatListener listener) {
        return this.chatListenerManager.removeListener(listener);
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
            
            this.update("send(..)",  msg, future);
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
            
            this.update("connect()", msg, handshakeFuture);
        });
        
        return handshakeFuture;
    }
    
    @Override
    public CompletableFuture<Message> subscribe() {
        LOG.debug("subscribe(), user: {}", chatConfig.getUser());
        
        final CompletableFuture<Message> future = new CompletableFuture<>();

        clientSession.batch(() -> {
            
            subscribe(chatConfig.getChannel(), chatConfig.getRoom(), 
                    (csc, msg) -> this.status.setSubscribedToChat(true), future);
        });
        
        return future;
    }
    
    private void subscribe(String channel, String room,
            BiConsumer<ClientSessionChannel, Message> onSuccess, 
            CompletableFuture<Message> future) {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("[{}] Subscribing to channel: {}", u, channel);
        
        final ClientSessionChannel channelObj = clientSession.getChannel(channel);
        
        channelObj.subscribe(
            (csc, msg) -> {
                update("subscribe()", csc, msg, onSuccess, future);
            }, 
            (msg) -> { 
                update("subscribe()", channelObj, msg, onSuccess);
            }
        );
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
    }
    
    @Override
    public CompletableFuture<Message> unsubscribe() {
        LOG.debug("unsubscribe(), user: {}", chatConfig.getUser());
        
        final CompletableFuture<Message> future = new CompletableFuture<>();

        clientSession.batch(() -> {
            
            unsubscribe(chatConfig.getChannel(), chatConfig.getRoom(), 
                    (csc, msg) -> this.status.setSubscribedToChat(false), future);
        });
        
        return future;
    }
    
    private void unsubscribe(String channel, String room,
            BiConsumer<ClientSessionChannel, Message> onSuccess, 
            CompletableFuture<Message> future) {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("[{}] Unsubscribing from channel: {}", u, channel);
        
        final ClientSessionChannel channelObj = clientSession.getChannel(channel);
        
        channelObj.subscribe(
            (csc, msg) -> {
                update("unsubscribe()", csc, msg, onSuccess, future);
            }, 
            (msg) -> { 
                update("unsubscribe()", channelObj, msg, onSuccess);
            }
        );
    }
    
    @Override
    public CompletableFuture<Message> disconnect() {
        LOG.debug("disconnect(), user: {}", chatConfig.getUser());
        
        this.disconnectFuture = this.requireNullThenCreateNew(this.disconnectFuture, "disconnect()");
        
        clientSession.disconnect();

        this.status.setDisconnecting(true);

        return disconnectFuture;
    }
    
    private void metaHandshake(ClientSessionChannel channel, Message message) {
        this.update("metaHandshake(..)", channel, message, handshakeFuture);

        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onHandshake(event));
    }
    
    private void metaConnect(ClientSessionChannel channel, Message message) {
        this.trace("metaConnect(..)", message);
         
        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnect(event));

        if (this.status.isDisconnecting()) {
            this.status.setConnected(false);
            this.connectionClosed(channel, message);
        } else {
            final boolean wasConnected = this.status.isConnected();
            this.status.setConnected(message.isSuccessful());
            if (!wasConnected && this.status.isConnected()) {
                this.connectionEstablished(channel, message);
            } else if (wasConnected && !this.status.isConnected()) {
                this.connectionBroken(channel, message);
            }
        }
    }
    
    private void metaSubscribe(ClientSessionChannel channel, Message message) {
        
        this.update("metaSubscribe(..)", channel, message, 
                (csc, msg) -> this.updateSubscriptionStatus(msg, true));
        
        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onSubscribe(event));
    }

    private void metaUnsubscribe(ClientSessionChannel channel, Message message) {
        
        this.update("metaUnsubscribe(..)", channel, message, 
                (csc, msg) -> this.updateSubscriptionStatus(msg, false));

        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onUnsubscribe(event));
    }
    
    private void updateSubscriptionStatus(Message msg, boolean flag) {
        final String key = "subscription";
        if(this.chatConfig.getChannel().equals(msg.get(key))){
            this.status.setSubscribedToChat(flag);
        }
    }

    private void metaDisconnect(ClientSessionChannel channel, Message message) {
        this.update("metaDisconnect(..)", channel, message, (csc, msg) -> {
            this.status.setConnected(false);
            this.status.setDisconnecting(false);
        }, this.disconnectFuture);

        this.removeListeners();
        
        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onDisconnect(event));
    }
    
    private void chatReceived(ClientSessionChannel channel, Message message) {
        this.trace("chatReceived(..)", message);

        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onChatReceived(event));
    }

    private void connectionClosed(ClientSessionChannel channel, Message message) {
        this.trace("connectionClosed(..)", message);
//        final Message msg = new HashMapMessage();
//        msg.put(Chat.USER, "system");
//        msg.put(Chat.CHAT, "Connection to Server Closed");
//        receive(msg);

        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnectionClosed(event));
    }

    private void connectionBroken(ClientSessionChannel channel, Message message){
        this.trace("connectionBroken(..)", message);
//        chatUtil.clearMemberListHtml();

        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnectionBroken(event));
    }

    private void connectionEstablished(ClientSessionChannel channel, Message message){
        this.trace("connectionEstablished(..)", message);
        
        // connection establish (maybe not for first time), so just
        // tell local user and update membership
        final String ch = Chat.MEMBERS_SERVICE_CHANNEL;
        final Message msg = this.getUserRoomMessage(ch);
        channel.getSession().getChannel(ch).publish(msg);
        
        this.chatListenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnectionEstablished(event));
    }
    
    private ChatListener.Event createEvent(ChatSession chatSession,
            ClientSessionChannel channel, Message message) {

        return new ChatEvent(chatSession, channel, message);
    }
    
    private void removeListeners(){
        clientSession.getChannel(Channel.META_HANDSHAKE).removeListener(this.handshakeListener);
        clientSession.getChannel(Channel.META_CONNECT).removeListener(this.connectListener);
        clientSession.getChannel(Channel.META_SUBSCRIBE).removeListener(this.subscribeListener);
        clientSession.getChannel(Channel.META_UNSUBSCRIBE).removeListener(this.unsubscribeListener);
        clientSession.getChannel(Channel.META_DISCONNECT).removeListener(this.disconnectListener);
        clientSession.getChannel(this.chatConfig.getChannel()).removeListener(this.chatListener);
    }
    
    private String getMembersRoom() {
        final String membersRoom = "/members/" + this.chatConfig.getRoom().substring("/chat/".length());
        LOG.debug("Chat room: {}, members room: {}", this.chatConfig.getRoom(), membersRoom);
        return membersRoom;
    }
    
    private Message getUserRoomMessage(String channel) {
        final ServerMessageImpl msg = new ServerMessageImpl();
        msg.setChannel(channel);
        msg.setClientId(clientSession.getId());
        final Message data = new HashMapMessage();
        data.put(Chat.USER, this.chatConfig.getUser());
        data.put(Chat.ROOM, this.chatConfig.getRoom());
        msg.setData(data);
        return msg;
    }
    
    private void update(String ID, ClientSessionChannel csc, Message msg, 
            BiConsumer<ClientSessionChannel, Message> onSuccess){
        update(ID, csc, msg, onSuccess, null);
    }
    private void update(String ID, Message msg, CompletableFuture<Message> future){
        update(ID, null, msg, (channel, message) -> {}, future);
    }
    private void update(String ID, ClientSessionChannel csc, Message msg, CompletableFuture<Message> future){
        update(ID, csc, msg, (channel, message) -> {}, future);
    }
    private void update(String ID, ClientSessionChannel csc, Message msg, 
            BiConsumer<ClientSessionChannel, Message> onSuccess, CompletableFuture<Message> future){
        
        debug(ID, msg);
        
        if(msg.isSuccessful()) {
        
            onSuccess.accept(csc, msg);
            
            if(future != null) {
                LOG.debug("Completing {}, for user: {}", ID, chatConfig.getUser());
                future.complete(msg);
            }
        }else{
            if(future != null) {
                LOG.debug("Cancelling {}, for user: {}", ID, chatConfig.getUser());
                future.completeExceptionally(newCancellationException(ID));
            }
        }
    }
    private void debug(String ID, Message msg) {
        LOG.debug("{} user: {}, success: {}, message: {}", 
                ID, chatConfig.getUser(), msg.isSuccessful(), msg);
    }
    private void trace(String ID, Message msg) {
        LOG.trace("{} user: {}, success: {}, message: {}", 
                ID, chatConfig.getUser(), msg.isSuccessful(), msg);
    }
    
    private CompletableFuture<Message> requireNullThenCreateNew(CompletableFuture<Message> f, String method) {
        if(f != null) {
            throw new IllegalStateException(method + " method may only be called once");
        }else{
            return new CompletableFuture();
        }
    }
    
    private CancellationException newCancellationException(String ID) {
        return new CancellationException("Cancelling: " + ID + 
                "\n" + this.status + "\n" + this.chatConfig);
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
    
    private static final class ChatEvent implements ChatListener.Event{
        
        private final ChatSession chatSession;
        private final ClientSessionChannel channel;
        private final Message message;

        public ChatEvent(ChatSession chatSession, ClientSessionChannel channel, Message message) {
            this.chatSession = Objects.requireNonNull(chatSession);
            this.channel = Objects.requireNonNull(channel);
            this.message = Objects.requireNonNull(message);
        }
        
        @Override
        public ChatSession getSession() {
            return chatSession;
        }
        @Override
        public ClientSessionChannel getChannel() {
            return channel;
        }
        @Override
        public Message getMessage() {
            return message;
        }
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

    @Override
    public String toString() {
        return "ChatSessionImpl{" + "Listeners=" + chatListenerManager.size() + ", clientSession=" + clientSession +  
                "\n" + chatConfig + "\n" + status + "\n}";
    }
}
