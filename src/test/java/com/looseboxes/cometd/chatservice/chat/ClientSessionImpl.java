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

import com.looseboxes.cometd.chatservice.test.TestChatObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class ClientSessionImpl extends AttributeStore implements ClientSession{

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionImpl.class);
    
    private final String id;

    private final TestChatObjects chatUtil;
    
    private final List<Extension> extensions;
    
    private final List<Runnable> batch;
    
    private boolean handshook;
    
    private boolean connected;

    public ClientSessionImpl(TestChatObjects chatUtil) {
        this(Long.toHexString(System.currentTimeMillis()), chatUtil);
    }
    
    public ClientSessionImpl(String id, TestChatObjects chatUtil) {
        this.id = Objects.requireNonNull(id);
        this.chatUtil = Objects.requireNonNull(chatUtil);
        this.extensions = Collections.synchronizedList(new ArrayList<>());
        this.batch = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void addExtension(Extension extnsn) {
        extensions.add(extnsn);
    }

    @Override
    public void removeExtension(Extension extnsn) {
        extensions.remove(extnsn);
    }

    @Override
    public List<Extension> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    @Override
    public void handshake(Map<String, Object> map, MessageListener ml) {
        handshook = true;
        chatUtil.sendSuccessMessage(id, ml);
    }

    @Override
    public void disconnect(MessageListener ml) {
        connected = false;
        chatUtil.sendSuccessMessage(id, ml);
    }

    @Override
    public ClientSessionChannel getChannel(String string) {
        return new ClientSessionChannelImpl(string, this, chatUtil);
    }

    @Override
    public void remoteCall(String target, Object data, MessageListener ml) {
        LOG.debug("Dummy remote call to: {}, with: {}", target, data);
        chatUtil.sendSuccessMessage(id, ml);
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
        return handshook;
    }

    @Override
    public void disconnect() {
        this.handshook = false;
        this.connected = false;
    }

    @Override
    public void batch(Runnable r) {
        batch.add(r);
    }

    @Override
    public void startBatch() { 
        batch.clear();
    }

    @Override
    public boolean endBatch() {
        batch.forEach((runnable) -> runnable.run());
        return true;
    }
}
