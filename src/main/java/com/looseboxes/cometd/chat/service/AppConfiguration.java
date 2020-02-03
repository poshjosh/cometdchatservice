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
import com.looseboxes.cometd.chat.service.handlers.response.ResponseConfiguration;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class AppConfiguration {
    
    private final ResponseConfiguration responseConfiguration;

    public AppConfiguration(@Autowired ResponseConfiguration responseConfiguration) {
        this.responseConfiguration = Objects.requireNonNull(responseConfiguration);
    }
    
    @Bean @Scope("prototype") public ClientSessionPublisher clientSessionPublisher() {
        return new ClientSessionPublisherImpl(this.responseConfiguration.responseSupplier(), await());
    }
    
    @Bean @Scope("prototype") public ClientSessionChannelSubscription 
        clientSessionChannelSubscription() {
        return new ClientSessionChannelSubscriptionImpl(
                this.responseConfiguration.responseSupplier(), await());
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
    
    @Bean public ClientProvider clientProvider() {
        return new ClientProviderImpl(clientTransportProvider());
    }

    @Bean public ClientTransportProvider clientTransportProvider() {
        return new ClientTransportProviderImpl();
    }

    @Bean public TerminateBean getTerminateBean() {
        return new TerminateBean();
    }
}