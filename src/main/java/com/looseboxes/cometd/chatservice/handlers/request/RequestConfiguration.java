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
package com.looseboxes.cometd.chatservice.handlers.request;

import com.looseboxes.cometd.chatservice.handlers.response.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class RequestConfiguration {
    
    @Bean(RequestHandlerQualifiers.MEMBERS_HANDLER) 
    @Scope("prototype") public RequestHandler<Response> membersHandler() {
        return new MembersHandler();
    }

    @Bean(RequestHandlerQualifiers.CHAT_HANDLER) 
    @Scope("prototype") public RequestHandler<Response> chatHandler() {
        return new ChatHandler();
    }

    @Bean(RequestHandlerQualifiers.JOIN_HANDLER) 
    @Scope("prototype") public RequestHandler<Response> joinHandler() {
        return new JoinHandler();
    }

    @Bean public RequestHandlerFactory requestHandlerFactory() {
        return new RequestHandlerFactoryImpl();
    }
}
