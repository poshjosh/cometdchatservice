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
package com.looseboxes.cometd.chatservice.services;

import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.services.response.Response;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author USER
 */
@Service
public class ChatControllerService implements ControllerService{

    private final JoinControllerService joinService;
    
    private final ServletUtil servletUtil; 
    
    private final Response.Builder responseBuilder;

    public ChatControllerService(
            @Autowired JoinControllerService joinService,
            @Autowired ServletUtil servletUtil,
            @Autowired Response.Builder responseBuilder) {
        this.joinService = Objects.requireNonNull(joinService);
        this.servletUtil = Objects.requireNonNull(servletUtil);
        this.responseBuilder = Objects.requireNonNull(responseBuilder);
    }

    @Override
    public Response process(ControllerService.ServiceContext serviceContext) {
        Objects.requireNonNull(serviceContext);
        try{
            return this.doProcess(serviceContext);
        }catch(RuntimeException e) {
            return this.getUniqueResponseBuilder()
                    .data(e).error(true).message("Error").build();
        }
    }

    protected Response doProcess(ControllerService.ServiceContext serviceContext) {

        final Response joinResponse = joinChatIfNotAlready(serviceContext);
        
        if( ! joinResponse.isSuccess()) {
        
            return joinResponse;
        }
        
        final Map<String, Object> params = serviceContext.getParameters();

        final String peer = (String)serviceContext
                .requireNonNullOrEmptyParameter(ParamNames.PEER);
        
        final String chat = (String)serviceContext
                .requireNonNullOrEmptyParameter(ParamNames.CHAT);
        
        String param = (String)params.get(ParamNames.ASYNC);
        final boolean async = param == null || param.isEmpty() ? true : Boolean.parseBoolean(param);
        
        final ChatSession chatSession = serviceContext.getChatSession();
        
        final Response response;
        
        if(async) {
            
            chatSession.send(chat, peer);
            
            response = this.buildSuccessResponse();
            
        }else{
            
            final Future<Message> chatFuture = chatSession.send(chat, peer);

            param = (String)params.get(ParamNames.TIMEOUT);
            final long timeout = param == null || param.isEmpty() ? 
                    Long.MAX_VALUE : Long.parseLong(param);

            final Message message = servletUtil.waitForFuture(chatFuture, timeout);
            
            response = getUniqueResponseBuilder()
                    .message(chatSession.getState()).data(message).build();
        }
        
        return response;
    }

    /**
     * @param serviceContext
     * @return {@link com.looseboxes.cometd.chatservice.services.response.Response Response} 
     * object with success set to true if previously joined to chat or
     * successfully joined to chat during this methods execution, otherwise 
     * return false.
     */
    public Response joinChatIfNotAlready(ControllerService.ServiceContext serviceContext) {
        
        final boolean joinedToChat = serviceContext.isJoinedToChat();
        
        if( ! joinedToChat) {
        
            final Response jhr = joinService.process(serviceContext);

            if(!jhr.isSuccess()) {

                return jhr;
            }
        }
        
        return buildSuccessResponse();
    }
    
    public Response buildSuccessResponse(){
        return getUniqueResponseBuilder()
                .code(HttpServletResponse.SC_OK)
                .message("Success").build();
    }

    
    public Response.Builder getUniqueResponseBuilder() {
        return responseBuilder.isBuildAttempted() ? 
                responseBuilder.newInstance() : responseBuilder;
    }
}

