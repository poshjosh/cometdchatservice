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

import java.util.concurrent.Future;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;

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
public interface ChatSession {
    
    interface State{
        boolean isConnected();
        boolean isConnecting();
        boolean isDisconnecting();
        boolean isSubscribing();
        boolean isUnsubscribing();
        boolean isSubscribed();
    }
    
    default ChatListenerManager listeners() {
        return this.getListenerManager();
    }
    
    ChatListenerManager getListenerManager();
    
    Future<Message> connect();
    
    /**
     * Un-subscribe before disconnecting.
     * <p>{@link #leave()} Calls {@link #disconnect()} then {@link #unsubscribe()}</p>
     * @return 
     */
    Future<Message> disconnect();
    
    State getState();

    /**
     * Handshake with the server. When user logging into your system, you can call this method
     * to connect that user to cometd server.
     * Calls {@link #connect()} then 
     * {@link #subscribe(ClientSessionChannel.MessageListener) subscribe(.)} asynchronously
     * @see #join(org.cometd.bayeux.client.ClientSessionChannel.MessageListener) 
     * @see #connect() 
     * @see #subscribe() 
     * @return 
     */
    default Future<Message> join() {
        return join((channel, message) -> {});
    }
    
    /**
     * Handshake with the server. When user logging into your system, you can call this method
     * to connect that user to cometd server.
     * Calls {@link #connect()} then 
     * {@link #subscribe(ClientSessionChannel.MessageListener) subscribe(.)} asynchronously
     * @param listener Used to listen for messages on this channel
     * @see #join() 
     * @see #connect() 
     * @see #subscribe() 
     * @return 
     */
    Future<Message> join(ClientSessionChannel.MessageListener listener);

    /**
     * This method can be invoked to disconnect from the chat server.
     * When user logging off or user close the browser window, user should
     * be disconnected from cometd server.
     * Calls {@link #unsubscribe()} then {@link #disconnect()} asynchronously
     * @see #unsubscribe() 
     * @see #disconnect() 
     * @return 
     */
    Future<Message> leave();
    
    /**
     * Send the text message to peer.
     * @param textMessage The message to send. Null or empty values not allowed
     * @param toUser
     * @return 
     */
    Future<Message> send(String textMessage, String toUser);
    
    /**
     * Send the text message to peer.
     * @param textMessage The message to send. Null or empty values not allowed
     * @param toUser
     * @param messageListener
     */
    void send(String textMessage, String toUser, ClientSession.MessageListener messageListener);
    
    /**
     * Connect before subscribing. 
     * <p>{@link #join()} Calls {@link #connect()} then 
     * {@link #subscribe(ClientSessionChannel.MessageListener) subscrible(.)}</p>
     * @return 
     * @see #subscribe(org.cometd.bayeux.client.ClientSessionChannel.MessageListener) 
     */
    default Future<Message> subscribe() {
        return subscribe((channel, message) -> {});
    }

    /**
     * Connect before subscribing. 
     * <p>{@link #join()} Calls {@link #connect()} then 
     * {@link #subscribe(ClientSessionChannel.MessageListener) subscrible(.)}</p>
     * @param listener Used to listen for messages on this channel
     * @return 
     * @see #subscribe() 
     */
    Future<Message> subscribe(ClientSessionChannel.MessageListener listener);

    Future<Message> unsubscribe();
}
