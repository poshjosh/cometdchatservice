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
package com.looseboxes.cometd.chatservice.handlers.request;

import com.looseboxes.cometd.chatservice.CometDProperties;
import com.looseboxes.cometd.chatservice.handlers.response.Response;
import com.looseboxes.cometd.chatservice.handlers.response.ResponseBuilder;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.handlers.ChatRequestService;

/**
 * @author USER
 */
public class JoinHandler extends AbstractRequestHandler{

    private static final Logger LOG = LoggerFactory.getLogger(JoinHandler.class);

    public JoinHandler() { }
    
    @Override
    public Response doProcess(HttpServletRequest req, HttpServletResponse res) {
        
        final WebApplicationContext webAppCtx = getWebAppContext(req);
        
        final ChatRequestService chatReqSvc = webAppCtx.getBean(ChatRequestService.class);
        
        final boolean alreadyJoined = chatReqSvc.isJoinedToChat(req);
        
        final ResponseBuilder resBuilder = webAppCtx.getBean(ResponseBuilder.class);

        if(alreadyJoined) {
        
            return resBuilder.buildResponse("Already joined chat", null, false);
        }

        final ChatSession chatSession = chatReqSvc.getChatSession(req, true);
        
        final CometDProperties cometdProps = webAppCtx.getBean(CometDProperties.class);

        final Future<Message> joinFuture = chatSession.join();
        
        final long timeout = cometdProps.getJoinTimeout();
        
        return this.awaitFutureThenBuildResponseFromResult(
                "Join-chat", joinFuture, timeout, resBuilder);
    }
}
