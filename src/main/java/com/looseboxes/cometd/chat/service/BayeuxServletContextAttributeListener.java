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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.UnavailableException;
import org.cometd.bayeux.server.BayeuxServer;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author USER
 */
public final class BayeuxServletContextAttributeListener implements ServletContextAttributeListener {
    
    public static final class BayeuxServerUnavailableException extends RuntimeException{
        public BayeuxServerUnavailableException() { }
        public BayeuxServerUnavailableException(Throwable cause) {
            super(cause);
        }
    }
    
    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        
        if (BayeuxServer.ATTRIBUTE.equals(event.getName())) {

//            final BayeuxServer bayeux = (BayeuxServer)event.getValue();

            final ServletContext servletContext = event.getServletContext();

            try{
                WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
                        .getBean(BayeuxInitializer.class).init(servletContext);
            }catch(UnavailableException e) {
                throw new BayeuxServerUnavailableException(e);
            }
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) { }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) { }
}
