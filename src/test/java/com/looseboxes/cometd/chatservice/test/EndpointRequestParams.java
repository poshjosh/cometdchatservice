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
package com.looseboxes.cometd.chatservice.test;

import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import java.util.HashMap;
import java.util.Map;

/**
 * @author USER
 */
public class EndpointRequestParams {
    
    public Map<String, String> forEndpoint(String endpoint) {
        final String room = "/chat/demo";
        final Map params = new HashMap<>();
        switch(endpoint) {
            case Endpoints.JOIN:
                params.put(ParamNames.USER, "Non");
                params.put(ParamNames.ROOM, room);
                break;
            case Endpoints.CHAT:
                // This is required because chat triggers join if user not joined to chat
                params.put(ParamNames.USER, "Non");
                params.put(ParamNames.ROOM, room);

                params.put(ParamNames.PEER, "Nel");
                params.put(ParamNames.CHAT, "Hi love");
                break;
            case Endpoints.MEMBERS:
//                params.put(ParamNames.ROOM, room);
                break;
            default:
                throw new UnsupportedOperationException("Unexpected endpoint: " + endpoint);
        }        
        return params;
    }
}
