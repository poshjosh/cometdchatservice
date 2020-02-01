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

import com.looseboxes.cometd.chat.service.handlers.response.JsonResponseHandler;
import com.looseboxes.cometd.chat.service.handlers.request.RequestHandlerFactory;
import com.looseboxes.cometd.chat.service.handlers.request.RequestHandler;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import java.io.IOException;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Listener;
import org.cometd.annotation.RemoteCall;
import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.annotation.Service;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.cometd.server.ext.AcknowledgedMessagesExtension;
import org.cometd.server.ext.TimesyncExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class CometDServlet extends HttpServlet {
    
    private static final Logger LOG = LoggerFactory.getLogger(CometDServlet.class);
    
    @Override
    public void init() throws ServletException {
        
        super.init();
        
        final ServletContext servletContext = getServletContext();
        
        final BayeuxServerImpl bayeux = (BayeuxServerImpl)servletContext.getAttribute(BayeuxServer.ATTRIBUTE);

        if (bayeux == null) {
            throw new UnavailableException("No BayeuxServer!");
        }

        // Create extensions
        bayeux.addExtension(new TimesyncExtension());
        bayeux.addExtension(new AcknowledgedMessagesExtension());

        // Deny unless granted
        bayeux.createChannelIfAbsent("/**", (ServerChannel.Initializer)channel -> channel.addAuthorizer(GrantAuthorizer.GRANT_NONE));
//        bayeux.createChannelIfAbsent("/**", (ServerChannel.Initializer)channel -> channel.addAuthorizer(GrantAuthorizer.GRANT_ALL));

        // Allow anybody to handshake
        bayeux.getChannel(ServerChannel.META_HANDSHAKE).addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
//        bayeux.getChannel(ServerChannel.META_HANDSHAKE).addAuthorizer(GrantAuthorizer.GRANT_ALL);

        ServerAnnotationProcessor processor = new ServerAnnotationProcessor(bayeux);
        processor.process(new EchoRPC());
        processor.process(new Monitor());

        final CometDProperties cometdProperties = this.getBean(servletContext, CometDProperties.class);
        
        bayeux.createChannelIfAbsent(cometdProperties.getDefaultChannel(), new ConfigurableServerChannel.Initializer.Persistent());
//        bayeux.createChannelIfAbsent(cometdProperties.getDefaultChannel(), (ServerChannel.Initializer)channel -> channel.addAuthorizer(GrantAuthorizer.GRANT_ALL));

        if (LOG.isDebugEnabled()) {
            LOG.debug(bayeux.dump());
        }
    }

    @Service("echo")
    public static class EchoRPC {
        
        @Configure("/service/echo")
        private void configureEcho(ConfigurableServerChannel channel) {
            channel.addAuthorizer(GrantAuthorizer.GRANT_SUBSCRIBE_PUBLISH);
        }

        @RemoteCall("echo")
        public void doEcho(RemoteCall.Caller caller, Object data) {
            LOG.info("ECHO from " + caller.getServerSession() + ": " + data);
            caller.result(data);
        }
    }

    @Service("monitor")
    public static class Monitor {
        @Listener("/meta/subscribe")
        public void monitorSubscribe(ServerSession session, ServerMessage message) {
            LOG.info("Monitored Subscribe from " + session + " for " + message.get(Message.SUBSCRIPTION_FIELD));
        }

        @Listener("/meta/unsubscribe")
        public void monitorUnsubscribe(ServerSession session, ServerMessage message) {
            LOG.info("Monitored Unsubscribe from " + session + " for " + message.get(Message.SUBSCRIPTION_FIELD));
        }

        @Listener("/meta/*")
        public void monitorMeta(ServerSession session, ServerMessage message) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(message.toString());
            }
        }
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        
        final RequestHandlerFactory reqHandlerFactory = this.getBean(req, RequestHandlerFactory.class);
        
        final RequestHandler<Response> reqHandler = reqHandlerFactory.getRequestHandler(req, res);

        LOG.debug("{}", reqHandler);
        
        final JsonResponseHandler resHandler = this.getBean(req, JsonResponseHandler.class);
        
        Response data = null;
        try{
            
            data = reqHandler.process(req, res);
            
            resHandler.onSuccess(req, res, data);
            
        }catch(RuntimeException e0) {
            
            final String msg = "Exception processing request";
            
            LOG.warn(msg, e0);
            
            final ResponseBuilder erp = this.getBean(req, ResponseBuilder.class);
            
            data = erp.buildErrorResponse(msg, e0);
            
            resHandler.onFailure(req, res, data);
            
        }finally{
        
            LOG.debug("{}", data);

            resHandler.onAlways(req, res, data);
        }
    }

    private <T extends Object> T getBean(ServletRequest req, Class<T> type) throws BeansException {
        return this.getBean(req.getServletContext(), type);
    }
    
    private <T extends Object> T getBean(ServletContext servletContext, Class<T> type) throws BeansException {
        return this.getWebApplicationContext(servletContext).getBean(type);
    }

    private WebApplicationContext getWebApplicationContext(ServletRequest req) {
        return this.getWebApplicationContext(req.getServletContext());
    }
    
    private WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
