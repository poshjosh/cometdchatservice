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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import static org.assertj.core.api.Assertions.fail;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ChatSessionTest {
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    public ChatSessionTest() { }
    
    @Test
    public void send_shouldReturnValidFuture() {

        final Function<ChatSession, Future<Message>> action = this.sendAction();
        
        this.chatSessionAction_shouldReturnValidFuture("send", action);
    }
    
    @Test
    public void join_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> 
                cs.join(this.getChannelMessageListener());
        this.chatSessionAction_shouldReturnValidFuture("join", action);
    } 
    
    @Test
    public void connect_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> cs.connect();
        this.chatSessionAction_shouldReturnValidFuture("connect", action);
    } 

    @Test
    public void subscribe_shouldReturnValidFuture() {
        this.chatSessionAction_shouldReturnValidFuture("subscribe", 
                this.connectThenSubscribeAction());
    } 

    @Test
    public void subscribe_whenNotConneced_shouldThrowRuntimeException() {
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> getChatSession().subscribe(this.getChannelMessageListener()));
        if(logStackTrace) {
            thrown.printStackTrace();
        }        
    } 

    @Test
    public void unsubscribe_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            this.connectThenSubscribeAction().apply(cs);
            return cs.unsubscribe();
        };        
        this.chatSessionAction_shouldReturnValidFuture("unsubscribe", action);
    } 

    @Test
    public void disconnect_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            cs.connect();
            return cs.disconnect();
        };        
        this.chatSessionAction_shouldReturnValidFuture("disconnect", action);
    } 

    @Test
    @Disabled("This is not guaranteed to happen. See issue #001 or is it #002?")
    public void disconnect_whenSubscribed_shouldThrowRuntimeException() {
        final ChatSession chatSession = this.getChatSession();

        try{
            this.connectThenSubscribeAction().apply(chatSession).get();
        }catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> chatSession.disconnect());
        if(logStackTrace) {
            thrown.printStackTrace();
        }        
    } 

    @Test
    public void leave_shouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            cs.join(this.getChannelMessageListener());
            return cs.leave();
        };    
        this.chatSessionAction_shouldReturnValidFuture("leave", action);
    } 

    protected Function<ChatSession, Future<Message>> sendAction() {
    
        final String user1 = "test_recipient";
        
        final ChatSession user1session = this.getChatSession(user1);
        
        final Function<ChatSession, Future<Message>> action = (user0session) -> {
            
            try{
                
                user0session.join(this.getChannelMessageListener()).get();

                user1session.join(this.getChannelMessageListener()).get();

                return user0session.send("Hi " + user1, user1);
                
            }catch(InterruptedException | ExecutionException e) {
            
                throw new RuntimeException(e);
            }    
        };  
        
        return action;
    }

    protected Future<Message> chatSessionAction_shouldReturnValidFuture(
            String methodName, Function<ChatSession, Future<Message>> action) {
        final ChatSession chatSession = this.getChatSession();
        return this.action_shouldReturnValidFuture(methodName, chatSession, action);
    }

    protected Future<Message> action_shouldReturnValidFuture(String methodName, 
            ChatSession chatSession, Function<ChatSession, Future<Message>> action) {
        
        final String description = this.getDescription(
                methodName + "_shouldReturnValidFuture()");
        
        System.out.println(description);
        
        final Future<Message> result = action.apply(chatSession);
        
        validateResult(result);
        
        return result;
    }
    
    protected void validateResult(Future<Message> result) {
        try{
            final Message message = result.get();
            assertThat(message.isSuccessful(), is(true));
        }catch(InterruptedException | ExecutionException e) {
            fail("Result is of type Future. Future.get() threw " + e);
        }
    }

    @Test
    public void join_shouldResultToValidState() {
        final Function<ChatSession, Future<Message>> action = (cs) -> 
                cs.join(this.getChannelMessageListener());
        this.chatSessionAction_shouldResultToValidState(
                "join", action, (state) -> {
                    // Join doesn't wait for subscribe so this may not work
//                    assertThat(state.isSubscribing() || state.isSubscribed(), is(true));
                    assertThat(state.isConnected(), is(true));
                }
        );
    } 
    
    @Test
    public void connect_shouldResultToValidState() {
        final Function<ChatSession, Future<Message>> action = (cs) -> cs.connect();
        this.chatSessionAction_shouldResultToValidState(
                "connect", action, 
                (state) -> assertThat(state.isConnected(), is(true)));
    } 

    @Test
    public void subscribe_shouldResultToValidState() {
        this.chatSessionAction_shouldResultToValidState(
                "subscribe", this.connectThenSubscribeAction(), 
                (state) -> assertThat(state.isSubscribing() || state.isSubscribed(), is(true)));
    } 

    @Test
    public void unsubscribe_shouldResultToValidState() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            this.connectThenSubscribeAction().apply(cs);
            return cs.unsubscribe();
        };        
        this.chatSessionAction_shouldResultToValidState(
                "unsubscribe", action, 
                (state) -> assertThat(state.isUnsubscribing() || ! state.isSubscribed(), is(true)));
    } 

    @Test
    public void disconnect_shouldResultToValidState() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            cs.connect();
            return cs.disconnect();
        };        
        this.chatSessionAction_shouldResultToValidState(
                "disconnect", action, 
                (state) -> assertThat(state.isConnected(), is(false)));
    } 

    @Test
    public void leave_shouldResultToValidState() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            cs.join(this.getChannelMessageListener());
            return cs.leave();
        };    
        this.chatSessionAction_shouldResultToValidState(
                "leave", action, (state) -> {
                    assertThat(state.isUnsubscribing() || ! state.isSubscribed(), is(true));
                    assertThat(state.isConnected(), is(false));
                }
        );
    }
    
    protected Function<ChatSession, Future<Message>> connectThenSubscribeAction() {
        final Function<ChatSession, Future<Message>> action = (cs) -> {
            final Future<Message> ret = cs.connect();
            final ClientSessionChannel.MessageListener listener = 
                    getChannelMessageListener();
            cs.subscribe(listener);
            return ret;
        };        
        return action;
    }

    protected Future<Message> chatSessionAction_shouldResultToValidState(
            String methodName, 
            Function<ChatSession, Future<Message>> action,
            Consumer<ChatSession.State> statusValidator) {

        return this.action_shouldResultToValidState(
                methodName, this.getChatSession(), action, statusValidator);
    }
    
    protected Future<Message> action_shouldResultToValidState(
            String methodName, 
            ChatSession chatSession, 
            Function<ChatSession, Future<Message>> action,
            Consumer<ChatSession.State> statusValidator) {
        
        final String description = this.getDescription(
                methodName + "_shouldResultToValidState()");
        
        System.out.println(description);
        
        final Future<Message> result = action.apply(chatSession);
        
        validateChatSessionStatus(chatSession, statusValidator);
        
        return result;
    }
    
    protected void validateChatSessionStatus(ChatSession chatSession, 
            Consumer<ChatSession.State> statusValidator) {
        statusValidator.accept(chatSession.getState());
    }

    protected void action_shouldThrowException(String methodName, 
            ChatSession chatSession, Function<ChatSession, Future<Message>> action) {
        
        final String description = this.getDescription(methodName);
        
        System.out.println(description);
        
        final Exception thrown = Assertions.assertThrows(Exception.class, 
                () -> action.apply(chatSession));
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }
    
    protected ClientSessionChannel.MessageListener getChannelMessageListener() {
        return (channel, message) -> {};
    }

    protected ChatSession getChatSession() {
        return this.getChatSession("test_sender");
    }

    protected ChatSession getChatSession(String user) {
        return this.getTestConfig().testChatObjects().getChatSession(user);
    }

    protected String getDescription(String testMethodName) {
        return getTestConfig().testUtil().getDescription(this.getClass(), testMethodName);
    }

    protected TestConfig getTestConfig() {
        return new TestConfig();
    }
}
/**
 * 

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
 * 
 */