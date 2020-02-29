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
public class ChatConfig implements Serializable{
    
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
    
    public ChatConfig(String channel, String room, String user) {
        this(channel, room, user, Chat.LOG_LEVEL_VALUES.INFO, false);
    }
    
    public ChatConfig(String channel, String room, String user, 
            String logLevel, boolean websocketEnabled) {
        this.channel = this.requireNonNullOrEmpty(channel);
        this.room = this.requireNonNullOrEmpty(room);
        this.user = this.requireNonNullOrEmpty(user);
        this.logLevel = this.requireNonNullOrEmpty(logLevel);
        this.websocketEnabled = websocketEnabled;
    }

    public ChatConfig forUser(String user) {
        return new ChatConfig(this.channel, this.room, user, this.logLevel, this.websocketEnabled);
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isWebsocketEnabled() {
        return websocketEnabled;
    }

    /**
     * If you enable web socket then there may be no HttpSession/HttpRequest. Hence,
     * calls to {@link org.cometd.bayeux.server.BayeuxContext#setHttpSessionAttribute(java.lang.String, java.lang.Object) BayeuxContext#setHttpSessionAttribute}
     * or {@link org.cometd.bayeux.server.BayeuxContext#getHttpSessionAttribute(java.lang.String) BayeuxContext#getHttpSessionAttribute}
     * amongst other methods accessing the HttpSession/HttpRequest will throw an Exception
     * @param websocketEnabled if true web socket will be enabled, otherwise it will be disabled
     */
    public void setWebsocketEnabled(boolean websocketEnabled) {
        this.websocketEnabled = websocketEnabled;
    }

    private String requireNonNullOrEmpty(String s) {
        Objects.requireNonNull(s);
        if(s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return s;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.channel);
        hash = 29 * hash + Objects.hashCode(this.room);
        hash = 29 * hash + Objects.hashCode(this.user);
        hash = 29 * hash + Objects.hashCode(this.logLevel);
        hash = 29 * hash + (this.websocketEnabled ? 1 : 0);
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
        final ChatConfig other = (ChatConfig) obj;
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
        return "ChatConfig{" + "channel=" + channel + ", room=" + room + ", user=" + user + ", logLevel=" + logLevel + ", websocketEnabled=" + websocketEnabled + '}';
    }
}
