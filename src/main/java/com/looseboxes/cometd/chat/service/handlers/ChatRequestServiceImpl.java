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
package com.looseboxes.cometd.chat.service.handlers;

import com.looseboxes.cometd.chat.service.AttributeNames;
import com.looseboxes.cometd.chat.service.ChatConfig;
import com.looseboxes.cometd.chat.service.ChatSession;
import com.looseboxes.cometd.chat.service.CometDProperties;
import com.looseboxes.cometd.chat.service.ParamNames;
import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import com.looseboxes.cometd.chat.service.handlers.request.JoinHandler;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public class ChatRequestServiceImpl implements ChatRequestService {
    
    private static final Logger LOG = LoggerFactory.getLogger(ChatRequestServiceImpl.class);
    
    private final ServletUtil servletUtil;

    public ChatRequestServiceImpl(ServletUtil servletUtil) {
        this.servletUtil = Objects.requireNonNull(servletUtil);
    }
    
    /**
     * @param req
     * @param res
     * @return {@link com.looseboxes.cometd.chat.service.handlers.response.Response Response} 
     * object with success set to true if previously joined to chat or
     * successfully joined to chat during this methods execution, otherwise 
     * return false.
     */
    @Override
    public Response joinChatIfNotAlready(HttpServletRequest req, HttpServletResponse res) {
        
        final WebApplicationContext webAppCtx = getWebAppContext(req);
        
        final boolean joinedToChat = this.isJoinedToChat(req);
        
        if( ! joinedToChat) {
        
            final JoinHandler jh = webAppCtx.getBean(JoinHandler.class);

            final Response jhr = jh.process(req, res);

            if(!jhr.isSuccess()) {

                return jhr;
            }
        }
        
        return webAppCtx.getBean(ResponseBuilder.class).buildSuccessResponse();
    }

    @Override
    public boolean isJoinedToChat(HttpServletRequest req) {
        
        final ChatSession chatSession = this.getChatSession(req, false);
        
        return chatSession == null ? false : chatSession.getStatus().isConnected();
    }
    
    @Override
    public ChatSession getChatSession(HttpServletRequest req, boolean createIfNone) {
        
        final ChatSession chatSession;
        
        final Object attr = req.getSession().getAttribute(
                AttributeNames.Session.COMETD_CHAT_SESSION);
        
        if(attr != null) {
            
            chatSession = (ChatSession)attr;
            
        }else if(createIfNone){
            
            chatSession = this.createChatSession(req);
            
        }else{
        
            chatSession = null;
        }
        
        return chatSession;
    }
    
    public ChatSession createChatSession(HttpServletRequest req) {
        
        final WebApplicationContext webAppCtx = getWebAppContext(req);

        final CometDProperties cometdProps = webAppCtx.getBean(CometDProperties.class);

// Servlet path may have an asterix e.g /cometd/*  therefore we use the literal endpoint i.e /cometd
//            final String cometdPath = webAppCtx.getBean(CometDProperties.class).getServletPath();
        final String url = servletUtil.getEndpointUrl(req, Endpoints.COMETD);
        final String room = servletUtil.requireNonNullOrEmpty(req, ParamNames.ROOM);
        final String user = servletUtil.requireNonNullOrEmpty(req, ParamNames.USER);
        LOG.debug("URL: {}, room: {}, user: {}", url, room, user);

        final ChatConfig chatConfig = webAppCtx.getBean(ChatConfig.class, 
                cometdProps.getDefaultChannel(), room, user);

        final Map<String, Object> transportOptions = new HashMap<>();

        final ChatSession chatSession = webAppCtx.getBean(ChatSession.class, url, transportOptions, chatConfig);
        
        req.getSession().setAttribute(AttributeNames.Session.COMETD_CHAT_SESSION, chatSession);
        
        return chatSession;
    }

    private WebApplicationContext getWebAppContext(HttpServletRequest req) {
        return getWebAppContext(req.getSession());
    }
    
    private WebApplicationContext getWebAppContext(HttpSession httpSession) {
        return WebApplicationContextUtils
                .getRequiredWebApplicationContext(httpSession.getServletContext());
    }
}
