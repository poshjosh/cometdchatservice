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
package com.looseboxes.cometd.chat.service;

import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import com.looseboxes.cometd.chat.service.test.TestConfig;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.cometd.bayeux.Message;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ChatSessionTest extends ChatSessionMockTest{
    
    public ChatSessionTest() { }

    @Test
    public void connectWhenCalledMoreThanOnce_ShouldThrowException() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            cs.connect();
            return cs.connect();
        };        
        this.action_ShouldThrowException(
                "connect_WhenCalledMoreThanOnce_ShouldThrowException()", action);
    } 

    @Override
    protected ChatSession getCandidate() {
        return this.getChatSession();
    }

    @Override
    protected ChatSession getChatSession() {
        final TestConfig cfg = this.getTestConfig();
        final String url = cfg.testUrl().getEndpointUrl(8080, Endpoints.COMETD);
        return cfg.appConfig().chatSession(url, 
                "/service/privatechat", "/chat/demo", "Nonso");
    }
}
