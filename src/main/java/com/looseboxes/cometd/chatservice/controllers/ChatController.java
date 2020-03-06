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

import com.looseboxes.cometd.chatservice.services.ChatControllerService;
import com.looseboxes.cometd.chatservice.services.response.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.looseboxes.cometd.chatservice.services.ControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextProvider;
import java.util.Objects;

/**
 * @author USER
 */
@RestController
public class ChatController {
    
    private final ControllerServiceContextProvider serviceContextProvider;
    private final ControllerService controllerService;

    public ChatController(
            @Autowired ControllerServiceContextProvider serviceContextProvider, 
            @Autowired ChatControllerService controllerService) {
        this.serviceContextProvider = Objects.requireNonNull(serviceContextProvider);
        this.controllerService = Objects.requireNonNull(controllerService);
    }

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
    public ResponseEntity chat(HttpServletRequest req, HttpServletResponse res) {
        
        final ControllerService.ServiceContext serviceContext = 
                this.serviceContextProvider.from(req);
        
        final Response response = this.controllerService.process(serviceContext);
        
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
