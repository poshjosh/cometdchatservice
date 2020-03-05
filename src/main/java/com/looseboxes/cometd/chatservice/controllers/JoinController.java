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

import com.looseboxes.cometd.chatservice.services.request.ControllerService;
import com.looseboxes.cometd.chatservice.services.request.ControllerServiceContextProvider;
import com.looseboxes.cometd.chatservice.services.response.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.looseboxes.cometd.chatservice.services.request.JoinControllerService;
import java.util.Objects;

/**
 * @author USER
 */
@RestController
public class JoinController {
    
    private final ControllerServiceContextProvider serviceContextProvider;
    
    private final ControllerService controllerService;

    public JoinController(
            @Autowired ControllerServiceContextProvider serviceContextProvider, 
            @Autowired JoinControllerService controllerService) {
        this.serviceContextProvider = Objects.requireNonNull(serviceContextProvider);
        this.controllerService = Objects.requireNonNull(controllerService);
    }

    @RequestMapping(Endpoints.JOIN)
    public ResponseEntity join(HttpServletRequest req, HttpServletResponse res) {
        
        final ControllerService.ServiceContext serviceContext = 
                this.serviceContextProvider.from(req);
        
        final Response response = this.controllerService.process(serviceContext);
        
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
