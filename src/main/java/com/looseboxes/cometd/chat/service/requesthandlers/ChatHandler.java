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
package com.looseboxes.cometd.chat.service.requesthandlers;

import com.looseboxes.cometd.chat.service.requesthandlers.exceptions.ProcessingRequestException;
import com.looseboxes.cometd.chat.service.ChatPropertyNames;
import com.looseboxes.cometd.chat.service.ClientSessionPublisher;
import com.looseboxes.cometd.chat.service.CometDProperties;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public final class ChatHandler extends AbstractRequestHandler{

    private static final Logger LOG = LoggerFactory.getLogger(ChatHandler.class);
    
    public ChatHandler() { }
    
    @Override
    public Response doProcess(HttpServletRequest req, HttpServletResponse res) {
        
        final WebApplicationContext webAppCtx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(req.getServletContext());

        final JoinHandler jh = webAppCtx.getBean(JoinHandler.class);

        final Response jhr = jh.process(req, res);

        if(!jhr.isSuccess()) {

            return jhr;
        }

        final ServletUtil util = webAppCtx.getBean(ServletUtil.class);
        
        final String sender = util.requireNonNullOrEmpty(req, "chatsender");
        final String recipient = util.requireNonNullOrEmpty(req, "chatrecipient");
        final String chatroom = util.requireNonNullOrEmpty(req, "chatroom");
        final String chatmessage = util.requireNonNullOrEmpty(req, "chatmessage");
        
        final ClientSessionChannel channel = util.getDefaultChatChannel(req.getSession(), null);

        if(channel == null) {

            throw new ProcessingRequestException("Chat channel not found");
        }

//        channel.addListener(
//                (ClientSessionChannel.MessageListener)(ClientSessionChannel sessChannel, Message msg) -> {
//        });

        final Map<String, Object> data = new HashMap<>();
        
        data.put(ChatPropertyNames.CHAT, chatmessage);
        data.put(ChatPropertyNames.PEER, recipient);
        data.put(ChatPropertyNames.ROOM, chatroom);
        data.put(ChatPropertyNames.USER, sender);
        
        final long timeout = webAppCtx.getBean(CometDProperties.class).getPublishTimeout();

        return webAppCtx.getBean(ClientSessionPublisher.class).publish(channel, data, timeout);
    }
}

