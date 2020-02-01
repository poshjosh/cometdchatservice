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
package com.looseboxes.cometd.chat.service.controllers;

import com.looseboxes.cometd.chat.service.requesthandlers.Response;
import com.looseboxes.cometd.chat.service.requesthandlers.ResponseBuilder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author USER
 */
@RestController
public class ShutdownController implements ApplicationContextAware {
     
    private ApplicationContext context;
     
    @RequestMapping(Endpoints.SHUTDOWN)
    public Response shutdown() {
        
        final ResponseBuilder resBuilder = context.getBean(ResponseBuilder.class);
        
        try{
            
            ((ConfigurableApplicationContext) context).close();
            
            return resBuilder.buildSuccessResponse();
            
        }catch(Exception e) {
        
            return resBuilder.buildErrorResponse(e);
        }
    }
 
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }
}