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
package com.looseboxes.cometd.chat.service.test;

import com.looseboxes.cometd.chat.service.ParamNames;
import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Build endpoint requests with custom request parameters for tests.
 * Expected endpoints are defined in the 
 * {@link com.looseboxes.cometd.chat.service.controllers.Endpoints Endpoints} interface.
 * @author USER
 */
public class EndpointRequestBuilders {

    public MockHttpServletRequestBuilder builder(String endpoint) {
        final MockHttpServletRequestBuilder builder = get(endpoint);
        switch(endpoint) {
            case Endpoints.CHAT:
                builder.param(ParamNames.USER, "Non")
                        .param(ParamNames.PEER, "Nel")
                        .param(ParamNames.ROOM, "/chat/demo")
                        .param(ParamNames.CHAT, "Hi love");
                break;
            case Endpoints.SHUTDOWN:
                builder.param(ParamNames.DELAY, "500");
                break;
            default:
                throw new UnsupportedOperationException(endpoint);
        }        
        return builder;
    }
}
