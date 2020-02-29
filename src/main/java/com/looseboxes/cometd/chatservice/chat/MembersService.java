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

/**
 * @author USER
 */
public interface MembersService {
    
    Map<String, Map<String, String>> getMembers();
    
    /**
     * Get the members of the specified room. The members are key-value pairs, 
     * with keys being a user identifier, while value being client id for each user.
     * @param room
     * @return The members of the specified room.
     */
    Map<String, String> getMembers(String room);

    /**
     * Add the specified user with value to the room as a key-value pair i.e
     * <code>user=value</code>
     * @param room
     * @param user
     * @param value
     * @return The previous value associated with the user or null if no 
     * value was previously associated with the user.
     */
    String addMember(String room, String user, String value);

    /**
     * Remove the specified user from the specified room
     * @param room
     * @param user
     * @return The value of the removed user, or <code>null</code> if no user was removed.
     */
    String removeMember(String room, String user);
    
    /**
     * @param room
     * @param user
     * @return The value associated with the member in the <code>room</code>, or
     * <code>null</code> if none.
     */
    String getMembersValue(String room, String user);
    
    /**
     * @param room
     * @param value
     * @return The key previously associated with the <code>value</code> value,
     * or <code>null</code> if no key was previously associated.
     */
    String removeMemberByValue(String room, String value);
}
