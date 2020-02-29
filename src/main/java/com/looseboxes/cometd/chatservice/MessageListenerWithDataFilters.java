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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.filter.DataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

/**
 * @author USER
 * This class fixes issue #002. 
 * {@link org.cometd.server.filter.DataFilterMessageListener DataFilterMessageListener} 
 * was sending only senders name for filtering. At this point we should be 
 * filtering the chat message. This class is thus used in place of 
 * {@link org.cometd.server.filter.DataFilterMessageListener DataFilterMessageListener}
 * to achieve filtering of chat messages.
 */
public final class MessageListenerWithDataFilters implements ServerChannel.MessageListener {

    private final Logger _LOG = LoggerFactory.getLogger(MessageListenerWithDataFilters.class);

    private static class MessageChatExtractor implements Function<ServerMessage.Mutable, Object>{
        @Override
        public Object apply(ServerMessage.Mutable message) {
            return message.get(Chat.CHAT);
        }
    }

    @Nullable private final BayeuxServer bayeux;

    private final Function<ServerMessage.Mutable, Object> format;

    private final List<DataFilter> extractor;

    public MessageListenerWithDataFilters(DataFilter... filters) {
        this(new MessageChatExtractor(), filters);
    }

    public MessageListenerWithDataFilters(Function<ServerMessage.Mutable, Object> extractor, DataFilter... filters) {
        this(null, extractor, filters);
    }

    public MessageListenerWithDataFilters(@Nullable BayeuxServer bayeux, 
            Function<ServerMessage.Mutable, Object> extractor, DataFilter... filters) {
        this.bayeux = bayeux;
        this.format = Objects.requireNonNull(extractor);
        this.extractor = Arrays.asList(filters);
    }

    @Override
    public boolean onMessage(ServerSession from, 
            ServerChannel channel, ServerMessage.Mutable message) {
        try {

            Object data = this.format.apply(message);

            if(_LOG.isTraceEnabled()) {
                _LOG.trace("Extracted: {} from message: {}" + data, message);
            }

            final Object orig = data;
            for (DataFilter filter : this.extractor) {
                data = filter.filter(from, channel, data);
                if (data == null) {
                    return false;
                }
            }

            if (data != orig) {
                message.setData(data);
            }

            return true;

        } catch (DataFilter.AbortException a) {
            if (_LOG.isDebugEnabled()) {
                _LOG.debug("Rejected by DataFilter, message: " + message, a);
            }
            return false;
        }
    }
}
