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
package com.looseboxes.cometd.chat.service.handlers.request;

import com.looseboxes.cometd.chat.service.AttributeNames;
import com.looseboxes.cometd.chat.service.ParamNames;
import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import com.looseboxes.cometd.chat.service.handlers.ChatRequestService;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author USER
 */
public final class MembersHandler extends AbstractRequestHandler{

    private static final Logger LOG = LoggerFactory.getLogger(MembersHandler.class);

    public MembersHandler() { }
    
    @Override
    public Response doProcess(HttpServletRequest req, HttpServletResponse res) {

        final boolean error;
        final String message;
        final Map outputData;
        
        final WebApplicationContext webAppCtx = this.getWebAppContext(req);
        
        final ChatRequestService svc = webAppCtx.getBean(ChatRequestService.class);
        
        if(svc.isJoinedToChat(req)) {
        
            final Map roomMembers = this.getRoomMembers(req);

            error = false;
            outputData = Collections.singletonMap(Endpoints.MEMBERS.substring(1), roomMembers);
            message = "success";
            
        }else{
        
            error = true;
            message = "You are not a member of any chat rooms";
            outputData = Collections.EMPTY_MAP;
        }

        return webAppCtx.getBean(ResponseBuilder.class).buildResponse(message, outputData, error);
    }
    
    private Map getRoomMembers(HttpServletRequest req) {
        
        final Map members = (Map)req.getSession().getAttribute(AttributeNames.Session.CHAT_MEMBERS);

        final String room = req.getParameter(ParamNames.ROOM);
        
        final Map roomMembers;

        if(members == null || members.isEmpty()) {
            roomMembers = Collections.EMPTY_MAP;
        }else{
            roomMembers = this.getRoomMembers(members, room);
        }
        
        LOG.debug("{}\nRoom: {}, room members: {}", members, room, roomMembers);
        
        return roomMembers;
    }

    private Map getRoomMembers(Map members, String room) {
        Objects.requireNonNull(members);
        
        final Map roomMembers;

        if(room == null || room.isEmpty()) {
            roomMembers = members;
        }else{
            final Map rmMembers = (Map)members.get(room);
            if(rmMembers == null || rmMembers.isEmpty()) {
                roomMembers = Collections.EMPTY_MAP;
            }else{
                roomMembers = new HashMap();
                roomMembers.put(room, rmMembers);
            }
        }
        
        return roomMembers;
    }
}
