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

import com.looseboxes.cometd.chatservice.chat.TestChatConfiguration;
import java.util.function.Supplier;
import org.cometd.bayeux.server.BayeuxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;

/**
 * @author USER
 */
@TestConfiguration
public class MyTestConfiguration extends TestConfig{
    
    private static final Logger LOG = LoggerFactory.getLogger(MyTestConfiguration.class);
    
    public MyTestConfiguration() { 
        this("", new TestChatConfiguration());
    }
    
    public MyTestConfiguration(String contextPath, TestChatConfiguration testChatConfiguration) {
        this(contextPath, 
                () -> testChatConfiguration.getBayeuxServer(), 
                testChatConfiguration.chatSessionProvider());
    }
    
    public MyTestConfiguration(String contextPath, 
            Supplier<BayeuxServer> bayeuxServerSupplier, 
            TestChatConfiguration.ChatSessionProvider chatSessionProvider) {
        super(contextPath, bayeuxServerSupplier, chatSessionProvider);
    }
}
