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

import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.server.LocalSessionImpl;
import org.cometd.server.ServerChannelImpl;
import org.cometd.server.ServerMessageImpl;
import org.cometd.server.ServerSessionImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

/**
 * @author USER
 */
public class ChatServiceTest {
    
    private final boolean logStackTrace = false;
    
    public ChatServiceTest() { }
    
    @Test
    public void configureChatStarStar_whenNullChannel_thenThrowRuntimeException() {
        System.out.println("configureChatStarStar_whenNullChannel_thenThrowRuntimeException");

        assertThrowsRuntimeException(
                () -> configureChatStarStar_whenCalledWithArg(null));
    }

    public ChatService configureChatStarStar_whenCalledWithArg(
            ConfigurableServerChannel channel) {

        final ChatService chatService = getChatService();

        chatService.configureChatStarStar(channel);

        return chatService;
    }

    @Test
    public void configureMembers_whenNullChannel_thenThrowRuntimeException() {
        System.out.println("configureMembers_whenNullChannel_thenThrowRuntimeException");

        assertThrowsRuntimeException(
                () -> configureMembers_whenCalledWithArg(null));
    }

    public ChatService configureMembers_whenCalledWithArg(
            ConfigurableServerChannel channel) {

        final ChatService chatService = getChatService();

        chatService.configureMembers(channel);

        return chatService;
    }

    @Test
    public void handleMembership_whenNullServerMessage_thenThrowRuntimeException() {
        System.out.println(
                "handleMembership_whenNullServerMessage_thenThrowRuntimeException");

        final ServerSession session = this.getServerSession();
        final ServerMessage message = null;

        assertThrowsRuntimeException(
                () -> handleMembership_whenCalledWithArgs(session, message));
    }
    
    @Test
    public void handleMembership_whenNullServerSession_thenThrowRuntimeException() {
        System.out.println(
                "handleMembership_whenNullServerSession_thenThrowRuntimeException");

        final ServerSession session = null;
        final ServerMessage message = this.getServerMessage();

        assertThrowsRuntimeException(
                () -> handleMembership_whenCalledWithArgs(session, message));
    }

    public ChatService handleMembership_whenCalledWithArgs(
            ServerSession serverSession, ServerMessage message) {

        final ChatService chatService = getChatService();

        chatService.handleMembership(serverSession, message);

        return chatService;
    }

    @Test
    public void configurePrivateChat_whenNullChannel_thenThrowRuntimeException() {
        System.out.println("configurePrivateChat_whenNullChannel_thenThrowRuntimeException");

        assertThrowsRuntimeException(
                () -> configurePrivateChat_whenCalledWithArg(null));
    }

    public ChatService configurePrivateChat_whenCalledWithArg(
            ConfigurableServerChannel channel) {

        final ChatService chatService = getChatService();

        chatService.configurePrivateChat(channel);

        return chatService;
    }
    
    @Test
    public void privateChat_whenNullServerMessage_thenThrowRuntimeException() {
        System.out.println(
                "privateChat_whenNullServerMessage_thenThrowRuntimeException");

        final ServerSession session = this.getServerSession();
        final ServerMessage message = null;

        assertThrowsRuntimeException(
                () -> privateChat_whenCalledWithArgs(session, message));
    }
    
    @Test
    public void privateChat_whenNullServerSession_thenThrowRuntimeException() {
        System.out.println(
                "privateChat_whenNullServerSession_thenThrowRuntimeException");

        final ServerSession session = null;
        final ServerMessage message = this.getServerMessage();
        
        assertThrowsRuntimeException(
                () -> privateChat_whenCalledWithArgs(session, message));
    }

    public void assertThrowsRuntimeException(Executable executable) {
        this.assertThrows(executable, RuntimeException.class);
    }
    
    public void assertThrows(Executable executable, Class exceptionType) {

        final Throwable thrown = Assertions.assertThrows(
                exceptionType, 
                executable,
                "Should throw " + exceptionType.getName() + ", but execution completed");
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }

    public ChatService privateChat_whenCalledWithArgs(
            ServerSession serverSession, ServerMessage message) {

        final ChatService chatService = getChatService();

        chatService.privateChat(serverSession, message);

        return chatService;
    }
    
    public ChatService getChatService() {
        return new ChatService();
    }
    
    public ConfigurableServerChannel getConfigurableServerChannel(String id) {
        return new ServerChannelImpl(this.getBayeuxServer(), new ChannelId(id)){
        
        };
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
/**

    @Test
    public void configureChatStarStar_whenValidChannel_thenListenerIsAddedToChannel() {
        System.out.println("configureChatStarStar_whenValidChannel_thenListenerIsAddedToChannel");

        final ConfigurableServerChannel channel = 
                this.configureChatStarStar_whenCalledWithValidChannel();

        verify(channel).addListener(isA(ConfigurableServerChannel.ServerChannelListener.class));
    }

    @Test
    public void configureChatStarStar_whenValidChannel_thenGrantAllAuthorizerIsAddedToChannel() {
        System.out.println("configureChatStarStar_whenValidChannel_thenGrantAllAuthorizerIsAddedToChannel");

        final ConfigurableServerChannel channel = 
                this.configureChatStarStar_whenCalledWithValidChannel();

        verify(channel).addAuthorizer(GrantAuthorizer.GRANT_ALL);
    }

    public ConfigurableServerChannel configureChatStarStar_whenCalledWithValidChannel() {

        final ConfigurableServerChannel channel = this.getConfigurableServerChannel();
        
        return this.configureChatStarStar_whenCalledWithArg(channel);
    }

    @Test
    public void configureMembers_whenValidChannel_thenListenerIsAddedToChannel() {
        System.out.println("configureMembers_whenValidChannel_thenListenerIsAddedToChannel");

        final ConfigurableServerChannel channel = this.configureMembers_whenCalledWithValidChannel();

        verify(channel).setPersistent(true);
    }

    @Test
    public void configureMembers_whenValidChannel_thenGrantPublishAuthorizerIsAddedToChannel() {
        System.out.println("configureMembers_whenValidChannel_thenGrantPublishAuthorizerIsAddedToChannel");

        final ConfigurableServerChannel channel = 
                this.configureChatStarStar_whenCalledWithValidChannel();

        verify(channel).addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
    }

    public ConfigurableServerChannel configureMembers_whenCalledWithValidChannel() {

        final ConfigurableServerChannel channel = this.getConfigurableServerChannel();
        
        return this.configureChatStarStar_whenCalledWithArg(channel);
    }
 * 
 */