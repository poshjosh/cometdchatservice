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

import com.looseboxes.cometd.chatservice.AppConfiguration;
import com.looseboxes.cometd.chatservice.chat.ChatConfiguration;
import com.looseboxes.cometd.chatservice.services.request.RequestConfiguration;
import com.looseboxes.cometd.chatservice.services.response.ResponseConfiguration;
import com.looseboxes.cometd.chatservice.initializers.InitConfiguration;
import java.util.Objects;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
public class TestConfig {
    
    public static final boolean DEBUG = false;
    public static final boolean LOG_STACKTRACE = DEBUG;
    
    private static final AppConfiguration appConfig = new AppConfiguration();
    private static final ChatConfiguration chatConfig = new ChatConfiguration();
    private static final RequestConfiguration requestConfig = new RequestConfiguration();
    private static final ResponseConfiguration responseConfig = new ResponseConfiguration();
    private static final InitConfiguration initConfig = new InitConfiguration();
    
    private final String contextPath;

    public TestConfig() {
        this("");
    } 
    
    public TestConfig(String contextPath) { 
        this.contextPath = Objects.requireNonNull(contextPath);
    }
    
    @Bean public TestChatObjects testChatObjects() {
        return new TestChatObjects(this);
    }
    
    @Bean public TestData testData() {
        return new TestData();
    }
    
    @Bean public TestUtil testUtil() {
        return new TestUtil();
    }
    
    @Bean public EndpointRequestBuilders endpointRequestBuilders() {
        return new EndpointRequestBuilders(this.endpointRequestParams());
    }
    
    @Bean public TestUrls testUrl() {
        return new TestUrls(contextPath, this.endpointRequestParams());
    }
    
    @Bean public EndpointRequestParams endpointRequestParams() {
        return new EndpointRequestParams();
    }
    
    public AppConfiguration appConfig() {
        return appConfig;
    }
    
    public ChatConfiguration chatConfig() {
        return chatConfig;
    }

    public RequestConfiguration getRequestConfig() {
        return requestConfig;
    }

    public ResponseConfiguration getResponseConfig() {
        return responseConfig;
    }

    public InitConfiguration getInitConfig() {
        return initConfig;
    }
}
