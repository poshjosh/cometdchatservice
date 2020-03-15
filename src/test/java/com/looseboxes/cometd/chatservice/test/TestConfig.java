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

import com.looseboxes.cometd.chatservice.CometDProperties;
import com.looseboxes.cometd.chatservice.chat.TestChatConfiguration.ChatSessionProvider;
import com.looseboxes.cometd.chatservice.services.RequestConfiguration;
import com.looseboxes.cometd.chatservice.services.response.ResponseConfiguration;
import com.looseboxes.cometd.chatservice.initializers.InitConfiguration;
import com.looseboxes.cometd.chatservice.services.ChatControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextImpl;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextProvider;
import com.looseboxes.cometd.chatservice.services.JoinControllerService;
import com.looseboxes.cometd.chatservice.services.MembersControllerService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import org.cometd.bayeux.server.BayeuxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
public class TestConfig {

    private static final Logger LOG = LoggerFactory.getLogger(TestConfig.class);
    
    public static final boolean DEBUG = true;
    public static final boolean LOG_STACKTRACE = DEBUG;
    
    private final String contextPath;
    
    private final Supplier<BayeuxServer> bayeuxServerSupplier;
    private final ChatSessionProvider chatSessionProvider;

    public TestConfig(String contextPath, 
            Supplier<BayeuxServer> bayeuxServerSupplier, 
            ChatSessionProvider chatSessionProvider) {
        this.contextPath = Objects.requireNonNull(contextPath);
        this.bayeuxServerSupplier = Objects.requireNonNull(bayeuxServerSupplier);
        this.chatSessionProvider = Objects.requireNonNull(chatSessionProvider);
    }
    
    @Bean public MembersControllerService membersControllerService() {
        return new MembersControllerService(responseConfig().responseBuilder());
    }

    @Bean public JoinControllerService joinControllerService() {
        final CometDProperties cometDProps = new CometDProperties();
        cometDProps.setHandshakeTimeout(3_000);
        cometDProps.setSubscriptionTimeout(7_000);
        return new JoinControllerService(
                this.requestConfig().servletUtil(), 
                this.responseConfig().responseBuilder(), 
                cometDProps);
    }
    
    @Bean public ChatControllerService chatControllerService() {
        return new ChatControllerService(
                joinControllerService(), 
                requestConfig().servletUtil(),
                responseConfig().responseBuilder());
    }
    
    @Bean public ControllerServiceContextProvider controllerServiceContextProvider() {
        return new ControllerServiceContextProviderImpl(this);
    }
    
    @Bean public ControllerServiceContextFromEndpointProvider 
        controllerServiceContextFromEndpointProvider() {
        return new ControllerServiceContextFromEndpointProvider(
                bayeuxServerSupplier.get(), 
                chatSessionProvider, 
                this.endpointRequestParams());
    }
    
    @Bean public TestResponse testResponse() {
        return new TestResponse();
    }
    
    @Bean public TestUtil testUtil() {
        return new TestUtil();
    }
    
    @Bean public EndpointRequestBuilders endpointRequestBuilders() {
        return new EndpointRequestBuilders(this.endpointRequestParams());
    }
    
    @Bean public TestUrls testUrls() {
        return new TestUrls(contextPath, this.endpointRequestParams());
    }
    
    @Bean public EndpointRequestParams endpointRequestParams() {
        return new EndpointRequestParams();
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
    
    private static final class ControllerServiceContextProviderImpl 
            implements ControllerServiceContextProvider{
        
        private final TestConfig testConfig;

        private ControllerServiceContextProviderImpl(TestConfig testConfig) {
            this.testConfig = testConfig;
        }
        
        @Override
        public ControllerService.ServiceContext from(HttpServletRequest req) {
            return testConfig.controllerServiceContext(this.getParameters(req));
        }
        
        private Map getParameters(HttpServletRequest req) {
            final Map<String, Object> params = new HashMap();
            req.getParameterMap().forEach((k, v) -> {
                if(v != null) {
                    if(v.length == 1) {
                        params.put(k, v[0]);
                    }else if(v.length > 1){
                        params.put(k, v);
                    }
                }
            });
            LOG.debug("Request Params: {}", params);
            return params;
        }
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
    public ControllerService.ServiceContext controllerServiceContext(String endpoint){
        return this.controllerServiceContext(
                this.endpointRequestParams().forEndpoint(endpoint));
    }

    public ControllerService.ServiceContext controllerServiceContext(Map params){
        return new ControllerServiceContextImpl(
                this.bayeuxServerSupplier.get(), 
                params,
                this.chatSessionProvider);
    }
}
    // If you annotate this with @Bean, the test ApplicationContext will fail to
    // load due to org.springframework.beans.factory.support.BeanDefinitionOverrideException
    // because a bean already exists in the non - test application configuration
