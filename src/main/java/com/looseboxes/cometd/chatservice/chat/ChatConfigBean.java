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

import java.io.Serializable;
import java.util.Objects;

/**
 * @author USER
 */
public final class ChatConfigBean implements ChatConfig, Serializable{
    
    private String channel;
    
    private String room;
    
    private String user;
    
    /** 
     * Any of [warn|info|debug]
     */
    private String logLevel = Chat.LOG_LEVEL_VALUES.INFO;
    
    /**
     * If you enable web socket then HttpSession/HttpRequest may be unavailable
     * @see #setWebsocketEnabled(boolean) 
     */
    private boolean websocketEnabled = false;

    public ChatConfigBean() { }
    
    @Override
    public ChatConfig forUser(String user) {
        final ChatConfigBean bean = new ChatConfigBean();
        bean.setChannel(this.channel);
        bean.setLogLevel(this.logLevel);
        bean.setRoom(this.room);
        bean.setUser(user);
        bean.setWebsocketEnabled(this.websocketEnabled);
        return bean;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public boolean isWebsocketEnabled() {
        return websocketEnabled;
    }

    public void setWebsocketEnabled(boolean websocketEnabled) {
        this.websocketEnabled = websocketEnabled;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.channel);
        hash = 37 * hash + Objects.hashCode(this.room);
        hash = 37 * hash + Objects.hashCode(this.user);
        hash = 37 * hash + Objects.hashCode(this.logLevel);
        hash = 37 * hash + (this.websocketEnabled ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChatConfigBean other = (ChatConfigBean) obj;
        if (this.websocketEnabled != other.websocketEnabled) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        if (!Objects.equals(this.room, other.room)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.logLevel, other.logLevel)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + 
                "{" + "channel=" + channel + 
                ", room=" + room + ", user=" + user + ", logLevel=" + logLevel + 
                ", websocketEnabled=" + websocketEnabled + '}';
    }
}
