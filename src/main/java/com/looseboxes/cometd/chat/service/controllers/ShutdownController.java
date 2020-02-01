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

import com.looseboxes.cometd.chat.service.ParamNames;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author USER
 */
@RestController
public class ShutdownController{
     
    private final ApplicationContext context;

    public ShutdownController(@Autowired ApplicationContext context) {
        this.context = Objects.requireNonNull(context);
    }
     
    @RequestMapping(Endpoints.SHUTDOWN)
    public Response shutdown(@RequestParam(value=ParamNames.DELAY, required=false) Long delay) {

        final ResponseBuilder resBuilder = context.getBean(ResponseBuilder.class);

        try{
            
            this.shutdownAfter(delay);
            
            return resBuilder.buildSuccessResponse();
            
        }catch(Exception e) {
        
            return resBuilder.buildErrorResponse(e);
        }
    }
    
    private void shutdownAfter(@RequestParam(value="delay", required=false) Long delay) {

        if(delay == null || delay < 1) {
        
            ((ConfigurableApplicationContext) context).close();
            
        }else{
        
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    ShutdownController.this.shutdown();
                }
            };
            final Timer timer = new Timer(this.getClass().getName()+"_Timer");
            timer.schedule(task, delay);            
        }
    }

    private void shutdown() {
        ((ConfigurableApplicationContext) context).close();
    }
}