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

import java.io.Serializable;
import java.util.Objects;

/**
 * @author USER
 */
public class ChatConfig implements Serializable{
    
    private String membersServiceChannel = Chat.MEMBERS_SERVICE_CHANNEL;
    
    private String channel;
    
    private String room;
    
    private String user;
    
    /** 
     * Any of [warn|info|debug]
     */
    private String logLevel = Chat.LOG_LEVEL_VALUES.INFO;
    
    private boolean websocketEnabled = true;
    
    public ChatConfig(String channel, String room, String user) {
        this(Chat.MEMBERS_SERVICE_CHANNEL, channel, 
                room, user, Chat.LOG_LEVEL_VALUES.INFO, true);
    }
    
    public ChatConfig(String membersServiceChannel, String channel, 
            String room, String user, String logLevel, boolean websocketEnabled) {
        this.membersServiceChannel = this.requireNonNullOrEmpty(membersServiceChannel);
        this.channel = this.requireNonNullOrEmpty(channel);
        this.room = this.requireNonNullOrEmpty(room);
        this.user = this.requireNonNullOrEmpty(user);
        this.logLevel = this.requireNonNullOrEmpty(logLevel);
        this.websocketEnabled = websocketEnabled;
    }

    public ChatConfig forUser(String user) {
        return new ChatConfig(this.membersServiceChannel, this.channel, 
                this.room, user, this.logLevel, this.websocketEnabled);
    }

    public String getMembersServiceChannel() {
        return membersServiceChannel;
    }

    public void setMembersServiceChannel(String membersServiceChannel) {
        this.membersServiceChannel = membersServiceChannel;
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
        hash = 29 * hash + Objects.hashCode(this.membersServiceChannel);
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
        if (!Objects.equals(this.membersServiceChannel, other.membersServiceChannel)) {
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
        return "ChatConfig{" + "membersServiceChannel=" + membersServiceChannel + ", channel=" + channel + ", room=" + room + ", user=" + user + ", logLevel=" + logLevel + ", websocketEnabled=" + websocketEnabled + '}';
    }
}