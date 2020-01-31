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

import com.looseboxes.cometd.chat.service.requesthandlers.ServletUtil;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
@WebListener()
public final class HttpSessionListenerImpl implements javax.servlet.http.HttpSessionListener{

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        try{
            
            final ServletUtil util = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(se.getSession().getServletContext())
                    .getBean(ServletUtil.class);

            final ClientSessionChannel channel = util.getDefaultChatChannel(se.getSession(), null);

            if(channel != null) {

                channel.unsubscribe();
            }
        }finally{
        
            final ClientSession client = (ClientSession)se.getSession()
                    .getAttribute(AttributeNames.COMETD_CLIENT_SESSION);

            if(client != null) {
                
                client.disconnect();
            }
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) { }
}
