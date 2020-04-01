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

import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.Assertions;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.api.function.Executable;
import static org.mockito.ArgumentMatchers.isNull;

/**
 * @author USER
 */
public class MembersServiceTest {

    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    public MembersServiceTest() { }

    @Test
    public void getMembers_whenNullRoom_shouldThrowRuntimeException() {
        System.out.println("getMembers_whenNullRoom_shouldThrowRuntimeException");
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> getMembersService().getMembers(null));
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }

    @Test
    public void getMembers_whenMembersAdded_shouldReturnMapOfValidContent() {
        System.out.println("getMembers_whenMembersAdded_shouldReturnMapOfValidContent");
        final String room = this.getRoom();
        final MembersService membersService = this.getMembersService();
        final Map<String, String> expRoomMembers = 
                this.addUniqueMembers(membersService, room, 3);
        final Map<String, String> roomMembers = membersService.getMembers(room);
        assertThat(roomMembers, is(expRoomMembers));
    }
    
    @Test
    public void getMembers_whenMembersAdded_shouldReturnMapOfValidSize() {
        System.out.println("getMembers_whenMembersAdded_shouldReturnMapOfValidSize");
        final int numberOfMembers = 3;
        final String room = this.getRoom();
        final MembersService membersService = this.getMembersService();
        this.addUniqueMembers(membersService, room, numberOfMembers);
        final Map<String, String> roomMembers = membersService.getMembers(room);
        assertThat(roomMembers.size(), is(numberOfMembers));
    }
    
    @Test
    public void getMembers_whenServiceNewlyCreated_shouldReturnEmptyMap() {
        System.out.println("getMembers_whenServiceNewlyCreated_shouldReturnEmptyMap");
        final MembersService membersService = this.getMembersService();
        final Map<String, Map<String, String>> result = membersService.getMembers();
        assertTrue(result.isEmpty());
    }

    @Test
    public void getMembersValue_whenNullUser_shouldThrowRuntimeException() {
        System.out.println("getMembersValue_whenNullUser_shouldThrowRuntimeException");
        
        this.onMethodCall_givenArgs_shouldThrowRuntimeException(
                "getMembersValue", getRoom(), null);
    }

    @Test
    public void getMembersValue_whenNullRoom_shouldThrowRuntimeException() {
        System.out.println("getMembersValue_whenNullRoom_shouldThrowRuntimeException");
        
        this.onMethodCall_givenArgs_shouldThrowRuntimeException(
                "getMembersValue", null, getUniqueUser());
    }
    
    @Test
    public void removeMember_whenNullUser_shouldThrowRuntimeException() {
        System.out.println("removeMembers_whenNullUser_shouldThrowRuntimeException");
        
        this.onMethodCall_givenArgs_shouldThrowRuntimeException(
                "removeMember", getRoom(), null);
    }

    @Test
    public void removeMember_whenNullRoom_shouldThrowRuntimeException() {
        System.out.println("removeMember_whenNullRoom_shouldThrowRuntimeException");
        
        this.onMethodCall_givenArgs_shouldThrowRuntimeException(
                "removeMember", null, getUniqueUser());
    }

    @Test
    public void getMembersValue_whenMembersAdded_shouldReturnValidResult() {
        System.out.println("getMembersValue_whenMembersAdded_shouldReturnValidResult");
        final String room = this.getRoom();
        
        final MembersService membersService = this.getMembersService();
        final Map.Entry<String, String> user2valueMapping = 
                addUniqueMember(membersService, room);
        
        final String expMemberValue = user2valueMapping.getValue();
        
        final String memberValue = membersService.getMembersValue(
                room, user2valueMapping.getKey());
        
        assertThat(memberValue, is(expMemberValue));
    }

    @Test
    public void getMembersValue_whenNewlyCreated_shouldReturnNull() {
        System.out.println("getMembersValue_whenNewlyCreated_shouldReturnNull");
        final String room = this.getRoom();
        final String user = this.getUniqueUser();
        final MembersService membersService = this.getMembersService();
        final String result = membersService.getMembersValue(room, user);
        assertTrue(result == null);
    }
    
    @Test
    public void removeMembers_whenMembersAdded_shouldReturnValidResult() {
        System.out.println("removeMembers_whenMembersAdded_shouldReturnValidResult");
        final String room = this.getRoom();
        
        final MembersService membersService = this.getMembersService();
        final Map.Entry<String, String> user2valueMapping = 
                addUniqueMember(membersService, room);
        
        final String expMemberValue = user2valueMapping.getValue();
        
        final String memberValue = membersService.removeMember(
                room, user2valueMapping.getKey());
        
        assertThat(memberValue, is(expMemberValue));
    }

    @Test
    public void removeMember_whenNewlyCreated_shouldReturnNull() {
        System.out.println("removeMember_whenNewlyCreated_shouldReturnNull");
        final String room = this.getRoom();
        final String user = this.getUniqueUser();
        final MembersService membersService = this.getMembersService();
        final String result = membersService.removeMember(room, user);
        assertTrue(result == null);
    }

    @Test
    public void removeMemberByValue_whenNullValue_shouldThrowRuntimeException() {
        System.out.println("removeMemberByValue_whenNullValue_shouldThrowRuntimeException");
        final String room = getRoom();
        final String value = null;
        final MembersService membersService = this.getMembersService();
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> membersService.removeMemberByValue(room, value));
        if(this.logStackTrace){
            thrown.printStackTrace();
        }
    }

    @Test
    public void removeMemberByValue_whenNullRoom_shouldThrowRuntimeException() {
        System.out.println("removeMemberByValue_whenNullRoom_shouldThrowRuntimeException");
        final String room = null;
        final String value = getUniqueValue();
        final MembersService membersService = this.getMembersService();
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> membersService.removeMemberByValue(room, value));
        if(this.logStackTrace){
            thrown.printStackTrace();
        }
    }
    
    @Test
    public void removeMemberByValue_whenMembersAdded_shouldReturnValidResult() {
        System.out.println("removeMemberByValue_whenMembersAdded_shouldReturnValidResult");
        final String room = getRoom();
        final String user = getUniqueUser();
        final String value = getUniqueValue();
        final MembersService membersService = this.getMembersService();
        membersService.addMember(room, user, value);
        final String result = membersService.removeMemberByValue(room, value);
        assertThat(result, is(user));
    }
    
    @Test
    public void removeMemberByValue_whenNewlyCreated_shouldReturnNull() {
        System.out.println("removeMemberByValue_whenNewlyCreated_shouldReturnNull");
        final String room = getRoom();
        final String value = getUniqueValue();
        final MembersService membersService = this.getMembersService();
        final String result = membersService.removeMemberByValue(room, value);
        assertTrue(result == null);
    }
    
    public void onMethodCall_givenArgs_shouldThrowRuntimeException(
            String methodName, String room, String user) {
        
        final MembersService membersService = this.getMembersService();
        
        final Executable executable;
        
        switch(methodName) {
            case "removeMember":
                executable = () -> membersService.removeMember(room, user); break;
            case "getMembersValue":
                executable = () -> membersService.getMembersValue(room, user); break;
            default: throw new IllegalArgumentException("Unexpected method: " + methodName);
        }
        
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, executable);
        
        if(this.logStackTrace){
            thrown.printStackTrace();
        }
    }

    public Set<String> getUniqueUsers(int count) {
        final Set<String> members = new HashSet();
        for(int i=0; i<count; i++) {
            members.add(this.getUniqueUser());
        }
        return members;
    }
    
    public Map<String, String> addUniqueMembers(MembersService membersService, int count) {
        return this.addUniqueMembers(membersService, getRoom(), count);
    }
    
    public Map<String, String> addUniqueMembers(MembersService membersService, 
            String room, int count) {
        final Map<String, String> output = new HashMap(count, 1.0f);
        for(int i=0; i<count; i++) {
            final Map.Entry<String, String> added = 
                    this.addUniqueMember(membersService, room);
            output.put(added.getKey(), added.getValue());
        }
        return Collections.unmodifiableMap(output);
    }
    
    public Map.Entry<String, String> addUniqueMember(MembersService membersService) {
        return this.addUniqueMember(membersService, getRoom());
    }
    
    public Map.Entry<String, String> addUniqueMember(
            MembersService membersService, String room) {
        final Map.Entry<String, String> entry = new HashMap.SimpleImmutableEntry<>(
                this.getUniqueUser(), this.getUniqueValue());
        membersService.addMember(room, entry.getKey(), entry.getValue());
        return entry;
    }

    public String getUniqueUser() {
        return getUnique("user");
    }
    
    public String getUniqueValue() {
        return getUnique("value");
    }
    
    public String getUnique(String prefix) {
        return prefix + '_' + 
                Long.toHexString(System.currentTimeMillis()) + Math.random();
    }

    public String getRoom() {
        return "/chat/dummyRoom";
    }
    
    public MembersService getMembersService() {
        return new MembersServiceInMemoryCache();
    }
}
