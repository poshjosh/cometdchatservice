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
package com.looseboxes.cometd.chatservice.services.request;

import com.looseboxes.cometd.chatservice.CometDProperties;
import com.looseboxes.cometd.chatservice.services.response.Response;
import java.util.concurrent.Future;
import org.cometd.bayeux.Message;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.services.ServletUtil;
import com.looseboxes.cometd.chatservice.services.response.MessageResponseBuilder;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author USER
 */
@Service
public class JoinControllerService implements ControllerService{

    private final ServletUtil servletUtil; 
    
    private final MessageResponseBuilder msgResBuilder;
    
    private final CometDProperties cometdProps;

    public JoinControllerService(@Autowired ServletUtil servletUtil,
            @Autowired MessageResponseBuilder msgResBuilder,
            @Autowired CometDProperties cometdProps) {
        this.servletUtil = Objects.requireNonNull(servletUtil);
        this.msgResBuilder = Objects.requireNonNull(msgResBuilder);
        this.cometdProps = Objects.requireNonNull(cometdProps);
    }

    @Override
    public Response process(ControllerService.ServiceContext serviceContext) {
        
        final boolean alreadyJoined = serviceContext.isJoinedToChat();
        
        if(alreadyJoined) {
        
            return msgResBuilder.buildResponse("Already joined chat", null, false);
        }
        
        final ChatSession chatSession = serviceContext.getChatSession();

        final Future<Message> joinFuture = chatSession.join((channel, message) -> {
            //@TODO 
            // Each time a message arrives on this channel, this will be invoked
        });
        
        final long timeout = cometdProps.getJoinTimeout();
       
        final Message message = servletUtil.waitForFuture(joinFuture, timeout);
        
        return msgResBuilder.buildResponse(chatSession, message);
    }
}
