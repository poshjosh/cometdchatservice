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

import com.looseboxes.cometd.chatservice.services.request.ControllerService;
import com.looseboxes.cometd.chatservice.chat.Chat;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.services.response.Response;
import com.looseboxes.cometd.chatservice.test.TestChatObjects;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Map;
import java.util.Objects;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public abstract class AbstractControllerServiceTest {
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    private static class ServiceContextImpl 
            implements ControllerService.ServiceContext{
        private final TestChatObjects testChatObjects;
        private final Map params;
        public ServiceContextImpl(String endpoint) {
            this(new TestConfig(), endpoint);
        }
        public ServiceContextImpl(TestConfig testConfig, String endpoint) {
            testChatObjects = testConfig.testChatObjects();
            params = testConfig.endpointRequestParams().forEndpoint(endpoint);
        }
        @Override
        public BayeuxServer getBayeuxServer() {
            return testChatObjects.getBayeuxServer();
        }
        @Override
        public ChatSession getChatSession() {
            return testChatObjects.getChatSession(getUser());
        }
        public String getUser() {
            final String user = (String)this.getParameters().get(Chat.USER);
            return user == null ? "test_user" : user;
        }
        @Override
        public Map<String, Object> getParameters() {
            return (Map)params;
        }
    }
    
    public static class ServiceContextParameterResolver 
            implements ParameterResolver{
        private final String endpoint;
        public ServiceContextParameterResolver(String endpoint) {
            this.endpoint = Objects.requireNonNull(endpoint);
        }
        @Override
        public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) 
                throws ParameterResolutionException {
            return pc.getParameter().getType()
                    .equals(ControllerService.ServiceContext.class);
        }
        @Override
        public Object resolveParameter(ParameterContext pc, ExtensionContext ec) 
                throws ParameterResolutionException {
            return new ServiceContextImpl(endpoint);
        }
    }
    
    public abstract ControllerService getControllerService();

    @Test
    public void process_whenNullArgumentGiven_shouldThrowRuntimeException() {
        this.process_whenArgumentGiven_shouldThrowRuntimeException(null);
    }
    
    public void process_whenArgumentGiven_shouldThrowRuntimeException(
            ControllerService.ServiceContext serviceContext) {
        final ControllerService controllerService = this.getControllerService();
        
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> controllerService.process(serviceContext));
        
        if(logStackTrace){
            thrown.printStackTrace();
        }
    }
    
    public void process_whenArgumentGiven_shouldReturnSuccessfully(
            ControllerService.ServiceContext serviceContext) {
        final ControllerService controllerService = this.getControllerService();
        final Response result = controllerService.process(serviceContext);
        assertThat(result.getCode(), is(200));
        assertThat(result.isSuccess(), is(true));
    }
    
    public void process_whenArgumentGiven_shouldReturnResponse(
            ControllerService.ServiceContext serviceContext, Response expResult) {
        final ControllerService controllerService = this.getControllerService();
        final Response result = controllerService.process(serviceContext);
        assertThat(result, is(expResult));
    }
}
