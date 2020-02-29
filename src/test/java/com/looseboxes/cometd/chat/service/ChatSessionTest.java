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

import com.looseboxes.cometd.chat.service.test.TestConfig;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import static org.assertj.core.api.Assertions.fail;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ChatSessionTest {
    
    public ChatSessionTest() { }

    @Test
    public void connect_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> cs.connect();
        this.action_shouldReturnValidFuture("connect_shouldReturnValidFuture()", action);
    } 
    
    @Test
    public void disconnect_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> cs.disconnect();
        this.action_shouldReturnValidFuture("disconnect_shouldReturnValidFuture()", action);
    } 

    protected Future<Message> action_shouldReturnValidFuture(
            String methodName, Function<ChatSession, Future<Message>> action) {
        
        final String description = this.getDescription(methodName);
        
        System.out.println(description);
        
        final ChatSession chatSession = this.getChatSession();
        
        final Future<Message> result = action.apply(chatSession);
        
        verifyCandidate(chatSession);
        
        validateResult(result);
        
        return result;
    }
    
    protected void verifyCandidate(ChatSession candidate) { 
    
    }
    
    protected void validateResult(Future<Message> result) {
        try{
            final Message message = result.get();
            assertThat(message.isSuccessful(), is(true));
        }catch(InterruptedException | ExecutionException e) {
            fail("Result is of type Future. Future.get() threw " + e);
        }
    }

    protected void action_ShouldThrowException(String methodName, 
            Function<ChatSession, Future<Message>> action) {
        
        final String description = this.getDescription(methodName);
        
        System.out.println(description);
        
        final ChatSession chatSession = this.getChatSession();
        
        try{

            final Future<Message> result = action.apply(chatSession);

            fail(description + ", BUT completed execution");
            
        }catch(Exception expected) { }
    }

    protected String getDescription(String testMethodName) {
        return getTestConfig().testUtil().getDescription(this.getClass(), testMethodName);
    }
    
    @Test
    public void testAddListener() {
        System.out.println("addListener");
        ChatListener listener = null;
        final ChatSession chatSession = getChatSession();
        boolean expResult = false;
        boolean result = chatSession.addListener(listener);
        
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testRemoveListener() {
        System.out.println("removeListener");
        ChatListener listener = null;
        final ChatSession chatSession = getChatSession();
        boolean expResult = false;
        boolean result = chatSession.addListener(listener);
        
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testContainsListener() {
        System.out.println("containsListener");
        ChatListener listener = null;
        final ChatSession chatSession = getChatSession();
        boolean expResult = false;
        boolean result = chatSession.containsListener(listener);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of connect method, of class ChatSession.
     */
    @Test
    public void testConnect() {
        System.out.println("connect");
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.connect();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of disconnect method, of class ChatSession.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.disconnect();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatus method, of class ChatSession.
     */
    @Test
    public void testGetStatus() {
        System.out.println("getStatus");
        final ChatSession chatSession = getChatSession();
        ChatSession.Status expResult = null;
        ChatSession.Status result = chatSession.getStatus();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of join method, of class ChatSession.
     */
    @Test
    public void testJoin() {
        System.out.println("join");
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.join();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of leave method, of class ChatSession.
     */
    @Test
    public void testLeave() {
        System.out.println("leave");
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.leave();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of send method, of class ChatSession.
     */
    @Test
    public void testSend_String_String() {
        System.out.println("send");
        String textMessage = "";
        String toUser = "";
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.send(textMessage, toUser);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of send method, of class ChatSession.
     */
    @Test
    public void testSend_3args() {
        System.out.println("send");
        String textMessage = "";
        String toUser = "";
        ClientSession.MessageListener messageListener = null;
        final ChatSession chatSession = getChatSession();
        chatSession.send(textMessage, toUser, messageListener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subscribe method, of class ChatSession.
     */
    @Test
    public void testSubscribe() {
        System.out.println("subscribe");
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.subscribe();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unsubscribe method, of class ChatSession.
     */
    @Test
    public void testUnsubscribe() {
        System.out.println("unsubscribe");
        final ChatSession chatSession = getChatSession();
        Future<Message> expResult = null;
        Future<Message> result = chatSession.unsubscribe();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    protected ChatSession getChatSession() {
        return this.getTestConfig().appConfig().chatSession(
                this.getChatUrl(), "/chat/privatechat", "test_room", "test_user");
    }
    
    protected String getChatUrl() {
        return this.getTestConfig().testUrl().getChatUrl(this.getPort());
    }
    
    protected int getPort() {
        return 8080;
    }
    
    protected TestConfig getTestConfig() {
        return new TestConfig();
    }
}
