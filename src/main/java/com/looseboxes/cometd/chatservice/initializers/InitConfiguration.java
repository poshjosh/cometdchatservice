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
package com.looseboxes.cometd.chatservice.initializers;

import com.looseboxes.cometd.chatservice.RestTemplateForGet;
import com.looseboxes.cometd.chatservice.RestTemplateForGetImpl;
import com.looseboxes.cometd.chatservice.chat.BadWordFilter;
import com.looseboxes.cometd.chatservice.chat.MembersService;
import com.looseboxes.cometd.chatservice.chat.MembersServiceInMemoryCache;
import com.looseboxes.cometd.chatservice.chat.MessageListenerWithDataFilters;
import com.looseboxes.cometd.chatservice.SafeContentService;
import com.looseboxes.cometd.chatservice.SafeContentServiceImpl;
import java.util.Collections;
import org.cometd.annotation.Configure;
import org.cometd.annotation.Listener;
import org.cometd.annotation.RemoteCall;
import org.cometd.annotation.Service;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.cometd.server.ext.AcknowledgedMessagesExtension;
import org.cometd.server.ext.TimesyncExtension;
import org.cometd.server.filter.NoMarkupFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

/**
 * @author USER
 */
@Configuration
public class InitConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(InitConfiguration.class);
    
    @Bean public ChatServerInitializer bayeuxInitializer(
            @Value("${services.safecontent.url}") String url,
            @Value("${services.safecontent.endpoint.flag}") String endpoint,
            @Value("${services.safecontent.endpoint.flag.timeout}") long timeout,
            @Value("${cometd.defaultChannel}") String channel) {
        
        final ChatServerInitializer bayeuxInit = (bayeuxServer) -> {
            
            final SafeContentService safeContentService = 
                    safeContentService(url, endpoint, timeout);
            
            addExtensionsToBayeuxServer().apply(bayeuxServer, 
                    timesyncExtension(), acknowledgedMessagesExtension());
            addOptionsToBayeuxServer().apply(bayeuxServer, 
                    membersService(), messageListenerWithDataFilters(safeContentService));
            createDefaultChannelsIfAbsent().apply(bayeuxServer, channel);
//            processAnnotatedServices().apply(bayeuxServer, 
//                    echoRPC(), monitor());
            dumpBayeuxServerState().apply(bayeuxServer, Collections.EMPTY_LIST);
        };
        
        return bayeuxInit;
    }
    
    @Bean public MessageListenerWithDataFilters messageListenerWithDataFilters(
            SafeContentService safeContentService) {
        return new MessageListenerWithDataFilters(
                noMarkupFilter(), badWordFilter(safeContentService));
    }
    
    @Bean public BadWordFilter badWordFilter(SafeContentService safeContentService) {
        return new BadWordFilter(safeContentService);
    }
    
    @Bean public NoMarkupFilter noMarkupFilter() {
        return new NoMarkupFilter();
    }
    
    @Bean public DumpChatServerState dumpBayeuxServerState() {
        return new DumpChatServerState();
    }

    @Bean public MembersService membersService() {
        return new MembersServiceInMemoryCache();
    }

    @Bean @Scope("singleton") public SafeContentService safeContentService(
            @Value("${services.safecontent.url}") String url,
            @Value("${services.safecontent.endpoint.flag}") String endpoint,
            @Value("${services.safecontent.endpoint.flag.timeout}") long timeout) {
        LOG.debug("${services.safecontent} .url={}, .endpoint.flag={}, .endpoint.flag.timeout={}", 
                url, endpoint, timeout);
        return new SafeContentServiceImpl(this.restTemplateForGet(), url, endpoint, timeout);
    }
    
    @Bean public RestTemplateForGet restTemplateForGet() {
        return new RestTemplateForGetImpl(this.restTemplate());
    }

    @LoadBalanced @Bean public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean public AddExtensionsToChatServer addExtensionsToBayeuxServer() {
        return new AddExtensionsToChatServer();
    }
    
    @Bean public TimesyncExtension timesyncExtension() {
        return new TimesyncExtension();
    }
    
    @Bean public AcknowledgedMessagesExtension acknowledgedMessagesExtension() {
        return new AcknowledgedMessagesExtension();
    }
    
    @Bean public AddOptionsToChatServer addOptionsToBayeuxServer() {
        return new AddOptionsToChatServer();
    }
    
    @Bean public CreateDefaultChannelsIfAbsent createDefaultChannelsIfAbsent() {
        return new CreateDefaultChannelsIfAbsent();
    }
    
    @Bean public ProcessAnnotatedServices processAnnotatedServices() {
        return new ProcessAnnotatedServices();
    }
    
    @Bean public InitConfiguration.EchoRPC echoRPC() {
        return new InitConfiguration.EchoRPC();
    }
    
    @Bean public InitConfiguration.Monitor monitor() {
        return new InitConfiguration.Monitor();
    }
    
    @Service("echo")
    public static final class EchoRPC {
        
        @Configure("/service/echo")
        private void configureEcho(ConfigurableServerChannel channel) {
            channel.addAuthorizer(GrantAuthorizer.GRANT_SUBSCRIBE_PUBLISH);
        }

        @RemoteCall("echo")
        public void doEcho(RemoteCall.Caller caller, Object data) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("ECHO from " + caller.getServerSession() + ": " + data);
            }
            caller.result(data);
        }
    }

    @Service("monitor")
    public static class Monitor {
        @Listener("/meta/subscribe")
        public void monitorSubscribe(ServerSession session, ServerMessage message) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Monitored Subscribe from " + session + " for " + 
                        message.get(Message.SUBSCRIPTION_FIELD));
            }
        }

        @Listener("/meta/unsubscribe")
        public void monitorUnsubscribe(ServerSession session, ServerMessage message) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("Monitored Unsubscribe from " + session + " for " + 
                        message.get(Message.SUBSCRIPTION_FIELD));
            }
        }

        @Listener("/meta/*")
        public void monitorMeta(ServerSession session, ServerMessage message) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(message.toString());
            }
        }
    }
}
