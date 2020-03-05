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

import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.chat.ChatServerOptionNames;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.services.ServletUtil;
import com.looseboxes.cometd.chatservice.services.response.Response;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Collections;
import java.util.Map;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.Matcher;

/**
 * @author USER
 */
public abstract class AbstractControllerServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractControllerServiceTest.class);
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    public static class ServiceContextImpl 
            implements ControllerService.ServiceContext{
        
        private final BayeuxServer bayeuxServer;
        private final ChatSession chatSession;
        private final Map params;
        
        public ServiceContextImpl(String endpoint) {
            this(new TestConfig(), endpoint);
        }
        public ServiceContextImpl(TestConfig testConfig, String endpoint) {
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

    public static class ValidServiceContext extends ServiceContextImpl{
        public ValidServiceContext(String endpoint) {
            this(new TestConfig(), endpoint);
        }
        public ValidServiceContext(TestConfig testConfig, String endpoint) {
            super(testConfig, endpoint);
            if(Endpoints.MEMBERS.equals(endpoint)) {
        
                final Object option = testConfig.initConfig().membersService();
                getBayeuxServer().setOption(ChatServerOptionNames.MEMBERS_SERVICE, option);
                
                getChatSession().join((csc, msg) -> {});
            }
        }
    }
    
    public static class InvalidServiceContext extends ServiceContextImpl{
        public InvalidServiceContext(String endpoint) {
            super(endpoint);
        }
        public InvalidServiceContext(TestConfig testConfig, String endpoint) {
            super(testConfig, endpoint);
        }
        @Override
        public Map<String, Object> getParameters() {
            return Collections.EMPTY_MAP;
        }
    }
    
    public abstract ControllerService getControllerService();
    
    public abstract String getEndpoint();

    @Test
    public void process_whenNullArgumentGiven_shouldThrowRuntimeException() {
        this.process_whenArgumentGiven_shouldThrowRuntimeException(null);
    }

    @Test
    @DisplayName("When method process is called with valid argument, return successfully")
    public void proces_whenValidArg_shouldReturnSuccessfully() {
        this.process_whenArgumentGiven_shouldReturnSuccessfully(
                getValidArgument());
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
    
    public void process_whenArgumentGiven_shouldReturnError(
            ControllerService.ServiceContext serviceContext) {
        this.process_whenArgumentGiven_shouldReturn(serviceContext, 500, false);
    }

    public void process_whenArgumentGiven_shouldReturnSuccessfully(
            ControllerService.ServiceContext serviceContext) {
        this.process_whenArgumentGiven_shouldReturn(serviceContext, 200, true);
    }

    public void process_whenArgumentGiven_shouldReturn(
            ControllerService.ServiceContext serviceContext,
            int code, boolean success) {
        this.process_whenArgumentGiven_shouldReturn(serviceContext, is(code), success);
    }

    public void process_whenArgumentGiven_shouldReturn(
            ControllerService.ServiceContext serviceContext,
            Matcher<Integer> codeMatcher, boolean success) {
        final ControllerService controllerService = this.getControllerService();
        final Response result = controllerService.process(serviceContext);
        LOG.debug("\n{}", result);
        assertThat(result.getCode(), codeMatcher);
        assertThat(result.isSuccess(), is(success));
    }

    public void process_whenArgumentGiven_shouldReturnResponse(
            ControllerService.ServiceContext serviceContext, Response expResult) {
        final ControllerService controllerService = this.getControllerService();
        final Response result = controllerService.process(serviceContext);
        LOG.debug("\n{}", result);
        assertThat(result, is(expResult));
    }
    
    public ControllerService.ServiceContext getInvalidArgument() {
        return new InvalidServiceContext(this.getEndpoint());
    }

    public ControllerService.ServiceContext getValidArgument() {
        return new ValidServiceContext(this.getEndpoint());
    }

    public ControllerService.ServiceContext getServiceContext() {
        return new ServiceContextImpl(this.getEndpoint());
    }
    
    public ServletUtil getServletUtil() {
        return getTestConfig().requestConfig().servletUtil();
    }

    public Response.Builder getResponseBuilder() {
        return getTestConfig().responseConfig().responseBuilder();
    }    
        
    public TestConfig getTestConfig() {
        return new TestConfig();
    }
}
