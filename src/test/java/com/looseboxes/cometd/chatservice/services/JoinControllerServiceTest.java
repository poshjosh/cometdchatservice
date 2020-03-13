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

import com.looseboxes.cometd.chatservice.CometDProperties;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;

/**
 * @author USER
 */
public class JoinControllerServiceTest extends AbstractControllerServiceTest{

    @Override
    public String getEndpoint() {
        return Endpoints.JOIN;
    }
    
    @Override
    public ControllerService getControllerService() {

        final CometDProperties props = new CometDProperties();
        props.setHandshakeTimeout(3_000);
        props.setSubscriptionTimeout(7_000);

        return new JoinControllerService(
                getServletUtil(), getResponseBuilder(), props);
    }
}
