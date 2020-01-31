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
import com.looseboxes.cometd.chat.service.requesthandlers.ChatHandler;
import com.looseboxes.cometd.chat.service.requesthandlers.ServletUtil;
import com.looseboxes.cometd.chat.service.requesthandlers.ErrorResponseProvider;
import com.looseboxes.cometd.chat.service.requesthandlers.JoinHandler;
import com.looseboxes.cometd.chat.service.requesthandlers.JsonResponseHandler;
import com.looseboxes.cometd.chat.service.requesthandlers.RequestHandlerFactory;
import com.looseboxes.cometd.chat.service.requesthandlers.RequestHandlerFactoryImpl;
import com.looseboxes.cometd.chat.service.requesthandlers.Await;
import com.looseboxes.cometd.chat.service.requesthandlers.AwaitImpl;
import com.looseboxes.cometd.chat.service.requesthandlers.ErrorResponseProviderImpl;
import com.looseboxes.cometd.chat.service.requesthandlers.ResponseImpl;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class CometDConfiguration {
    
    @Bean @Scope("prototype") public ClientSessionPublisher clientSessionPublisher(Await await) {
        final Supplier<ResponseImpl> supplier = () -> this.response();
        return new ClientSessionPublisherImpl(supplier, await);
    }
    
    @Bean @Scope("prototype") public ClientSessionChannelSubscription 
        clientSessionChannelSubscriptionListener(Await await) {
        final Supplier<ResponseImpl> supplier = () -> this.response();
        return new ClientSessionChannelSubscriptionImpl(supplier, await);
    }
    
    @Bean @Scope("prototype") public Await await() {
        return new AwaitImpl();
    }
    
    @Bean @Scope("prototype") public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean public ErrorResponseProvider errorResponseProvider() {
        final Supplier<ResponseImpl> supplier = () -> this.response();
        return new ErrorResponseProviderImpl(supplier);
    }
    
    @Bean @Scope("prototype") public ResponseImpl response() {
        return new ResponseImpl();
    }
    
    @Bean public ServletUtil servletUtil() {
        return new ServletUtil();
    }
    
    @Bean public JsonResponseHandler jsonResponseHandler() {
        return new JsonResponseHandler();
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
