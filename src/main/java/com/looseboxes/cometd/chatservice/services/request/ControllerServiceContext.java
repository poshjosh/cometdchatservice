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
import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.bayeuxServer);
        hash = 47 * hash + Objects.hashCode(this.chatSession);
        hash = 47 * hash + Objects.hashCode(this.parameters);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ControllerServiceContext other = (ControllerServiceContext) obj;
        if (!Objects.equals(this.bayeuxServer, other.bayeuxServer)) {
            return false;
        }
        if (!Objects.equals(this.chatSession, other.chatSession)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }
}
/**
 * 

    public ChatRequestServiceContextImpl(
            BayeuxServer bayeuxServer, ChatSession chatSession, 
            Map<String, Object> parameters, Response.Builder responseBuilder) {
        this.bayeuxServer = Objects.requireNonNull(bayeuxServer);
        this.chatSession = Objects.requireNonNull(chatSession);
        this.parameters = Objects.requireNonNull(parameters);
        this.responseBuilder = Objects.requireNonNull(responseBuilder);
    }
 * 
 */