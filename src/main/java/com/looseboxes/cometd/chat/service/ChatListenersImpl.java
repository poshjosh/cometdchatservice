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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

/**
 * @author USER
 */
public class ChatListenersImpl implements ChatListeners{
    
    private final List<ChatListener> listeners;
    
    public ChatListenersImpl() {
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }
    
    @Override
    public boolean addListener(ChatListener listener) {
        synchronized(listeners) {
            return this.listeners.add(listener);
        }    
    }
    
    @Override
    public boolean removeListener(ChatListener listener) {
        synchronized(listeners) {
            return this.listeners.remove(listener);
        }
    }

    @Override
    public void fireEvent(ChatSession chatSession, ClientSessionChannel channel, 
            Message message, BiConsumer<ChatListener, ChatListener.Event> action) {

        final ChatListener.Event event = this.createEvent(chatSession, channel, message);

        this.fireEvent(event, action);
    }

    private ChatListener.Event createEvent(ChatSession chatSession,
            ClientSessionChannel channel, Message message) {

        return new ChatEvent(chatSession, channel, message);
    }
    
    @Override
    public void fireEvent(ChatListener.Event event, 
            BiConsumer<ChatListener, ChatListener.Event> action) {
        synchronized(listeners) {
            for(ChatListener listener : listeners) {
                action.accept(listener, event);
            }
        }
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
}
