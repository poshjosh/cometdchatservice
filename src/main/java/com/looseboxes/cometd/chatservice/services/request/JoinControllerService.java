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

import com.looseboxes.cometd.chatservice.services.response.Response;
import java.util.concurrent.Future;
import org.cometd.bayeux.Message;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.services.ServletUtil;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author USER
 */
@Service
public class JoinControllerService implements ControllerService{

    private final ServletUtil servletUtil; 
    
    private final Response.Builder responseBuilder;
    
    private final long joinTimeout;

    public JoinControllerService(@Autowired ServletUtil servletUtil,
            @Autowired Response.Builder responseBuilder,
            @Value("${cometd.handshakeTimeout}") long handshakeTimeoutMillis,
            @Value("${cometd.subscriptionTimeout}") long subscriptionTimeoutMillis) {
        this.servletUtil = Objects.requireNonNull(servletUtil);
        this.responseBuilder = Objects.requireNonNull(responseBuilder);
        this.joinTimeout = handshakeTimeoutMillis + subscriptionTimeoutMillis;
    }

    @Override
    public Response process(ControllerService.ServiceContext serviceContext) {
        
        final boolean alreadyJoined = serviceContext.isJoinedToChat();
        
        if(alreadyJoined) {
        
            return getUniqueResponseBuilder()
                    .message("Already joined chat").success(false).build();
        }
        
        final ChatSession chatSession = serviceContext.getChatSession();

        final Future<Message> joinFuture = chatSession.join();
        
        final Message message = servletUtil.waitForFuture(joinFuture, joinTimeout);
        
        return getUniqueResponseBuilder()
                .message(chatSession.getState()).data(message).build();
    }

    public Response.Builder getUniqueResponseBuilder() {
        return responseBuilder.isBuildAttempted() ? 
                responseBuilder.newInstance() : responseBuilder;
    }
}
