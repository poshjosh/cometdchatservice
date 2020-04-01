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
import com.looseboxes.cometd.chatservice.chat.TestChatConfiguration.ChatSessionProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.cometd.bayeux.server.BayeuxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To simulate a service context in the event the parameter Map argument to
 * the constructor is empty (i.e invalid state), we defer the creation of 
 * the {@link com.looseboxes.cometd.chatservice.chat.ChatSession ChatSession} 
 * object for this context till the method {@link #getChatSession()} is called.
 * @see #getChatSession() 
 * @author USER
 */
public class ControllerServiceContextImpl implements ControllerService.ServiceContext{
        
    private static final Logger LOG = 
            LoggerFactory.getLogger(ControllerServiceContextImpl.class);
    
    private final BayeuxServer bayeuxServer;
    private final Map parameters;
    private final ChatSessionProvider chatSessionProvider;

    public ControllerServiceContextImpl(
            BayeuxServer bayeuxServer, Map params,
            ChatSessionProvider chatSessionProvider) {
        this.bayeuxServer = Objects.requireNonNull(bayeuxServer);
        this.parameters = params == null ? null : Collections.unmodifiableMap(params);
        this.chatSessionProvider = Objects.requireNonNull(chatSessionProvider);
        LOG.debug("Parameters: {}", params);
    }

    @Override
    public BayeuxServer getBayeuxServer() {
        return bayeuxServer;
    }
    
    
    private ChatSession _chatSession;
    @Override
    public ChatSession getChatSession() {
        if(_chatSession == null) {
            final Map m = this.getParameters();
            LOG.debug("Parameters: {}", m);
            _chatSession = this.chatSessionProvider.getChatSession(
                    (String)m.get(ParamNames.USER),
                    (String)m.get(ParamNames.ROOM));
        } 
        return _chatSession;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return (Map)parameters;
    }
}
