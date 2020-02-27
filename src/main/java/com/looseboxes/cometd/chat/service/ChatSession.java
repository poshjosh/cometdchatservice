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

import java.util.concurrent.Future;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;

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
public interface ChatSession {
    
    interface Status{
        boolean isConnected();
        boolean isDisconnecting();
        boolean isSubscribedToChat();
        boolean isSubscribedToMembers();
    }
    
    boolean addListener(ChatListener listener);
    
    boolean removeListener(ChatListener listener);
    
    Future<Message> connect();
    
    Future<Message> disconnect();
    
    Status getStatus();

    /**
     * Handshake with the server. When user logging into your system, you can call this method
     * to connect that user to cometd server.
     * Calls {@link #connect()} then {@link #subscribe()} asynchronously
     * @see #connect() 
     * @see #subscribe() 
     * @return 
     */
    Future<Message> join();

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

    Future<Message> subscribe();

    Future<Message> unsubscribe();
}
