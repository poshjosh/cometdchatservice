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

import com.looseboxes.cometd.chatservice.chat.ChatServerOptionNames;
import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.services.response.Response;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.cometd.bayeux.server.BayeuxServer;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author USER
 */
@Service
public class MembersControllerService implements ControllerService{

    private final Response.Builder responseBuilder;

    public MembersControllerService(@Autowired Response.Builder msgResBuilder) {
        this.responseBuilder = Objects.requireNonNull(msgResBuilder);
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

        final boolean success;
        final String message;
        final Map outputData;
        
        if(serviceContext.isJoinedToChat()) {
        
            final Map<String, Object> params = serviceContext.getParameters();
            
            final String room = (String)params.get(ParamNames.ROOM);

            final Map roomMembers = this.getMembers(
                    serviceContext.getBayeuxServer(), room);

            success = true;
            outputData = Collections.singletonMap(Endpoints.MEMBERS.substring(1), roomMembers);
            message = "success";
            
        }else{
        
            success = false;
            message = "You are not a member of any chat rooms";
            outputData = null;
        }

        return getUniqueResponseBuilder()
                .message(message).data(outputData).success(success).build();
    }
    
    private Map getMembers(BayeuxServer bayeuxServer, String room) {
        
        final com.looseboxes.cometd.chatservice.chat.MembersService membersService = 
                (com.looseboxes.cometd.chatservice.chat.MembersService)bayeuxServer
                .getOption(ChatServerOptionNames.MEMBERS_SERVICE);
        Objects.requireNonNull(membersService);
        
        final Map result;
       
        if(room == null || room.isEmpty()) {
            result = membersService.getMembers();
        }else{
            final Map roomMembers = membersService.getMembers(room);
            result = Collections.singletonMap(room, roomMembers);
        }
        
        return result;
    }
    
    public Response.Builder getUniqueResponseBuilder() {
        return responseBuilder.isBuildAttempted() ? 
                responseBuilder.newInstance() : responseBuilder;
    }
}
