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

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

/**
 *
 * @author USER
 */
public interface ChatListener {

    interface Event{
        ChatSession getSession();
        ClientSessionChannel getChannel();
        Message getMessage();
    }

    default void onHandshake(ChatListener.Event event){}
    default void onConnect(ChatListener.Event event){}
    default void onDisconnect(ChatListener.Event event){}
    default void onSubscribe(ChatListener.Event event){}
    default void onUnsubscribe(ChatListener.Event event){}
    default void onConnectionClosed(ChatListener.Event event){}
    default void onConnectionBroken(ChatListener.Event event){}
    default void onConnectionEstablished(ChatListener.Event event){}
}
