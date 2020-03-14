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

import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class MembersControllerServiceTest extends AbstractControllerServiceTest{
    
    @Test
    @DisplayName("When method process is called with invalid argument, return error")
    public void proces_whenInvalidArg_shouldReturnError() {
        this.process_whenArgumentGiven_shouldReturn(
                getInvalidArgument(), greaterThanOrEqualTo(300), false);
    }
    
    @Override
    public String getEndpoint() {
        return Endpoints.MEMBERS;
    }
    
    @Override
    public ControllerService getControllerService() {
        return this.getTestConfig().membersControllerService();
    }
}

