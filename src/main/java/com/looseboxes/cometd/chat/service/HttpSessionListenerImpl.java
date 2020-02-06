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

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

/**
 * @author USER
 */
@WebListener()
public final class HttpSessionListenerImpl implements javax.servlet.http.HttpSessionListener{

    /**
     * Un-subscribe from chat channel, disconnect from chat session.
     * @param se 
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        final HttpSession session = se.getSession();
        
        final ChatSession chatSession = (ChatSession)session.getAttribute(
                AttributeNames.Session.COMETD_CHAT_SESSION);
        
        if(chatSession != null) {
        
            chatSession.leave();
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) { }
}
