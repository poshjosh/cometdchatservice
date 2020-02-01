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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looseboxes.cometd.chat.service.handlers.request.ChatHandler;
import com.looseboxes.cometd.chat.service.handlers.ServletUtil;
import com.looseboxes.cometd.chat.service.handlers.request.JoinHandler;
import com.looseboxes.cometd.chat.service.handlers.request.RequestHandlerFactory;
import com.looseboxes.cometd.chat.service.handlers.request.RequestHandlerFactoryImpl;
import com.looseboxes.cometd.chat.service.handlers.Await;
import com.looseboxes.cometd.chat.service.handlers.AwaitImpl;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseCodeFromSpringAnnotationProvider;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseCodeProvider;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseConfiguration.ResponseSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class CometDConfiguration {
    
    @Bean public ResponseCodeProvider responseCodeProvider() {
        return new ResponseCodeFromSpringAnnotationProvider();
    }
    
    @Bean @Scope("prototype") public ClientSessionPublisher clientSessionPublisher(
            ResponseSupplier resSupplier, Await await) {
        return new ClientSessionPublisherImpl(resSupplier, await);
    }
    
    @Bean @Scope("prototype") public ClientSessionChannelSubscription 
        clientSessionChannelSubscriptionListener(ResponseSupplier resSupplier, Await await) {
        return new ClientSessionChannelSubscriptionImpl(resSupplier, await);
    }
    
    @Bean @Scope("prototype") public Await await() {
        return new AwaitImpl();
    }
    
    @Bean @Scope("prototype") public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean public ServletUtil servletUtil() {
        return new ServletUtil();
    }
    
    @Bean @Scope("prototype") public ChatHandler chatHandler() {
        return new ChatHandler();
    }

    @Bean @Scope("prototype") public JoinHandler joinHandler() {
        return new JoinHandler();
    }

    @Bean public RequestHandlerFactory requestHandlerFactory() {
        return new RequestHandlerFactoryImpl();
    }
    
    @Bean public ClientProvider clientProvider(ClientTransportProvider clientTransportProvider) {
        return new ClientProviderImpl(clientTransportProvider);
    }

    @Bean public ClientTransportProvider clientTransportProvider() {
        return new ClientTransportProviderImpl();
    }

    @Bean public TerminateBean getTerminateBean() {
        return new TerminateBean();
    }
}
