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

import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Map;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * @author USER
 */
public class ControllerServiceContextImpl implements ControllerService.ServiceContext{
        
    private final BayeuxServer bayeuxServer;
    private final ChatSession chatSession;
    private final Map params;

    public ControllerServiceContextImpl(String endpoint) {
        this(new TestConfig(), endpoint);
    }
    
    public ControllerServiceContextImpl(TestConfig testConfig, String endpoint) {
        bayeuxServer = testConfig.testChatObjects().getBayeuxServer();
        params = testConfig.endpointRequestParams().forEndpoint(endpoint);
        String user = (String)params.get(ParamNames.USER);
        user = user == null ? "test_user" : user;
        chatSession = testConfig.testChatObjects().getChatSession(user);
    }

    @Override
    public BayeuxServer getBayeuxServer() {
        return bayeuxServer;
    }
    
    @Override
    public ChatSession getChatSession() {
        return chatSession;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return (Map)params;
    }
}
