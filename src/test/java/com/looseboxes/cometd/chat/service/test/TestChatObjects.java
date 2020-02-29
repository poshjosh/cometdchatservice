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
package com.looseboxes.cometd.chat.service.test;

import com.looseboxes.cometd.chat.service.ChatService;
import java.util.Objects;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.LocalSessionImpl;
import org.cometd.server.ServerChannelImpl;
import org.cometd.server.ServerMessageImpl;
import org.cometd.server.ServerSessionImpl;

/**
 * @author USER
 */
public class TestChatObjects {
    
    private final TestConfig testConfig;

    public TestChatObjects(TestConfig testConfig) {
        this.testConfig = Objects.requireNonNull(testConfig);
    }
    
    public ChatService getChatService() {
        return new ChatService();
    }
    
    public ConfigurableServerChannel getConfigurableServerChannel(String id) {
        return new ServerChannelImpl(this.getBayeuxServer(), new ChannelId(id)){
        
        };
    }
    
    public ClientSession getClientSession(int port){
        return this.testConfig.appConfig().clientSession(
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
