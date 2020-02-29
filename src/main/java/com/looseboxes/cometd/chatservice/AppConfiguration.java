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

import com.looseboxes.cometd.chatservice.chat.ChatConfig;
import com.looseboxes.cometd.chatservice.chat.HttpClientInitializer;
import com.looseboxes.cometd.chatservice.chat.ChatSessionImpl;
import com.looseboxes.cometd.chatservice.chat.HttpClientInitializerImpl;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.looseboxes.cometd.chatservice.handlers.ServletUtil;
import com.looseboxes.cometd.chatservice.handlers.ChatRequestService;
import com.looseboxes.cometd.chatservice.handlers.ChatRequestServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;

/**
 * @author USER
 */
@Configuration
public class AppConfiguration {

//    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);
    
    public AppConfiguration(){}
    
    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheCacheManager().getObject());
    }
 
    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        final EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        factory.setConfigLocation(new ClassPathResource("ehcache.xml"));
        factory.setShared(true);
        return factory;
    }
    
    @Bean public ChatRequestService chatRequestService() {
        return new ChatRequestServiceImpl(this.servletUtil());
    }

    @Bean @Scope("prototype") public ChatSession chatSession(
            String url, String channel, String room, String user) {
        // Do not use an unmodifiable map
        return this.createChatSession(url, new HashMap<>(), this.chatConfig(channel, room, user));
    }

    @Bean @Scope("prototype") public ChatSession chatSession(String url, ChatConfig chatConfig) {
        // Do not use an unmodifiable map
        return this.createChatSession(url, new HashMap<>(), chatConfig);
    }
    
    @Bean @Scope("prototype") public ChatSession chatSession(
            String url, Map<String, Object> transportOptions, ChatConfig chatConfig) {
        return this.createChatSession(url, transportOptions, chatConfig);
    }

    private ChatSession createChatSession(
            String url, Map<String, Object> transportOptions, ChatConfig chatConfig) {
        final ClientSession clientSession = this.clientSession(url, transportOptions);
        return new ChatSessionImpl(clientSession, chatConfig);
    }

    @Bean @Scope("prototype") public ChatConfig chatConfig(String channel, String room, String user) {
        return new ChatConfig(channel, room, user);
    }

    @Bean @Scope("prototype") public ClientSession clientSession(String url) {
        // Do not use an unmodifiable map
        return this.createClientSession(url, new HashMap<>());
    }
    
    @Bean @Scope("prototype") public ClientSession clientSession(
            String url, Map<String, Object> transportOptions) {
        return this.createClientSession(url, transportOptions);
    }
    
    private ClientSession createClientSession(
            String url, Map<String, Object> transportOptions) {
        //@TODO use a validator for URL. Check for null, empty and malformed
        final ClientTransport transport = this.clientTransport(transportOptions);
        return new BayeuxClient(url, transport);
    }

    @Bean @Scope("prototype") public ClientTransport clientTransport() {
        // Do not use an unmodifiable map
        return this.createClientTransport(new HashMap<>());
    }
    
    /**
     * @param transportOptions The transport options. <b>Do not use an unmodifiable map</b>
     * @return 
     */
    @Bean @Scope("prototype") public ClientTransport clientTransport(
            Map<String, Object> transportOptions) {
        return this.createClientTransport(transportOptions);
    }

    private ClientTransport createClientTransport(Map<String, Object> transportOptions) {
        final HttpClient httpClient = this.httpClient();
        this.httpClientConfigurer().init(httpClient);
        return new LongPollingTransport(transportOptions, httpClient);
    }
    
    @Bean public HttpClientInitializer httpClientConfigurer() {
        return new HttpClientInitializerImpl();
    }

    @Bean @Scope("prototype") public HttpClient httpClient() {
        return new HttpClient();
    }
    
    @Bean @Scope("prototype") public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    
    @Bean public ServletUtil servletUtil() {
        return new ServletUtil();
    }

    @Bean public TerminateBean terminateBean() {
        return new TerminateBean();
    }
}
