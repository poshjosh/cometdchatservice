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
import com.looseboxes.cometd.chatservice.services.request.ControllerService;
import com.looseboxes.cometd.chatservice.services.request.ControllerServiceContextImpl;
import java.util.Objects;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
public class TestConfig {
    
    public static final boolean DEBUG = false;
    public static final boolean LOG_STACKTRACE = DEBUG;
    
    private final String contextPath;

    public TestConfig() {
        this("");
    } 
    
    public TestConfig(String contextPath) { 
        this.contextPath = Objects.requireNonNull(contextPath);
    }
    
    @Bean public ControllerServiceContextFromEndpointProvider 
        controllerServiceContextFromEndpointProvider() {
        return new ControllerServiceContextFromEndpointProvider(this);
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
        return new AppConfiguration();
    }
    
    public ChatConfiguration chatConfig() {
        return new ChatConfiguration();
    }

    public RequestConfiguration requestConfig() {
        return new RequestConfiguration();
    }

    public ResponseConfiguration responseConfig() {
        return new ResponseConfiguration();
    }

    public InitConfiguration initConfig() {
        return new InitConfiguration();
    }

    // When @Bean annotation is added here. Spring complains that
    // Caused by: ..UnsatisfiedDependencyException: Error creating bean with name 
    // 'controllerServiceContext' .. Unsatisfied dependency expressed through 
    // method 'controllerServiceContext' parameter 0; nested exception is 
    // ..NoSuchBeanDefinitionException: No qualifying bean of type 'java.lang.String' 
    // available: expected at least 1 bean which qualifies as autowire candidate. 
    // Dependency annotations: {}
    /**
     * Rather than use this method to obtain an instance of 
     * {@link com.looseboxes.cometd.chatservice.services.request.ControllerService.ServiceContext ControllerService.ServiceContext},
     * inject/autowire the bean 
     * {@link com.looseboxes.cometd.chatservice.test.ControllerServiceContextFromEndpointProvider ControllerServiceContextFromEndpointProvider}
     * and call its method 
     * {@link com.looseboxes.cometd.chatservice.test.ControllerServiceContextFromEndpointProvider#from(java.lang.String) from(String)}
     * 
     * @param endpoint The endpoint e.g <code>/chat</code> for which to return a
     * {@link com.looseboxes.cometd.chatservice.services.request.ControllerService.ServiceContext ControllerService.ServiceContext}
     * @return an instance of 
     * {@link com.looseboxes.cometd.chatservice.services.request.ControllerService.ServiceContext ControllerService.ServiceContext}
     * @see #controllerServiceContextFromEndpointProvider() 
     * @deprecated
     */
    @Deprecated
    public ControllerService.ServiceContext controllerServiceContext(
            String endpoint){
        return new ControllerServiceContextImpl(this, endpoint);
    }
}
