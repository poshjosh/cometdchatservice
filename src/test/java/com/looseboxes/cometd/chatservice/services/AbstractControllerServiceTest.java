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
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.chat.MembersService;
import com.looseboxes.cometd.chatservice.chat.TestChatConfiguration;
import com.looseboxes.cometd.chatservice.chat.TestChatConfiguration.ChatSessionProvider;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.initializers.InitConfiguration;
import com.looseboxes.cometd.chatservice.services.AbstractControllerServiceTest.ControllerServiceTestConfiguration;
import com.looseboxes.cometd.chatservice.services.response.Response;
import com.looseboxes.cometd.chatservice.services.response.ResponseConfiguration;
import com.looseboxes.cometd.chatservice.test.ControllerServiceContextFromEndpointProvider;
import com.looseboxes.cometd.chatservice.test.EndpointRequestParams;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author USER
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    MyTestConfiguration.class, TestChatConfiguration.class, 
    ControllerServiceTestConfiguration.class})
public abstract class AbstractControllerServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractControllerServiceTest.class);
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    @TestConfiguration
    static class ControllerServiceTestConfiguration {

        private final RequestConfiguration reqConfig = new RequestConfiguration();
        private final ResponseConfiguration resConfig = new ResponseConfiguration();
        private final InitConfiguration initConfig = new InitConfiguration();

        @Bean public ServletUtil servletUtil() {
            return reqConfig.servletUtil();
        }
        
        @Bean public Response.Builder responseBuilder() {
            return resConfig.responseBuilder();
        }
        
        @Bean public MembersService membersService() {
            return initConfig.membersService();
        }
    } 
    
    @Autowired private ServletUtil servletUtil;
    @Autowired private Response.Builder responseBuilder;
    @Autowired private MembersService membersService;
    
    @Autowired private TestChatConfiguration chatConfig;
    @Autowired private EndpointRequestParams endpointRequestParams;
    @Autowired private ControllerServiceContextFromEndpointProvider 
            controllerServiceContextFromEndpointProvider;
    @Autowired private BayeuxServer bayeuxServer;
    @Autowired private ChatSessionProvider chatSessionProvider;
    
    public abstract ControllerService getControllerService();
    
    public abstract String getEndpoint();

//    @Test
    @DisplayName("When method process is called with NULL argument, throw exception")
    public void process_whenNullArgumentGiven_shouldThrowRuntimeException() {
        this.process_whenArgumentGiven_shouldThrowRuntimeException(null);
    }

//    @Test
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
        return new InvalidServiceContext(
                this.getBayeuxServer(),
                this.getChatSessionProvider());
    }

    public ControllerService.ServiceContext getValidArgument() {
        return new ValidServiceContext(
                this.getBayeuxServer(), this.getChatSessionProvider(),
                this.getEndpointRequestParams(), this.getMembersService(), 
                this.getEndpoint(), Endpoints.MEMBERS.equals(this.getEndpoint()));
    }

    public ControllerService.ServiceContext getServiceContext() {
        return getControllerServiceContextFromEndpointProvider().from(getEndpoint());
    }

    public TestChatConfiguration getChatConfig() {
        return chatConfig;
    }
    
    public ServletUtil getServletUtil() {
        return servletUtil;
    }

    public Response.Builder getResponseBuilder() {
        return responseBuilder;
    }    

    public ControllerServiceContextFromEndpointProvider 
        getControllerServiceContextFromEndpointProvider() {
        return controllerServiceContextFromEndpointProvider;
    }

    public EndpointRequestParams getEndpointRequestParams() {
        return endpointRequestParams;
    }

    public MembersService getMembersService() {
        return membersService;
    }

    public BayeuxServer getBayeuxServer() {
        return bayeuxServer;
    }

    public ChatSessionProvider getChatSessionProvider() {
        return chatSessionProvider;
    }

    public static class InvalidServiceContext extends ControllerServiceContextImpl{
        public InvalidServiceContext(
                BayeuxServer bayeuxServer, 
                ChatSessionProvider chatSessionProvider) {
            super(bayeuxServer, Collections.EMPTY_MAP, chatSessionProvider);
            LOG.debug("<TestConfig>{}");
        }
    }

    public static class ValidServiceContext extends ControllerServiceContextImpl{
        
        private final boolean joinChat;
        
        public ValidServiceContext(
                BayeuxServer bayeuxServer, ChatSessionProvider chatSessionProvider, 
                EndpointRequestParams endpointRequestParams, 
                MembersService membersService, String endpoint,
                boolean joinChat) {
            
            super(bayeuxServer, 
                    params(endpointRequestParams, endpoint),
                    chatSessionProvider);
            
            if(Endpoints.MEMBERS.equals(endpoint)) {
                bayeuxServer.setOption(
                        ChatServerOptionNames.MEMBERS_SERVICE, membersService);
            }
            
            this.joinChat = joinChat;
        }

        private final AtomicBoolean joined = new AtomicBoolean();
        
        @Override
        public ChatSession getChatSession() {
            final ChatSession chatSession = super.getChatSession();
            if(joinChat && !joined.get()) {
                joined.compareAndSet(false, true);
                chatSession.join((csc, msg) -> {});
            }
            return chatSession;
        }

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
        private static Map params(
                EndpointRequestParams endpointRequestParams, String endpoint) {
            final Map params = endpointRequestParams.forEndpoint(endpoint);
            if(Endpoints.MEMBERS.equals(endpoint)) {
                final Map update = params == null ? 
                        new HashMap() : new HashMap(params);
                final Map joinParams = endpointRequestParams.forEndpoint(Endpoints.JOIN);
                update.putAll(joinParams);
                return update;
            }
            return params;
        }
    }
}
