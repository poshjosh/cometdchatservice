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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class MembersServiceInMemoryCache implements MembersService{
    
    private static final Logger LOG = LoggerFactory.getLogger(MembersServiceInMemoryCache.class);
    
    private final ConcurrentMap<String, Map<String, String>> members = new ConcurrentHashMap<>();

    @Override
    public Map<String, Map<String, String>> getMembers() {
        return Collections.unmodifiableMap(members);
    }

    /**
     * Get the members of the specified room. The members are key-value pairs, 
     * with keys being a user identifier, while value being client id for each user.
     * @param room
     * @return The members of the specified room.
     */
    @Override
    public Map<String, String> getMembers(String room) {
        final Map<String, String> roomMembers = members.get(room);
        LOG.debug("Room: {}, room members: {}", room, roomMembers);
        return roomMembers == null ? Collections.EMPTY_MAP : 
                Collections.unmodifiableMap(roomMembers);
    }

    /**
     * Add the specified user with value to the room as a key-value pair i.e
     * <code>user=value</code>
     * @param room
     * @param user
     * @param value
     * @return The previous value associated with the user or null if no 
     * value was previously associated.
     */
    @Override
    public String addMember(String room, String user, String value) {
        Map<String, String> roomMembers = this.members.get(room);
        if(roomMembers == null) {
            roomMembers = new ConcurrentHashMap<>();
            this.members.put(room, roomMembers);
        }
        final String previous = roomMembers.put(user, value);
        LOG.debug("After adding. Member: {}, value: {}, room: {}, room members: {}", 
                user, value, room, roomMembers);
        return previous;
    }

    /**
     * Remove the specified user from the specified room
     * @param room
     * @param user
     * @return The value of the removed user, or <code>null</code> if no user was removed.
     */
    @Override
    public String removeMember(String room, String user) {
        final Map<String, String> roomMembers = this.members.get(room);
        final String value = roomMembers == null || roomMembers.isEmpty() ? null : roomMembers.remove(user);
        LOG.debug("Member: {}, value: {}, room: {}, room members: {}", 
                user, value, room, roomMembers);
        return value;
    }

    /**
     * @param room
     * @param user
     * @return The value associated with the member in the <code>room</code>, or
     * <code>null</code> if none.
     */
    @Override
    public String getMembersValue(String room, String user) {
        final Map<String, String> roomMembers = this.members.get(room);
        final String value = roomMembers == null || roomMembers.isEmpty() ? 
                null : roomMembers.get(user);
        LOG.debug("Member: {}, value: {}, room: {}, room members: {}", 
                user, value, room, roomMembers);
        return value;
    }

    /**
     * @param room
     * @param value
     * @return The key previously associated with the <code>value</code> value,
     * or <code>null</code> if no key was previously associated.
     */
    @Override
    public String removeMemberByValue(String room, String value) {
        final Map<String, String> roomMembers = this.members.get(room);
        String member = null;
        if(roomMembers != null && !roomMembers.isEmpty()) {
            for(String user : roomMembers.keySet()) {
                if(value.equals(roomMembers.get(user))) {
                    member = user;
                    break;
                }
            }
            if(member != null) {
                roomMembers.remove(member);
            }
        }
        LOG.debug("Member: {}, value: {}, room: {}, room members: {}", 
                member, value, room, roomMembers);
        return member;
    }
}
