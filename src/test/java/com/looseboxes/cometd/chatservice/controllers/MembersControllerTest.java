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
import com.looseboxes.cometd.chatservice.services.MembersControllerService;
import java.util.Collections;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author USER
 */
@WebMvcTest(controllers = MembersController.class)
public class MembersControllerTest extends AbstractControllerTest{

    @MockBean private MembersControllerService controllerService;

    @Test
    @Disabled("@TODO Call Join, maintain session state before calling this")
    public void requestToMembersEndpoint_whenParamsValid_shouldReturnSuccessfully() {
        this.requestToEndpoint_whenParamsValid_shouldReturnSuccessfully(Endpoints.MEMBERS);
    }

    @Test
    @Disabled("@TODO Call Join, maintain session state before calling this")
    public void requestToMembersEndpoint_whenParamsNotValid_shouldReturnErrorResponse() {
        this.requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                Endpoints.MEMBERS, 400, Collections.EMPTY_MAP);
    }

    @Override
    protected ControllerService getControllerService() {
        return controllerService;
    }
}
