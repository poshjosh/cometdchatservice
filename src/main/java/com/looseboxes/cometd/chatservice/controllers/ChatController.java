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
package com.looseboxes.cometd.chatservice.controllers;

import com.looseboxes.cometd.chatservice.handlers.request.RequestHandler;
import com.looseboxes.cometd.chatservice.handlers.request.RequestHandlerQualifiers;
import com.looseboxes.cometd.chatservice.handlers.response.Response;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author USER
 */
@RestController
public class ChatController {
    
    @Qualifier(RequestHandlerQualifiers.CHAT_HANDLER)
    @Autowired private RequestHandler<Response> requestHandler;
    
    /**
     * <p>Send a chat message to a specified chat user</p>
     * May trigger a call to <code>/join</code> endpoint, if the user has not
     * previously called that endpoint to join chat. In which case. the parameters
     * required for joining chat would be expected. 
     * @param req
     * @param res
     * @return 
     */
    @RequestMapping(Endpoints.CHAT)
    public ResponseEntity chat(ServletRequest req, ServletResponse res) {
        
        final Response response = requestHandler.process(req, res);
        
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
/**
 * 
        @RequestParam(value=ParamNames.USER, required=true) final String user,
        @RequestParam(value=ParamNames.PEER, required=true) final String peer,
        @RequestParam(value=ParamNames.ROOM, required=false) final String room,
        @RequestParam(value=ParamNames.CHAT, required=false) final String chat,
        @RequestParam(value=ParamNames.USER, required=false) final String scope
 * 
 */
