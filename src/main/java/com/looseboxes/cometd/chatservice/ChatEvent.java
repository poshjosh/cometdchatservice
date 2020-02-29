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
package com.looseboxes.cometd.chatservice;

import java.util.Objects;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;

/**
 * @author USER
 */
public final class ChatEvent implements ChatListener.Event{

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
