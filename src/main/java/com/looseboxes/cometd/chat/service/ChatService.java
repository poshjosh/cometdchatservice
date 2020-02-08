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
package com.looseboxes.cometd.chat.service;

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
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.authorizer.GrantAuthorizer;
import org.cometd.server.filter.DataFilter;
import org.cometd.server.filter.DataFilterMessageListener;
import org.cometd.server.filter.JSONDataFilter;
import org.cometd.server.filter.NoMarkupFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("chat")
public final class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
    
    @Inject
    private BayeuxServer _bayeux;
    
    @Session
    private ServerSession _session;

    @Configure({"/chat/**", "/members/**"})
    protected void configureChatStarStar(ConfigurableServerChannel channel) {
        LOG.debug("configureChatStarStar(ConfigurableServerChannel)");
        DataFilterMessageListener noMarkup = new DataFilterMessageListener(
                new NoMarkupFilter(), new BadWordFilter(getSafeContentService()));
        channel.addListener(noMarkup);
        channel.addAuthorizer(GrantAuthorizer.GRANT_ALL);
    }

    @Configure(Chat.MEMBERS_SERVICE_CHANNEL)
    protected void configureMembers(ConfigurableServerChannel channel) {
        LOG.debug("configureMembers(ConfigurableServerChannel)");
        channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
        channel.setPersistent(true);
    }

    @Listener(Chat.MEMBERS_SERVICE_CHANNEL)
    public void handleMembership(ServerSession client, ServerMessage message) {
        try{
            LOG.debug("handleMembership(ServerSession, ServerMessage={})", message);
            final Map<String, Object> data = getData(message);

            final String room = data == null ? null : getRoom(data, null);

            final MembersService membersSvc = this.getMembersService();
            
            final String userName = (String)data.get(Chat.USER);

            membersSvc.addMember(room, userName, client.getId());

            client.addListener((ServerSession.RemoveListener)(session, timeout) -> {
                membersSvc.removeMemberByValue(room, session.getId());
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
        ClientSessionChannel channel = _session.getLocalSession().getChannel("/members/" + room);
        channel.publish(users);
        return true;
    }

    @Configure(ClientSessionChannel.SERVICE+"/privatechat")
    protected void configurePrivateChat(ConfigurableServerChannel channel) {
        LOG.debug("configurePrivateChat(ConfigurableServerChannel)");
        DataFilterMessageListener noMarkup = new DataFilterMessageListener(
                new NoMarkupFilter(), new BadWordFilter(getSafeContentService()));
        channel.setPersistent(true);
        channel.addListener(noMarkup);
        channel.addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);
    }

    @Listener(ClientSessionChannel.SERVICE+"/privatechat")
    public void privateChat(ServerSession client, ServerMessage message) {
        try{
            LOG.trace("privateChat(ServerSession, ServerMessage={})", message);
            final Map<String, Object> data = getData(message);

            final String room = data == null ? null : getRoom(data, null);

            final MembersService membersSvc = this.getMembersService();

            final String[] peerNames = this.getPeerNames(data);
            final ArrayList<ServerSession> peers = new ArrayList<>(peerNames.length);

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

                for (ServerSession peer : peers) {
                    if (peer != client) {
                        peer.deliver(_session, forward, Promise.noop());
                    }
                }
                client.deliver(_session, forward, Promise.noop());
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
        LOG.trace("Extracted room name: {}, from: {} of: {}", result, raw, data);
        return result;
    }
    
    private Map<String, Object> getData(Message message) {
        Map<String, Object> data = message.getDataAsMap();
        return data == null ? message : data;
    }
    
    private MembersService getMembersService() {
        return (MembersService)_bayeux.getOption(MembersService.class.getSimpleName());
    }

    private SafeContentService getSafeContentService() {
        return (SafeContentService)_bayeux.getOption(SafeContentService.class.getSimpleName());
    }

    private static class BadWordFilter extends JSONDataFilter {
        
        private final SafeContentService safeContentService;

        public BadWordFilter(SafeContentService safeContentService) {
            this.safeContentService = Objects.requireNonNull(safeContentService);
        }
        
        @Override
        protected Object filterString(ServerSession session, ServerChannel channel, String string) {
            if ( ! safeContentService.isSafe(string)) {
                throw new DataFilter.AbortException();
            }
            return string;
        }
    }
}
