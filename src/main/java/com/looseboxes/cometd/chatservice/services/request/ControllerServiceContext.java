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
package com.looseboxes.cometd.chatservice.services.request;

import com.looseboxes.cometd.chatservice.chat.ChatSession;
import java.util.Map;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * @author USER
 */
public class ControllerServiceContext implements ControllerService.ServiceContext{
    
    private BayeuxServer bayeuxServer; 
    
    private ChatSession chatSession;
    
    private Map<String, Object> parameters;

    public ControllerServiceContext() { }

    @Override
    public BayeuxServer getBayeuxServer() {
        return bayeuxServer;
    }

    public void setBayeuxServer(BayeuxServer bayeuxServer) {
        this.bayeuxServer = bayeuxServer;
    }

    @Override
    public ChatSession getChatSession() {
        return chatSession;
    }

    public void setChatSession(ChatSession chatSession) {
        this.chatSession = chatSession;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
/**
 * 

    public ChatRequestServiceContextImpl(
            BayeuxServer bayeuxServer, ChatSession chatSession, 
            Map<String, Object> parameters, MessageResponseBuilder responseBuilder) {
        this.bayeuxServer = Objects.requireNonNull(bayeuxServer);
        this.chatSession = Objects.requireNonNull(chatSession);
        this.parameters = Objects.requireNonNull(parameters);
        this.responseBuilder = Objects.requireNonNull(responseBuilder);
    }
 * 
 */