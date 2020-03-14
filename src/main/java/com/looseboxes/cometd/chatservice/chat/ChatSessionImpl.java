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
package com.looseboxes.cometd.chatservice.chat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * Connect before subscribing. Un-subscribe before disconnecting.
 * <p>{@link #join()} Calls {@link #connect()} then {@link #subscribe()}</p>
 * <p>{@link #leave()} Calls {@link #disconnect()} then {@link #unsubscribe()}</p>
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
    private final StateBean state;

    private CompletableFuture<Message> handshakeFuture;
    private CompletableFuture<Message> disconnectFuture;
    
    private final ClientSessionChannel.MessageListener handshakeListener;
    private final ClientSessionChannel.MessageListener connectListener;
    private final ClientSessionChannel.MessageListener subscribeListener;
    private final ClientSessionChannel.MessageListener unsubscribeListener;
    private final ClientSessionChannel.MessageListener disconnectListener;
    
    private final ClientSessionChannel.MessageListener chatListener;

    private final ChatListenerManager listenerManager;
    
    public ChatSessionImpl(ClientSession client, ChatConfig chatConfig) {
    
        this.clientSession = Objects.requireNonNull(client);
        this.requireNonNullOrEmpty(chatConfig.getChannel());
        this.requireNonNullOrEmpty(chatConfig.getRoom());
        this.requireNonNullOrEmpty(chatConfig.getUser());
        this.chatConfig = chatConfig.forUser(chatConfig.getUser());
        this.state = new StateBean();
        
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

        this.listenerManager = new ChatListenerManagerImpl();
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
    
            this.debug("send(..)",  msg);
            
            future.complete(msg);
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

        LOG.debug("send(\"{}\", \"{}\") sender: {}", 
                textMessage, peerUserName, chatConfig.getUser());

        requireNonNullOrEmpty(textMessage);
        requireNonNullOrEmpty(peerUserName);

        final Message msg = new HashMapMessage();
        msg.put(Chat.ROOM, this.chatConfig.getRoom());
        msg.put(Chat.USER, this.chatConfig.getUser());
        msg.put(Chat.CHAT, textMessage);
        msg.put(Chat.PEER, peerUserName);
        
        clientSession.getChannel(this.chatConfig.getChannel())
                .publish(msg, messageListener);
    }
    
    /**
     * Handshake with the server. When user logging into your system, you can 
     * call this method
     * to connect that user to cometd server.
     * Calls {@link #connect()} then 
     * {@link #subscribe(org.cometd.bayeux.client.ClientSessionChannel.MessageListener) subscribe(.)} asynchronously
     * @param listener
     * @see #connect() 
     * @see #subscribe(org.cometd.bayeux.client.ClientSessionChannel.MessageListener) 
     * @return 
     */
    @Override
    public Future<Message> join(ClientSessionChannel.MessageListener listener) {
        
        LOG.debug("join(), user: {}", chatConfig.getUser());
        
        return this.connect().thenComposeAsync(msg -> {
            
            debug("join()", msg);
            
            if(msg.isSuccessful()) {
                subscribe(listener);
            }
            
            return CompletableFuture.completedFuture(msg);
        });
    }
    
    @Override
    public CompletableFuture<Message> connect() {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("connect(), user: {}", u);
        
        this.handshakeFuture = this.requireNullThenCreateNew(this.handshakeFuture, "connect()");
        
        final Map<String, Object> template = new HashMap<>();
        template.put(Chat.WEBSOCKET_ENABLED, this.chatConfig.isWebsocketEnabled());
        template.put(Chat.LOG_LEVEL, this.chatConfig.getLogLevel());

        this.clientSession.handshake(template, (Message msg) -> {

            this.debug("connect()", msg);

            this.state.onConnectResponse(msg.isSuccessful());

            this.handshakeFuture.complete(msg);
        });
        
        this.state.setConnecting(true);

        return handshakeFuture;
    }
    
    /**
     * Connect before subscribing. 
     * <p>{@link #join()} Calls {@link #connect()} then 
     * {@link #subscribe(org.cometd.bayeux.client.ClientSessionChannel.MessageListener) subscribe(.)}</p>
     * @param listener
     * @return 
     */
    @Override
    public Future<Message> subscribe(ClientSessionChannel.MessageListener listener) {
        LOG.debug("subscribe(), user: {}", chatConfig.getUser());

        if( ! this.state.isConnected()) {
            throw new IllegalStateException("Not connected");
        }
        
        final CompletableFuture<Message> future = new CompletableFuture<>();

        try{
            
            clientSession.startBatch();

            clientSession.batch(() -> {

                subscribe(chatConfig.getChannel(), listener, future);
            });
        }finally{
        
            clientSession.endBatch();
        }
        return future;
    }
    
    private void subscribe(String channel, 
            ClientSessionChannel.MessageListener listener, 
            CompletableFuture<Message> future) {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("[{}] Subscribing to channel: {}", u, channel);
        
        final ClientSessionChannel channelObj = clientSession.getChannel(channel);
        
        final Consumer<Message> callback = (msg) -> {
            debug("subscribe()", msg);
            this.state.onSubscribeResponse(msg.isSuccessful());
            future.complete(msg);
        };
        
        channelObj.subscribe(
            listener, 
            (msg) -> { 
                callback.accept(msg);
            }
        );
        
        this.state.setSubscribing(true);
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
    public Future<Message> unsubscribe() {
        LOG.debug("unsubscribe(), user: {}", chatConfig.getUser());
        
        final CompletableFuture<Message> future = new CompletableFuture<>();

        try{
            
            clientSession.startBatch();

            clientSession.batch(() -> {

                unsubscribe(chatConfig.getChannel(), future);
            });
        }finally{
        
            clientSession.endBatch();
        }
        
        return future;
    }
    
    private void unsubscribe(String channel, CompletableFuture<Message> future) {
        
        final String u = chatConfig.getUser();
        
        LOG.debug("[{}] Unsubscribing from channel: {}", u, channel);
        
        final ClientSessionChannel channelObj = clientSession.getChannel(channel);
        
        final ClientSession.MessageListener callback = (msg) -> {
            debug("unsubscribe()", msg);
            this.state.onUnsubscribeResponse(msg.isSuccessful());
            future.complete(msg);
        };

        final List<ClientSessionChannel.MessageListener> subscribers = 
                channelObj.getSubscribers();
        if(subscribers == null || subscribers.isEmpty()) {
            final HashMapMessage msg = new HashMapMessage();
            msg.put("id", Long.toHexString(System.currentTimeMillis()));
            msg.put("clientId", this.clientSession.getId());
            msg.put("meta", true);
            msg.put("successful", true);
            msg.put("empty", true);
            msg.put("publishReply", false);
            callback.onMessage(msg);
        }else{
            final Iterator<ClientSessionChannel.MessageListener> iter =  
                    subscribers.iterator();
            while(iter.hasNext()) {
                final boolean last = ! iter.hasNext();
                final ClientSession.MessageListener listener = last ? 
                        callback : ClientSession.MessageListener.NOOP;
                channelObj.unsubscribe(iter.next(), listener);
            }
        }
        
        this.state.setUnsubscribing(true);
    }
    
    /**
     * Un-subscribe before disconnecting.
     * <p>{@link #leave()} Calls {@link #disconnect()} then {@link #unsubscribe()}</p>
     * @return 
     */
    @Override
    public Future<Message> disconnect() {
        LOG.debug("disconnect(), user: {}", chatConfig.getUser());

        this.disconnectFuture = this.requireNullThenCreateNew(this.disconnectFuture, "disconnect()");
  
        // Even when a user calls unsubscribe, it is no guarantee the unsubscription
        // or even the process of unsubscription has begun. Hence we can't do this.
//        if(this.state.isSubscribed()) {
//            throw new IllegalStateException(
//                    "Current subscribed to chat. Method disconnect() may only be called when un-subscribed");
//        }

        this.clientSession.disconnect((Message msg) -> {
            
            debug("disconnect()", msg);
            
            this.state.onDisconnectResponse(msg.isSuccessful());

            this.disconnectFuture.complete(msg);
        });

        this.state.setDisconnecting(true);

        return disconnectFuture;
    }
    
    private void metaHandshake(ClientSessionChannel channel, Message message) {
        
        this.debug("metaHandshake(..)", message); 
        
        handshakeFuture.complete(message);

        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onHandshake(event));
    }
    
    private void metaConnect(ClientSessionChannel channel, Message message) {
         
        this.trace("metaConnect(..)", message);

        this.state.setConnecting(false);
        
        this.handshakeFuture.complete(message);

        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnect(event));

        if (this.state.isDisconnecting()) {
            this.state.setConnected(false);
            this.connectionClosed(channel, message);
        } else {
            final boolean wasConnected = this.state.isConnected();
            this.state.setConnected(message.isSuccessful());
            if (!wasConnected && this.state.isConnected()) {
                this.connectionEstablished(channel, message);
            } else if (wasConnected && !this.state.isConnected()) {
                this.connectionBroken(channel, message);
            }
        }
    }
    
    private void metaSubscribe(ClientSessionChannel channel, Message message) {
        
        this.debug("metaSubscribe(..)", message);

        this.state.onSubscribeResponse(message.isSuccessful());
        
        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onSubscribe(event));
    }

    private void metaUnsubscribe(ClientSessionChannel channel, Message message) {
        
        this.debug("metaUnsubscribe(..)", message);

        this.state.onUnsubscribeResponse(message.isSuccessful());

        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onUnsubscribe(event));
    }
    
    private boolean isChannelMessage(String channel, Message msg) {
        final String key = "subscription";
        return channel.equals(msg.get(key));
    }

    private void metaDisconnect(ClientSessionChannel channel, Message message) {
        
        this.debug("metaDisconnect(..)", message);

        this.state.onDisconnectResponse(message.isSuccessful());

        this.disconnectFuture.complete(message);

        this.removeListeners();
        
        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onDisconnect(event));
    }
    
    private void chatReceived(ClientSessionChannel channel, Message message) {
        this.trace("chatReceived(..)", message);

        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onChatReceived(event));
    }

    private void connectionClosed(ClientSessionChannel channel, Message message) {
        this.trace("connectionClosed(..)", message);
//        final Message msg = new HashMapMessage();
//        msg.put(Chat.USER, "system");
//        msg.put(Chat.CHAT, "Connection to Server Closed");
//        receive(msg);

        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnectionClosed(event));
    }

    private void connectionBroken(ClientSessionChannel channel, Message message){
        this.trace("connectionBroken(..)", message);
//        chatUtil.clearMemberListHtml();

        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
                (listener, event) -> listener.onConnectionBroken(event));
    }

    private void connectionEstablished(ClientSessionChannel channel, Message message){
        this.trace("connectionEstablished(..)", message);
        
        // connection establish (maybe not for first time), so just
        // tell local user and update membership
        final String ch = Chat.MEMBERS_SERVICE_CHANNEL;
        final Message msg = this.getUserRoomMessage(ch);
        channel.getSession().getChannel(ch).publish(msg);
        
        this.listenerManager.fireEvent(this.createEvent(this, channel, message), 
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
    public State getState() {
        return this.state;
    }

    @Override
    public ChatListenerManager getListenerManager() {
        return listenerManager;
    }
    
    private static final class StateBean implements State, Serializable{
        private final AtomicBoolean connected = new AtomicBoolean(false);
        private final AtomicBoolean connecting = new AtomicBoolean(false);
        private final AtomicBoolean disconnecting = new AtomicBoolean(false);
        private final AtomicBoolean subscribed = new AtomicBoolean(false);
        private final AtomicBoolean subscribing = new AtomicBoolean(false);
        private final AtomicBoolean unsubscribing = new AtomicBoolean(false);
        private StateBean() { }
        public void onConnectResponse(boolean success){
            set(connecting, false);
            set(connected, success);
        }
        public void onDisconnectResponse(boolean success){
            set(disconnecting, false);
            set(connected, ! success);
        }
        public void onSubscribeResponse(boolean success){
            set(subscribing, false);
            set(subscribed, success);
        }
        public void onUnsubscribeResponse(boolean success){
            set(unsubscribing, false);
            set(subscribed, ! success);
        }
        @Override
        public boolean isConnected() {
            return connected.get();
        }
        public void setConnected(boolean flag) {
            set(connected, flag);
        }
        @Override
        public boolean isConnecting() {
            return connecting.get();
        }
        public void setConnecting(boolean flag) {
            set(connecting, flag);
        }
        @Override
        public boolean isDisconnecting() {
            return disconnecting.get();
        }
        public void setDisconnecting(boolean flag) {
            set(disconnecting, flag);
        }
        @Override
        public boolean isSubscribed() {
            return subscribed.get();
        }
        public void setSubscribied(boolean flag) {
            set(subscribed, flag);
        }
        @Override
        public boolean isSubscribing() {
            return subscribing.get();
        }
        public void setSubscribing(boolean flag) {
            set(subscribing, flag);
        }
        @Override
        public boolean isUnsubscribing() {
            return unsubscribing.get();
        }
        public void setUnsubscribing(boolean flag) {
            set(unsubscribing, flag);
        }
        private void set(AtomicBoolean atomic, boolean flag) {
            atomic.compareAndSet(!flag, flag);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 13 * hash + Objects.hashCode(this.connected);
            hash = 13 * hash + Objects.hashCode(this.connecting);
            hash = 13 * hash + Objects.hashCode(this.disconnecting);
            hash = 13 * hash + Objects.hashCode(this.subscribed);
            hash = 13 * hash + Objects.hashCode(this.subscribing);
            hash = 13 * hash + Objects.hashCode(this.unsubscribing);
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
            final StateBean other = (StateBean) obj;
            if (!Objects.equals(this.connected, other.connected)) {
                return false;
            }
            if (!Objects.equals(this.connecting, other.connecting)) {
                return false;
            }
            if (!Objects.equals(this.disconnecting, other.disconnecting)) {
                return false;
            }
            if (!Objects.equals(this.subscribed, other.subscribed)) {
                return false;
            }
            if (!Objects.equals(this.subscribing, other.subscribing)) {
                return false;
            }
            if (!Objects.equals(this.unsubscribing, other.unsubscribing)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ServiceStatus{" + "connected=" + connected + 
                    ", connecting=" + connecting + 
                    ", disconnecting=" + disconnecting + 
                    ", subscribed=" + subscribed + 
                    ", subscribing=" + subscribing + 
                    ", unsubscribing=" + unsubscribing +'}';
        }
    }

    @Override
    public String toString() {
        return "ChatSessionImpl{" + "Listeners=" + listenerManager.size() + 
                ", clientSession=" + clientSession +  
                "\n" + chatConfig + "\n" + state + "\n}";
    }
}
