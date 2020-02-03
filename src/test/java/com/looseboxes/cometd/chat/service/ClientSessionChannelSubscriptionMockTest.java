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

import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.test.Mocker;
import com.looseboxes.cometd.chat.service.test.TestConfig;
import org.cometd.bayeux.client.ClientSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ClientSessionChannelSubscriptionMockTest {
    
    @Mock private ClientSessionChannelSubscription clientSessionChannelSubscription;

    @Mock private ClientSession clientSession;
    
    @Test
    public void subscribe_GivenValidArguments_ShouldCreateAndReturnValidResponse() {
        
        final String methodName = "subscribe_GivenValidArguments_ShouldCreateAndReturnValidResponse";
        
        final Response result = subscribe_GivenArguments_ShouldCreateAndReturnValidResponse(
                methodName, this.getValidClientSession(), 
                this.getValidChannelName(), this.getValidTimeout());
    }
    
    @Test
    public void subscribe_GivenNegativeTimeout_ShouldCreateAndReturnValidResponse() {
        
        final String methodName = "subscribe_GivenNegativeTimeout_ShouldCreateAndReturnValidResponse";
        
        final Response result = subscribe_GivenArguments_ShouldCreateAndReturnValidResponse(
                methodName, this.getValidClientSession(), 
                this.getValidChannelName(), Integer.MIN_VALUE);
    }

    protected Response subscribe_GivenArguments_ShouldCreateAndReturnValidResponse(
            String methodName, ClientSession clientSession, String channel, long timeout) {
        final String description = this.getDescription(methodName);
        System.out.println(description);
        
        final ClientSessionChannelSubscription candidate = this.getCandidate();
        
        final Response result = candidate.subscribe(clientSession, channel, timeout);
        
        verifyCandidate(candidate, clientSession, channel, timeout);
        
        validateResult(result);
        
        return result;
    }
    
    protected void verifyCandidate(ClientSessionChannelSubscription candidate,
            ClientSession clientSession, String channel, long timeout) {
        verify(candidate, times(1)).subscribe(eq(clientSession), eq(channel), eq(timeout));
    }

    protected void validateResult(Response result) { 
        this.getTestConfig().testData().validateSuccessResponse(result);
    }

    @Test
    public void subscribe_GivenEmptyChannelText_ShouldThrowException() {

        final ClientSession clientSession = this.getValidClientSession();
        final String channel = "";
        final long timeout = this.getValidTimeout();
        
        this.subscribe_GivenArgs_ShouldThrowException(
                "subscribe_GivenEmptyChannelText_ShouldThrowException", 
                clientSession, channel, timeout);
    }
    
    @Test
    public void subscribe_GivenNullChannel_ShouldThrowException() {

        final ClientSession clientSession = this.getValidClientSession();
        final String channel = null;
        final long timeout = this.getValidTimeout();
        
        this.subscribe_GivenArgs_ShouldThrowException(
                "subscribe_GivenNullChannel_ShouldThrowException", 
                clientSession, channel, timeout);
    }
    
    @Test
    public void subscribe_GivenNullClientSession_ShouldThrowException() {

        final ClientSession clientSession = null;
        final String channel = this.getValidChannelName();
        final long timeout = this.getValidTimeout();
        
        this.subscribe_GivenArgs_ShouldThrowException(
                "subscribe_GivenNullClientSession_ShouldThrowException", 
                clientSession, channel, timeout);
    }
    
    protected void subscribe_GivenArgs_ShouldThrowException(
            String methodName, ClientSession clientSession, String channel, long timeout) {
        final String description = this.getDescription(methodName);
        System.out.println(description);
        
        final ClientSessionChannelSubscription candidate = this.getCandidate();
        
        try{
            
            final Response result = candidate.subscribe(clientSession, channel, timeout);

            fail(description + ", BUT completed execution");
            
        }catch(Exception expected) { }
    }

    protected String getDescription(String testMethodName) {
        return getTestConfig().testUtil().getDescription(testMethodName);
    }

    protected ClientSession getValidClientSession(){
        // Use lenient instance to avoid strict stub checking
        return getMocker().lenient().mock(clientSession, null) ;
    }
    
    protected String getValidChannelName() {
        return "/service/chat";
    }
    
    protected long getValidTimeout() {
        return 5000;
    }
    
    protected ClientSessionChannelSubscription getCandidate() {
        return getMocker().mock(getClientSessionChannelSubscription());
    }
    
    protected Mocker getMocker() {
        return getTestConfig().mocker();
    }
    
    protected TestConfig getTestConfig() {
        return new TestConfig();
    }

    protected ClientSessionChannelSubscription getClientSessionChannelSubscription() {
        return clientSessionChannelSubscription;
    }
}
