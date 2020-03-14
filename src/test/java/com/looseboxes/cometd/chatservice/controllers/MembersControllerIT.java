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

import com.looseboxes.cometd.chatservice.services.ControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextProvider;
import com.looseboxes.cometd.chatservice.services.JoinControllerService;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

/**
 * @author USER
 */
@Import(MyTestConfiguration.class)
public class MembersControllerIT extends AbstractMembersControllerTest{

    @Autowired private JoinControllerService controllerService;
    
    @Autowired private ControllerServiceContextProvider serviceContextProvider;

    @Override
    protected MockContext getMockContext() {
        return MockContext.NO_OP;
    }

    @Override
    public ControllerService getControllerService() {
        return controllerService;
    }

    @Override
    public ControllerServiceContextProvider getServiceContextProvider() {
        return serviceContextProvider;
    }
}
