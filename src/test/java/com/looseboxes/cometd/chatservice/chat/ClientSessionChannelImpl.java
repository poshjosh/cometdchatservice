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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;

/**
 * @author USER
 */
public class ClientSessionChannelImpl extends AttributeStore implements ClientSessionChannel{
    
    private final String id;

    private final ChannelId channelId;
    
    private final ClientSession clientSession;

    private final ChatUtil chatUtil;
    
    private final List<MessageListener> subscribers;
    
    private final List<ClientSessionChannelListener> listeners;
    
    private boolean released;

    public ClientSessionChannelImpl(String id, 
            ClientSession clientSession, ChatUtil chatUtil) {
        this.id = Objects.requireNonNull(id);
        this.channelId = new ChannelId(id);
        this.clientSession = Objects.requireNonNull(clientSession);
        this.chatUtil = Objects.requireNonNull(chatUtil);
        this.subscribers = Collections.synchronizedList(new ArrayList<>());
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }
    
    @Override
    public void addListener(ClientSessionChannelListener cl) {
        listeners.add(cl);
    }

    @Override
    public void removeListener(ClientSessionChannelListener cl) {
        this.listeners.remove(cl);
    }

    @Override
    public List<ClientSessionChannelListener> getListeners() {
        return Collections.unmodifiableList(listeners);
    }

    @Override
    public ClientSession getSession() {
        return clientSession;
    }

    @Override
    public void publish(Object o, ClientSession.MessageListener ml) {
        publish(chatUtil.createSuccessMessage(id, o), ml);
    }

    @Override
    public void publish(Message.Mutable mtbl, ClientSession.MessageListener ml) {
        ml.onMessage(mtbl);
    }

    @Override
    public boolean subscribe(Message.Mutable mtbl, MessageListener ml, ClientSession.MessageListener ml1) {
        this.subscribers.add(ml);
        this.chatUtil.sendSuccessMessage(id, mtbl, ml1);
        return true;
    }

    @Override
    public boolean unsubscribe(Message.Mutable mtbl, MessageListener ml, ClientSession.MessageListener ml1) {
        this.subscribers.remove(ml);
        this.chatUtil.sendSuccessMessage(id, mtbl, ml1);
        return true;
    }

    @Override
    public void unsubscribe() { 
        this.subscribers.clear();
    }

    @Override
    public List<MessageListener> getSubscribers() {
        return Collections.unmodifiableList(subscribers);
    }

    @Override
    public boolean release() {
        released = true;
        return true;
    }

    @Override
    public boolean isReleased() {
        return released;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ChannelId getChannelId() {
        return channelId;
    }

    @Override
    public boolean isMeta() {
        return this.channelId.isMeta();
    }

    @Override
    public boolean isService() {
        return this.channelId.isService();
    }

    @Override
    public boolean isBroadcast() {
        return this.channelId.isBroadcast();
    }

    @Override
    public boolean isWild() {
        return this.channelId.isWild();
    }

    @Override
    public boolean isDeepWild() {
        return channelId.isDeepWild();
    }
}
