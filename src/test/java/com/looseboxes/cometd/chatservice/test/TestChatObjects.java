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
package com.looseboxes.cometd.chatservice.test;

import com.looseboxes.cometd.chatservice.chat.Chat;
import com.looseboxes.cometd.chatservice.chat.ChatConfig;
import com.looseboxes.cometd.chatservice.chat.ChatService;
import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.chat.ChatSessionImpl;
import com.looseboxes.cometd.chatservice.chat.ClientSessionImpl;
import java.util.Objects;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.LocalSessionImpl;
import org.cometd.server.ServerChannelImpl;
import org.cometd.server.ServerMessageImpl;
import org.cometd.server.ServerSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class TestChatObjects {
    
    private static final Logger LOG = LoggerFactory.getLogger(TestChatObjects.class);
    
    private final TestConfig testConfig;

    public TestChatObjects(TestConfig testConfig) {
        this.testConfig = Objects.requireNonNull(testConfig);
    }
    
    public Message.Mutable sendSuccessMessage(String clientId, ClientSession.MessageListener ml) { 
        return sendSuccessMessage(clientId, null, ml);
    }

    public Message.Mutable sendSuccessMessage(String clientId, Object data, ClientSession.MessageListener ml) { 
        LOG.trace("Sending success message");
        final Message.Mutable message = this.createSuccessMessage(clientId, data);
        ml.onMessage(message);
        LOG.trace("Done sending message: {}", message);
        return message;
    }
    
    public ServerMessage.Mutable createSuccessMessage(String clientId, Object data) {
        return this.createMessage(true, clientId, data);
    }
    
    public ServerMessage.Mutable createSuccessMessage(String clientId, String key, Object value) {
        return this.createMessage(true, clientId, key, value);
    }

    public ServerMessage.Mutable createMessage(boolean success, String clientId, Object data) {
        return createMessage(success, clientId, "data", data);
    }
    
    public ServerMessage.Mutable createMessage(boolean success, 
            String clientId, String key, Object value) {
        final ServerMessage.Mutable msg = new ServerMessageImpl();
        msg.put("id", Long.toHexString(System.currentTimeMillis()));
        msg.put("clientId", clientId);
        msg.put("meta", true);
        msg.put("successful", success);
        if(value != null) {
            msg.put(key, value);
        }
        msg.put("empty", value == null);
        msg.put("publishReply", false);
        return msg;
    }

    public ChatService getChatService() {
        return new ChatService();
    }
    
    public ChatSession getChatSession(String user, String room) {
        final ChatConfig chatConfig = testConfig.chatConfig().chatConfigBuilder()
                .user(user).room(room).build();
        return getChatSession(this.getClientSession(), chatConfig);
    }
    
    public ChatSession getChatSession(ClientSession client, ChatConfig chatConfig) {
        Objects.requireNonNull(client);
        Objects.requireNonNull(chatConfig);
        return new ChatSessionImpl(client, chatConfig);
    }

    public ChatSession getChatSession(int port) {
        return this.testConfig.chatConfig().chatSession(
                this.testConfig.testUrl().getChatUrl(port), this.getChatConfig());
    }

    public ClientSession getClientSession() {
        return new ClientSessionImpl(this);
    }
    
    public ChatConfig getChatConfig() {
        return ChatConfig.builder()
                .logLevel(Chat.LOG_LEVEL_VALUES.DEBUG)
                .channel("/chat/privatechat")
                .room("test_room")
                .user("test_user").build();
    }

    public ServerChannel getServerChannel(String id) {
        return new ServerChannelImpl(this.getBayeuxServer(), new ChannelId(id)){
        
        };
    }
    
    public ClientSession getClientSession(int port){
        return this.testConfig.chatConfig().clientSession(
                this.testConfig.testUrl().getChatUrl(port));
    }
    
    public ServerSession getServerSession(){
        return new ServerSessionImpl(
                this.getBayeuxServer(), this.getLocalSession(), this.getUniqueId());
    }
    
    public LocalSessionImpl getLocalSession(){
        return new LocalSessionImpl(this.getBayeuxServer(), this.getUniqueId());
    }
    
    public String getUniqueId() {
        return Long.toHexString(System.currentTimeMillis());
    }

    public BayeuxServerImpl getBayeuxServer() {
        return new BayeuxServerImpl();
    }
    
    public ServerMessage getServerMessage() {
        return new ServerMessageImpl();
    }
}
