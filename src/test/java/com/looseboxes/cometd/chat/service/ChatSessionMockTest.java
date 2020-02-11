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

import com.looseboxes.cometd.chat.service.test.Mocker;
import com.looseboxes.cometd.chat.service.test.TestConfig;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.cometd.bayeux.Message;
import static org.assertj.core.api.Assertions.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ChatSessionMockTest {
 
    @Mock private ChatSession chatSession;
    
    @Test
    public void connect_ShouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> cs.connect();
        this.action_ShouldReturnValidFuture("connect_ShouldReturnValidFuture()", action);
    } 
    
    @Test
    public void disconnect_ShouldReturnValidFuture() {
        final Function<ChatSession, Future<Message>> action = (cs) -> cs.disconnect();
        this.action_ShouldReturnValidFuture("disconnect_ShouldReturnValidFuture()", action);
    } 

    protected Future<Message> action_ShouldReturnValidFuture(
            String methodName, Function<ChatSession, Future<Message>> action) {
        
        final String description = this.getDescription(methodName);
        
        System.out.println(description);
        
        final ChatSession candidate = this.getCandidate();
        
        final Future<Message> result = action.apply(candidate);
        
        verifyCandidate(candidate);
        
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
        
        final ChatSession candidate = this.getCandidate();
        
        try{

            final Future<Message> result = action.apply(candidate);

            fail(description + ", BUT completed execution");
            
        }catch(Exception expected) { }
    }

    protected String getDescription(String testMethodName) {
        return getTestConfig().testUtil().getDescription(this.getClass(), testMethodName);
    }
    
    protected ChatSession getCandidate() {
        return getMocker().lenient().mock(getChatSession());
    }
    
    protected Mocker getMocker() {
        return getTestConfig().mocker();
    }
    
    protected TestConfig getTestConfig() {
        return new TestConfig();
    }

    protected ChatSession getChatSession() {
        return chatSession;
    }
}
