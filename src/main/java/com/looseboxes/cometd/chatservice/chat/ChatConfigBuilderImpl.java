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

import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class ChatConfigBuilderImpl implements ChatConfig.Builder{
    
    private static final Logger LOG = LoggerFactory.getLogger(ChatConfigBuilderImpl.class);
    
    private final ChatConfigBean bean = new ChatConfigBean();

    private final AtomicBoolean buildAttempted = new AtomicBoolean(false);

    public ChatConfigBuilderImpl() { }
    
    @Override
    public ChatConfig build() {
        if(LOG.isTraceEnabled()) {
            LOG.trace("build() called by: {}", this);
        }
        
        if(this.isBuildAttempted()) {
            throw new IllegalStateException("Method build() may only be called once");
        }
        buildAttempted.compareAndSet(false, true);
        
        return bean;
    }

    @Override
    public ChatConfig.Builder newInstance() {
        return new ChatConfigBuilderImpl();
    }

    @Override
    public boolean isBuildAttempted() {
        return this.buildAttempted.get();
    }

    @Override
    public ChatConfig.Builder channel(String channel) {
        this.bean.setChannel(channel);
        return this;
    }

    @Override
    public ChatConfig.Builder room(String room) {
        this.bean.setRoom(room);
        return this;
    }

    @Override
    public ChatConfig.Builder user(String user) {
        this.bean.setUser(user);
        return this;
    }

    @Override
    public ChatConfig.Builder logLevel(String logLevel) {
        this.bean.setLogLevel(logLevel);
        return this;
    }

    /**
     * If you enable web socket then there may be no HttpSession/HttpRequest. Hence,
     * calls to {@link org.cometd.bayeux.server.BayeuxContext#setHttpSessionAttribute(java.lang.String, java.lang.Object) BayeuxContext#setHttpSessionAttribute}
     * or {@link org.cometd.bayeux.server.BayeuxContext#getHttpSessionAttribute(java.lang.String) BayeuxContext#getHttpSessionAttribute}
     * amongst other methods accessing the HttpSession/HttpRequest will throw an Exception
     * @param websocketEnabled if true web socket will be enabled, otherwise it will be disabled
     * @return the calling instance
     */
    @Override
    public ChatConfig.Builder websocketEnabled(boolean websocketEnabled) {
        this.bean.setWebsocketEnabled(websocketEnabled);
        return this;
    }
}
