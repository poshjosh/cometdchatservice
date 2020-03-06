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

import com.looseboxes.cometd.chatservice.AttributeNames;
import com.looseboxes.cometd.chatservice.ParamNames;
import com.looseboxes.cometd.chatservice.chat.ChatConfig;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.services.RequestUrl;
import com.looseboxes.cometd.chatservice.services.ServletUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.cometd.bayeux.server.BayeuxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public class ControllerServiceContextProviderImpl implements ControllerServiceContextProvider{
    
    private static final Logger LOG = LoggerFactory.getLogger(ControllerServiceContextProviderImpl.class);
    
    private final ServletUtil servletUtil;

    public ControllerServiceContextProviderImpl(ServletUtil servletUtil) {
        this.servletUtil = Objects.requireNonNull(servletUtil);
    }
    
    @Override
    public ControllerService.ServiceContext from(HttpServletRequest req){
    
        final ChatSession chatSession = getChatSession(req, true);
        
        final BayeuxServer bayeuxServer = (BayeuxServer)req.getServletContext()
                .getAttribute(BayeuxServer.ATTRIBUTE);
        
        final WebApplicationContext webAppCtx = getWebAppContext(req);

        final ControllerServiceContext bean = webAppCtx.getBean(ControllerServiceContext.class);

        final Map<String, Object> params = new HashMap();
        req.getParameterMap().forEach((k, v) -> {
            if(v != null) {
                if(v.length == 1) {
                    params.put(k, v[0]);
                }else if(v.length > 1){
                    params.put(k, v);
                }
            }
        });
        
        bean.setBayeuxServer(bayeuxServer);
        bean.setChatSession(chatSession);
        bean.setParameters(Collections.unmodifiableMap(params));
        
        return bean;
    }
    
    public ChatSession getChatSession(HttpServletRequest req, boolean createIfNone) {
        
        final ChatSession chatSession;
        
        final Object attr = req.getSession().getAttribute(
                AttributeNames.Session.COMETD_CHAT_SESSION);
        
        if(attr != null) {
            
            chatSession = (ChatSession)attr;
            
        }else if(createIfNone){
            
            chatSession = this.createChatSession(req);
            
            req.getSession().setAttribute(
                    AttributeNames.Session.COMETD_CHAT_SESSION, chatSession);

        }else{
        
            chatSession = null;
        }
        
        return chatSession;
    }
    
    public ChatSession createChatSession(HttpServletRequest req) {
        
        final WebApplicationContext webAppCtx = getWebAppContext(req);
        
        final RequestUrl reqUrl = webAppCtx.getBean(RequestUrl.class, req);

// Servlet path may have an asterix e.g /cometd/*  therefore we use the literal endpoint i.e /cometd
//            final String cometdPath = webAppCtx.getBean(CometDProperties.class).getServletPath();
        final String url = reqUrl.getEndpointUrl(Endpoints.COMETD);
        final String room = servletUtil.requireNonNullOrEmpty(req, ParamNames.ROOM);
        final String user = servletUtil.requireNonNullOrEmpty(req, ParamNames.USER);
        LOG.debug("URL: {}, room: {}, user: {}", url, room, user);

        final ChatConfig.Builder chatCfgBuilder = webAppCtx
                .getBean(ChatConfig.Builder.class);
        
        final ChatConfig chatConfig = chatCfgBuilder
                .room(room).user(user).build();

        final Map<String, Object> transportOptions = new HashMap<>();

        final ChatSession chatSession = webAppCtx.getBean(
                ChatSession.class, url, transportOptions, chatConfig);
        
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
