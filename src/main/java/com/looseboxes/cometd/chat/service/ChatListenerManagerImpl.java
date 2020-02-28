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

/**
 * @author USER
 */
public class ChatListenerManagerImpl implements ChatListenerManager{
    
    private final List<ChatListener> listeners;
    
    public ChatListenerManagerImpl() {
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public boolean containsListener(ChatListener listener) {
        Objects.requireNonNull(listener);
        return listeners.contains(listener);
    }

    @Override
    public int size() {
        return listeners.size();
    }
    
    @Override
    public boolean addListener(ChatListener listener) {
        Objects.requireNonNull(listener);
        synchronized(listeners) {
            return this.listeners.add(listener);
        }    
    }
    
    @Override
    public boolean removeListener(ChatListener listener) {
        Objects.requireNonNull(listener);
        synchronized(listeners) {
            return this.listeners.remove(listener);
        }
    }
    
    @Override
    public void fireEvent(ChatListener.Event event, EventHandler eventHandler) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(eventHandler);
        synchronized(listeners) {
            for(ChatListener listener : listeners) {
                eventHandler.accept(listener, event);
            }
        }
    }
}
