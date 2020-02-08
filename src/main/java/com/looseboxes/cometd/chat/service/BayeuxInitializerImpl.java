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

import java.util.Objects;
import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
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

/**
 * @author USER
 */
public final class BayeuxInitializerImpl implements BayeuxInitializer{
    
    private static final Logger LOG = LoggerFactory.getLogger(BayeuxInitializerImpl.class);
    
    private final MembersService membersService;
    
    private final SafeContentService safeContentService;

    public BayeuxInitializerImpl(
            MembersService membersService, SafeContentService safeContentService) {
        this.membersService = Objects.requireNonNull(membersService);
        this.safeContentService = Objects.requireNonNull(safeContentService);
    }
    
    @Override
    public void init(ServletContext servletContext) throws UnavailableException{
        
        final BayeuxServer bayeux = (BayeuxServer)servletContext.getAttribute(BayeuxServer.ATTRIBUTE);

        if (bayeux == null) {
            throw new UnavailableException("CometD BayeuxServer unavailable!");
        }
        
        bayeux.setOption(MembersService.class.getSimpleName(), membersService);
        bayeux.setOption(SafeContentService.class.getSimpleName(), safeContentService);

        // Create extensions
        bayeux.addExtension(new TimesyncExtension());
        bayeux.addExtension(new AcknowledgedMessagesExtension());

        // Deny unless granted
        bayeux.createChannelIfAbsent("/**", (ServerChannel.Initializer)channel -> channel.addAuthorizer(GrantAuthorizer.GRANT_NONE));

        // Allow anybody to handshake
        bayeux.getChannel(ServerChannel.META_HANDSHAKE).addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);

        ServerAnnotationProcessor processor = new ServerAnnotationProcessor(bayeux);
        processor.process(new BayeuxInitializerImpl.EchoRPC());
        processor.process(new BayeuxInitializerImpl.Monitor());

        final CometDProperties cometdProperties = this.getBean(servletContext, CometDProperties.class);
        
        bayeux.createChannelIfAbsent(cometdProperties.getDefaultChannel(), new ConfigurableServerChannel.Initializer.Persistent());

        if (LOG.isDebugEnabled()) {
            if(bayeux instanceof BayeuxServerImpl) {
                LOG.debug(((BayeuxServerImpl)bayeux).dump());
            }
        }
    }

    @Service("echo")
    public static final class EchoRPC {
        
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

    private <T extends Object> T getBean(ServletContext servletContext, Class<T> type) throws BeansException {
        return this.getWebApplicationContext(servletContext).getBean(type);
    }
    
    private WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }
}
