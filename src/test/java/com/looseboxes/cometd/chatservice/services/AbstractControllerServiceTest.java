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

import com.looseboxes.cometd.chatservice.chat.ChatServerOptionNames;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.services.response.Response;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * @author USER
 */
public abstract class AbstractControllerServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractControllerServiceTest.class);
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    public static class ValidServiceContext extends ControllerServiceContextImpl{
        
        /**
         * By default Endpoints.MEMBERS requires no parameters. But it also needs
         * to call ChatSession.join(), prior to being executed, to be successful,
         * hence this method adds the parameters required by ChatSession.join, 
         * i.e required by Endpoints.JOIN.
         * @param testConfig
         * @param endpoint
         * @param params The parameters to update
         * @return The updated parameters
         */
        private static Map updateParams(
                TestConfig testConfig, String endpoint, Map params) {
            if(Endpoints.MEMBERS.equals(endpoint)) {
                final Map update = params == null ? 
                        new HashMap() : new HashMap(params);
                final Map joinParams = testConfig
                        .endpointRequestParams().forEndpoint(Endpoints.JOIN);
                update.putAll(joinParams);
                return update;
            }
            return params;
        }
        
        public ValidServiceContext(TestConfig testConfig, String endpoint, Map params) {
            super(testConfig, updateParams(testConfig, endpoint, params));
            if(Endpoints.MEMBERS.equals(endpoint)) {
        
                final Object option = testConfig.initConfig().membersService();
                getBayeuxServer().setOption(ChatServerOptionNames.MEMBERS_SERVICE, option);
                
                getChatSession().join((csc, msg) -> {});
            }
        }
    }
    
    public static class InvalidServiceContext extends ControllerServiceContextImpl{
        public InvalidServiceContext() {
            this(new TestConfig());
        }
        public InvalidServiceContext(TestConfig testConfig) {
            super(testConfig, Collections.EMPTY_MAP);
            LOG.debug("<TestConfig>{}");
        }
    }
    
    public abstract ControllerService getControllerService();
    
    public abstract String getEndpoint();

    @Test
    @DisplayName("When method process is called with NULL argument, throw exception")
    public void process_whenNullArgumentGiven_shouldThrowRuntimeException() {
        this.process_whenArgumentGiven_shouldThrowRuntimeException(null);
    }

    @Test
    @DisplayName("When method process is called with invalid argument, return error response")
    public void proces_whenInvalidArg_shouldReturnError() {
        this.process_whenArgumentGiven_shouldReturn(
                getInvalidArgument(), greaterThanOrEqualTo(300), false);
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
        return new InvalidServiceContext(this.getTestConfig());
    }

    public ControllerService.ServiceContext getValidArgument() {
        return new ValidServiceContext(
                this.getTestConfig(), this.getEndpoint(),
                getTestConfig().endpointRequestParams().forEndpoint(getEndpoint())
        );
    }

    public ControllerService.ServiceContext getServiceContext() {
        return getTestConfig()
                .controllerServiceContextFromEndpointProvider().from(getEndpoint());
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
