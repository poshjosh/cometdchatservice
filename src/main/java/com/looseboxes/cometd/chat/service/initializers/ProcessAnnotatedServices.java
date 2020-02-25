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
package com.looseboxes.cometd.chat.service.initializers;

import java.util.List;
import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.annotation.Service;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * @author USER
 */
public final class ProcessAnnotatedServices implements ChatServerInitAction<Object>{
    
    public ProcessAnnotatedServices() { }
    
    @Override
    public BayeuxServer apply(BayeuxServer bayeux, List args) {
        
        final ServerAnnotationProcessor processor = new ServerAnnotationProcessor(bayeux);
        
        for(Object service : args) {
            
            this.checkAnnotatedService(service);
            
            processor.process(service);
        }
        
        return bayeux;
    }

    private void checkAnnotatedService(Object service) {
        final Class type = service.getClass();
        if(type.getAnnotation(Service.class) == null){
            throw new IllegalArgumentException("Must be annotated with @" + 
                    Service.class.getName() + ", type: " + type);
        }
    }
}
