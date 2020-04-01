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
package com.looseboxes.cometd.chatservice.chat;

import com.looseboxes.cometd.chatservice.test.ChatUtil;
import com.looseboxes.cometd.chatservice.test.EndpointRequestParams;
import com.looseboxes.cometd.chatservice.test.TestUrls;
import java.util.Objects;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.LocalSessionImpl;
import org.cometd.server.ServerChannelImpl;
import org.cometd.server.ServerMessageImpl;
import org.cometd.server.ServerSessionImpl;
import org.cometd.server.filter.DataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@TestConfiguration
public class TestChatConfiguration extends ChatConfiguration{
    
    private static final Logger LOG = LoggerFactory.getLogger(TestChatConfiguration.class);
    
    private final TestUrls testUrls;

    public TestChatConfiguration() {
        this("", new EndpointRequestParams());
    }

    public TestChatConfiguration(String contextPath, 
            EndpointRequestParams endpointReqParams) {
        this(new TestUrls(contextPath, endpointReqParams));
    }
    
    public TestChatConfiguration(TestUrls testUrls) {
        this.testUrls = Objects.requireNonNull(testUrls);
    }

    @Bean public ChatUtil chatUtil() {
        return new ChatUtil();
    }
    
    @Bean @Scope("prototype") public ChatService getChatService() {
        return new ChatService();
    }
    
    @Bean @Scope("prototype") public ChatSession getChatSession(
            String user, String room) {
        return this.chatSessionProvider().getChatSession(user, room);
    }
    
    @Bean public ChatSessionProvider chatSessionProvider() {
        return new ChatSessionProviderImpl(this);
    }

    @Bean @Scope("prototype") public ChatSession getChatSession(
            ClientSession client, ChatConfig chatConfig) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(chatConfig);
        final ChatSession chatSession = new ChatSessionImpl(client, chatConfig);
        LOG.debug("{}", chatSession);
        return chatSession;
    }

    @Bean @Scope("prototype") public ChatSession getChatSession(int port) {
        return this.chatSession(
                this.testUrls.getChatUrl(port), this.getChatConfig());
    }

    @Bean @Scope("prototype") public ClientSession getClientSession() {
        return new ClientSessionImpl(this.chatUtil());
    }
    
    @Bean @Scope("prototype") public ChatConfig getChatConfig() {
        return ChatConfig.builder()
                .logLevel(Chat.LOG_LEVEL_VALUES.DEBUG)
                .channel("/chat/privatechat")
                .room("test_room")
                .user("test_user").build();
    }

    @Bean @Scope("prototype") public ServerChannel getServerChannel(String id) {
        return new ServerChannelImpl(this.getBayeuxServer(), new ChannelId(id)){
        
        };
    }
    
    @Bean @Scope("prototype") public ClientSession getClientSession(int port){
        return this.clientSession(
                this.testUrls.getChatUrl(port));
    }
    
    @Bean @Scope("prototype") public ServerSession getServerSession(){
        return new ServerSessionImpl(
                this.getBayeuxServer(), this.getLocalSession(), this.getUniqueId());
    }
    
    @Bean @Scope("prototype") public LocalSessionImpl getLocalSession(){
        return new LocalSessionImpl(this.getBayeuxServer(), this.getUniqueId());
    }
    
    public String getUniqueId() {
        return Long.toHexString(System.currentTimeMillis());
    }

    @Bean @Scope("prototype") public BayeuxServerImpl getBayeuxServer() {
        return new BayeuxServerImpl();
    }
    
    @Bean @Scope("prototype") public ServerMessage getServerMessage() {
        return new ServerMessageImpl();
    }

    @Bean @Scope("prototype") public ServerChannel.MessageListener 
    getServerChannelMessageListener(){
        return new MessageListenerWithDataFilters(getDataFilterDummy());
    }

//        public ServerChannel.MessageListener getServerChannelMessageListenerDummy(){
//            return new ServerChannelMessageListenerDummy();
//        }

    @Bean @Scope("prototype") public DataFilter getDataFilterDummy() {
        return new DataFilterDummy();
    }

    public static interface ChatSessionProvider{
        ChatSession getChatSession(String user, String room);
    }
    private static final class ChatSessionProviderImpl 
            implements ChatSessionProvider{
        
        private final TestChatConfiguration testChatConfiguration;

        public ChatSessionProviderImpl() {
            this(new TestChatConfiguration());
        }

        public ChatSessionProviderImpl(TestChatConfiguration testChatConfiguration) {
            this.testChatConfiguration = Objects.requireNonNull(testChatConfiguration);
        }
        
        @Override
        public ChatSession getChatSession(String user, String room) {
            final ChatConfig chatConfig = testChatConfiguration.chatConfigBuilder()
                    .channel("/service/privatechat")
                    .logLevel("debug")
                    .websocketEnabled(false)
                    .user(user)
                    .room(room).build();
            LOG.debug("{}", chatConfig);
            return testChatConfiguration.getChatSession(
                    testChatConfiguration.getClientSession(), chatConfig);
        }
    }
    
    private static final class ServerChannelMessageListenerDummy 
            implements ServerChannel.MessageListener{
        @Override
        public boolean onMessage(ServerSession sender, 
                ServerChannel channel, 
                ServerMessage.Mutable message) {
            Objects.requireNonNull(sender);
            Objects.requireNonNull(channel);
            return true;
        }
    }

    private static final class DataFilterDummy implements DataFilter{
        @Override
        public Object filter(ServerSession ss, ServerChannel sc, Object o) throws DataFilter.AbortException {
            Objects.requireNonNull(ss);
            Objects.requireNonNull(sc);
            return o;
        }
    }
}
