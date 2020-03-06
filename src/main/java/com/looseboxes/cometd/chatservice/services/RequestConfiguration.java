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

import com.looseboxes.cometd.chatservice.services.RequestUrl;
import com.looseboxes.cometd.chatservice.services.RequestUrlImpl;
import com.looseboxes.cometd.chatservice.services.ServletUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class RequestConfiguration {
    
    @Bean @Scope("prototype") public RequestUrl requestUrl(HttpServletRequest request){
        return new RequestUrlImpl(request);
    }
    
    @Bean @Scope("prototype") public ControllerServiceContext controllerServiceContext() {
        return new ControllerServiceContext();
    }
    
    @Bean public ControllerServiceContextProvider controllerServiceContextProvider(){
        return new ControllerServiceContextProviderImpl(servletUtil());
    }
    
    @Bean public ServletUtil servletUtil() {
        return new ServletUtil();
    }
}
/**
 * 
    @Bean(ControllerServiceQualifiers.MEMBERS_SERVICE) 
    @Scope("prototype") public ControllerService membersHandler() {
        return new MembersHandler();
    }

    @Bean(ControllerServiceQualifiers.CHAT_SERVICE) 
    @Scope("prototype") public ControllerService chatHandler() {
        return new ChatHandler();
    }

    @Bean(ControllerServiceQualifiers.JOIN_SERVICE) 
    @Scope("prototype") public ControllerService joinHandler() {
        return new JoinHandler();
    }
 * 
 */