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

import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.handlers.response.Response;
import com.looseboxes.cometd.chatservice.handlers.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import com.looseboxes.cometd.chatservice.ChatSession;
import com.looseboxes.cometd.chatservice.handlers.ChatRequestService;
import com.looseboxes.cometd.chatservice.handlers.response.ResponseBuilder;
import java.util.concurrent.Future;
import org.cometd.bayeux.Message;

/**
 * @author USER
 */
public final class ChatHandler extends AbstractRequestHandler{

    private static final Logger LOG = LoggerFactory.getLogger(ChatHandler.class);
    
    public ChatHandler() { }
    
    @Override
    public Response doProcess(HttpServletRequest req, HttpServletResponse res) {
        
        final WebApplicationContext webAppCtx = getWebAppContext(req);

        final ChatRequestService chatReqSvc = webAppCtx.getBean(ChatRequestService.class);
        
        final Response joinResponse = chatReqSvc.joinChatIfNotAlready(req, res);
        
        if( ! joinResponse.isSuccess()) {
        
            return joinResponse;
        }

        final ServletUtil util = webAppCtx.getBean(ServletUtil.class);
        
        final String peer = util.requireNonNullOrEmpty(req, ParamNames.PEER);
        final String chat = util.requireNonNullOrEmpty(req, ParamNames.CHAT);
         String param = req.getParameter(ParamNames.ASYNC);
        final boolean async = param == null || param.isEmpty() ? true : Boolean.parseBoolean(param);
        
        final ChatSession chatSession = chatReqSvc.getChatSession(req, false);

        final ResponseBuilder resBuilder = webAppCtx.getBean(ResponseBuilder.class);
        
        final Response response;
        
        if(async) {
            
            chatSession.send(chat, peer);
            
            response = resBuilder.buildSuccessResponse();
            
        }else{
            
            final Future<Message> chatFuture = chatSession.send(chat, peer);

            param = req.getParameter(ParamNames.TIMEOUT);
            final long timeout = param == null || param.isEmpty() ? 
                    Long.MAX_VALUE : Long.parseLong(param);

            response = this.awaitFutureThenBuildResponseFromResult(
                    "Send-chat", chatFuture, timeout, resBuilder);
        }
        
        return response;
    }
}

