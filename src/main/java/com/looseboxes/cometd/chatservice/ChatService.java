/*
 * Copyright (c) 2008-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.looseboxes.cometd.chatservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.cometd.annotation.Configure;
import org.cometd.annotation.Listener;
import org.cometd.annotation.Service;
import org.cometd.annotation.Session;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.Promise;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("chat")
public final class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
    
    @Inject
    private BayeuxServer bayeuxServer;
    
    @Session
    private ServerSession serverSession;

    @Configure({"/chat/**", "/members/**"})
    protected void configureChatStarStar(ConfigurableServerChannel channel) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("configureChatStarStar(ConfigurableServerChannel={})", channel);
        }
        Objects.requireNonNull(channel);
        final ConfigurableServerChannel.ServerChannelListener listener =
                getServerChannelListener();
        channel.addListener(listener);
        channel.addAuthorizer(GrantAuthorizer.GRANT_ALL);
    }

    @Configure(Chat.MEMBERS_SERVICE_CHANNEL)
    protected void configureMembers(ConfigurableServerChannel channel) {
        if(LOG.isDebugEnabled()) {
            LOG.debug("configureMembers(ConfigurableServerChannel={})", channel);
        }
        Objects.requireNonNull(channel);
        channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
        channel.setPersistent(true);
    }

    @Listener(Chat.MEMBERS_SERVICE_CHANNEL)
    public void handleMembership(ServerSession session, ServerMessage message) {
        try{
            if(LOG.isDebugEnabled()) {
                LOG.debug("handleMembership(ServerSession, ServerMessage={})", message);
            }
            Objects.requireNonNull(session);
            Objects.requireNonNull(message);

            final Map<String, Object> data = getData(message);

            final String room = data == null ? null : getRoom(data, null);

            final MembersService membersSvc = this.getMembersService();
            
            final String userName = (String)data.get(Chat.USER);

            membersSvc.addMember(room, userName, session.getId());

            session.addListener((ServerSession.RemoveListener)(sess, timeout) -> {
                membersSvc.removeMemberByValue(room, sess.getId());
                broadcastMembers(room);
            });

            broadcastMembers(room);
            
        }catch(Exception e) {
            LOG.warn("Exception while handling membership. ServerMessage: " + message, e);
        }
    }

    private boolean broadcastMembers(String room) {
        final Map<String, String> roomMembers = this.getMembersService().getMembers(room);
        if(roomMembers == null || roomMembers.isEmpty()) {
            return false;
        }
        final Set<String> users = roomMembers.keySet();
        // Broadcast the new members list
        ClientSessionChannel channel = this.getServerSession()
                .getLocalSession().getChannel("/members/" + room);
        channel.publish(users);
        return true;
    }

    @Configure(ClientSessionChannel.SERVICE+"/privatechat")
    protected void configurePrivateChat(ConfigurableServerChannel channel) {
        LOG.debug("configurePrivateChat(ConfigurableServerChannel={})", channel);
        Objects.requireNonNull(channel);
        final ConfigurableServerChannel.ServerChannelListener listener =
                getServerChannelListener();
        channel.setPersistent(true);
        channel.addListener(listener);
        channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
    }

    @Listener(ClientSessionChannel.SERVICE+"/privatechat")
    public void privateChat(ServerSession session, ServerMessage message) {
        try{
            if(LOG.isTraceEnabled()) {
                LOG.trace("privateChat(ServerSession, ServerMessage={})", message);
            }
            Objects.requireNonNull(session);
            Objects.requireNonNull(message);
            final Map<String, Object> data = getData(message);

            final String room = data == null ? null : getRoom(data, null);

            final MembersService membersSvc = this.getMembersService();

            final String[] peerNames = this.getPeerNames(data);
            final ArrayList<ServerSession> peers = new ArrayList<>(peerNames.length);

            final BayeuxServer _bayeux = this.getBayeuxServer();
            
            for (String peerName : peerNames) {
                
                final String peerId = membersSvc.getMembersValue(room, peerName);
                
                if (peerId != null) {
                    ServerSession peer = _bayeux.getSession(peerId);
                    if (peer != null) {
                        peers.add(peer);
                    }
                }
            }

            if (peers.size() > 0) {
                Map<String, Object> chat = new HashMap<>();
                String text = (String)data.get(Chat.CHAT);
                chat.put(Chat.CHAT, text);
                chat.put(Chat.USER, data.get(Chat.USER));
                chat.put(Chat.SCOPE, "private");
                ServerMessage.Mutable forward = _bayeux.newMessage();
                forward.setChannel("/chat/" + room);
                forward.setId(message.getId());
                forward.setData(chat);

                // test for lazy messages
                if (text.lastIndexOf("lazy") > 0) {
                    forward.setLazy(true);
                }
                
                if(LOG.isTraceEnabled()) {
                    LOG.trace("Forwarding to {} peers, message:\n{}", peers.size(), forward);
                }
            
                final ServerSession _session = this.getServerSession();
                
                for (ServerSession peer : peers) {
                    if (peer != session) {
                        peer.deliver(_session, forward, Promise.noop());
                    }
                }
                
                session.deliver(_session, forward, Promise.noop());
            }
        }catch(Exception e) {
            LOG.warn("Exception while handling private chat. ServerMessage: " + message, e);
        }
    }
    
    private String [] getPeerNames(final Map<String, Object> data) {
        final String peer = data == null ? null : ((String)data.get(Chat.PEER));
        return peer == null || peer.isEmpty() ? new String[0] : peer.split(",");
    }
    
    private String getRoom(final Map<String, Object> data, String resultIfNone) {
        final String raw = data == null ? null : ((String)data.get(Chat.ROOM));
        final String result = raw == null ? resultIfNone : raw.substring("/chat/".length());
        if(LOG.isTraceEnabled()) {
            LOG.trace("Extracted room name: {}, from: {} of: {}", result, raw, data);
        }
        return result;
    }
    
    private Map<String, Object> getData(Message message) {
        Map<String, Object> data = message.getDataAsMap();
        return data == null ? message : data;
    }
    
    private MembersService getMembersService() {
        return (MembersService)this.getBayeuxServer()
                .getOption(ChatServerOptionNames.MEMBERS_SERVICE);
    }
    
    private ConfigurableServerChannel.ServerChannelListener getServerChannelListener() {
        return (ConfigurableServerChannel.ServerChannelListener)this.getBayeuxServer()
                .getOption(ChatServerOptionNames.CHANNEL_MESSAGE_LISTENER);
    }

    public final BayeuxServer getBayeuxServer() {
        return bayeuxServer;
    }

    public final ServerSession getServerSession() {
        return serverSession;
    }
}
