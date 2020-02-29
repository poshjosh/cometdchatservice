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

import java.util.Map;
import java.util.Objects;
import org.cometd.bayeux.ChannelId;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.common.AbstractClientSession;
import org.cometd.common.HashMapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class ClientSessionDummy extends AbstractClientSession{
    
    private static Logger LOG = LoggerFactory.getLogger(ClientSessionDummy.class);
    
    private final String id;
    
    private boolean connected;

    public ClientSessionDummy() {
        this(Long.toHexString(System.currentTimeMillis()));
    }
    
    public ClientSessionDummy(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public void remoteCall(String target, Object data, MessageListener callback) {
        LOG.debug("Dummy remote call to: {}, with: {}", target, data);
        this.sendSuccessMessage(callback);
    }

    @Override
    public void batch(Runnable batch) {
        LOG.debug("#batch(Runnable)");
        batch.run();
    }

    @Override
    protected ChannelId newChannelId(String string) {
        return new ChannelId(string);
    }

    @Override
    protected AbstractSessionChannel newChannel(ChannelId channelId) {
        return new AbstractSessionChannel(channelId) {
            @Override
            public ClientSession getSession() {
                return ClientSessionDummy.this;
            }
        };
    }

    @Override
    protected void sendBatch() { 
        LOG.debug("#sendBatch()");
    }

    @Override
    protected void send(Message.Mutable mtbl) {  
        LOG.debug("#send(Message.Mutable)");
    }

    @Override
    public void handshake(Map<String, Object> map, MessageListener ml) { 
        connected = true;
        LOG.debug("Handshaken: {}", connected);
        this.sendSuccessMessage(ml);
    }

    @Override
    public void disconnect(MessageListener ml) { 
        LOG.trace("Disconnecting");
        this.disconnect();
        this.sendSuccessMessage(ml);
    }
    
    protected void sendSuccessMessage(MessageListener ml) { 
        LOG.trace("Sending success message");
        final Message message = this.createSuccessMessage();
        ml.onMessage(message);
        LOG.trace("Done sending message: {}", message);
    }
    
    protected Message createSuccessMessage() {
        return this.createMessage(true);
    }
    
    protected Message createMessage(boolean success) {
        final Message msg = new HashMapMessage();
        msg.put("id", Long.toHexString(System.currentTimeMillis()));
        msg.put("clientId", this.id);
        msg.put("meta", true);
        msg.put("successful", success);
//        msg.put("empty", empty);
        msg.put("publishReply", false);
        return msg;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean isHandshook() {
        return connected;
    }

    @Override
    public void disconnect() {
        connected = false;
        LOG.debug("Disconnected: {}", !connected);
    }
}
