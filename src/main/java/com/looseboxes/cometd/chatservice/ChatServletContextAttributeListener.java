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
package com.looseboxes.cometd.chatservice;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import org.cometd.bayeux.server.BayeuxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import com.looseboxes.cometd.chatservice.initializers.ChatServerInitializer;

/**
 * @author USER
 */
public final class ChatServletContextAttributeListener implements ServletContextAttributeListener {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServletContextAttributeListener.class);
    
    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        
        if (BayeuxServer.ATTRIBUTE.equals(event.getName())) {

            final BayeuxServer bayeux = (BayeuxServer)event.getValue();
            
            LOG.debug("Servlet context attribute added: {} = {}", BayeuxServer.ATTRIBUTE, bayeux);

            final ServletContext servletContext = event.getServletContext();
//            final BayeuxServer bayeux = (BayeuxServer)servletContext.getAttribute(BayeuxServer.ATTRIBUTE);
//            if (bayeux == null) {
//                throw new UnavailableException("CometD BayeuxServer unavailable!");
//            }
            
            WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext)
                    .getBean(ChatServerInitializer.class).init(bayeux);
        }
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) { }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) { }
}
