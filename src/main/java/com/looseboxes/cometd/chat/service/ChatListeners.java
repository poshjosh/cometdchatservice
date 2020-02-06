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

import java.util.function.BiConsumer;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

/**
 * @author USER
 */
public interface ChatListeners {

    boolean addListener(ChatListener listener);

    void fireEvent(ChatSession chatSession, ClientSessionChannel channel, 
            Message message, BiConsumer<ChatListener, ChatListener.Event> action);

    void fireEvent(ChatListener.Event event, BiConsumer<ChatListener, ChatListener.Event> action);

    boolean removeListener(ChatListener listener);
}
