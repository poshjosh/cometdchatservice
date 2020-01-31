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
package com.looseboxes.cometd.chat.service.requesthandlers;

import com.looseboxes.cometd.chat.service.requesthandlers.exceptions.InvalidRequestParameterException;
import com.looseboxes.cometd.chat.service.AttributeNames;
import com.looseboxes.cometd.chat.service.CometDProperties;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public final class ServletUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(ServletUtil.class);
    
    public ClientSessionChannel getDefaultChatChannel(
            HttpSession session, ClientSessionChannel resultIfNone) {
        
        final ClientSessionChannel channel;
        
        final ClientSession client = (ClientSession)session
                .getAttribute(AttributeNames.COMETD_CLIENT_SESSION);
        
        if(client == null) {
            channel = null;
        }else{    
            final CometDProperties cometdProps = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(session.getServletContext())
                    .getBean(CometDProperties.class);
            channel = client.getChannel(cometdProps.getDefaultChannel());
        }
        
        return channel == null ? resultIfNone : channel;
    }
    
    public String requireNonNullOrEmpty(ServletRequest req, String paramName) {
        final String paramValue = req.getParameter(paramName);
        if(paramValue == null || paramValue.isEmpty()) {
            throw new InvalidRequestParameterException(paramName + '=' + paramValue);
        }
        return paramValue;
    }

    public String getContextUrl(HttpServletRequest request) {
        
        final String result = this.getBaseUrl(request) + request.getContextPath();
        
        LOG.debug("Context URL: {}", result);
        
        return result;
    }

    public String getBaseUrl(HttpServletRequest request) {
        
        final String result = request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length());
        
        LOG.debug("Base URL: {}", result);

        return result;
    }
}
