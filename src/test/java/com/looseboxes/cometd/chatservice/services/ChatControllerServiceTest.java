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

import com.looseboxes.cometd.chatservice.services.JoinControllerService;
import com.looseboxes.cometd.chatservice.services.ChatControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerService;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.services.ServletUtil;
import com.looseboxes.cometd.chatservice.services.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ChatControllerServiceTest extends AbstractControllerServiceTest{

    @Test
    @DisplayName("When method process is called with invalid argument, throw RuntimeException")
    public void proces_whenInvalidArg_shouldThrowRuntimeException() {
        this.process_whenArgumentGiven_shouldThrowRuntimeException(
                this.getInvalidArgument());
    }
    
    @Override
    public String getEndpoint() {
        return Endpoints.CHAT;
    }
    
    @Override
    public ControllerService getControllerService() {

        final ServletUtil servletUtil = this.getServletUtil();

        return new ChatControllerService(
                new JoinControllerService(servletUtil, getResponseBuilder(), 3_000, 7_000), 
                servletUtil,
                getResponseBuilder()
        );
    }
}
