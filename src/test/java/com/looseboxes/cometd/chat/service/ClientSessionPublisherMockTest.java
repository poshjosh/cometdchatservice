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
import java.util.HashMap;
import java.util.Map;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ClientSessionPublisherMockTest {
 
    @Mock private ClientSessionPublisher clientSessionPublisher;
    
    @Mock private ClientSessionChannel clientSessionChannel;
    
    @Test
    public void publish_GivenValidArguments_ShouldReturnSuccessResponse() {
        
        final String methodName = "publish_GivenValidArguments_ShouldReturnSuccessResponse";
        
        final Response result = publish_GivenArguments_ShouldReturnSuccessResponse(
                methodName, this.getValidClientSessionChannel(),
                this.getValidMessage(), this.getValidTimeout());
    }
    
    @Test
    public void publish_GivenEmptyMessage_ShouldReturnSuccessResponse() {
        
        final String methodName = "publish_GivenEmptyMessage_ShouldReturnSuccessResponse";
        
        final Response result = publish_GivenArguments_ShouldReturnSuccessResponse(
                methodName, this.getValidClientSessionChannel(),
                new HashMap(), this.getValidTimeout());
    }

    @Test
    public void publish_GivenNegativeTimeout_ShouldReturnSuccessResponse() {
        
        final String methodName = "publish_GivenNegativeTimeout_ShouldReturnSuccessResponse";
        
        final Response result = publish_GivenArguments_ShouldReturnSuccessResponse(
                methodName, this.getValidClientSessionChannel(),
                new HashMap(), Integer.MIN_VALUE);
    }

    protected Response publish_GivenArguments_ShouldReturnSuccessResponse(
            String methodName, ClientSessionChannel channel, 
            Map<String, Object> message, long timeout) {
        final String description = this.getDescription(methodName);
        System.out.println(description);
        
        final ClientSessionPublisher candidate = this.getCandidate();
        
        final Response result = candidate.publish(channel, message, timeout);
        
        verifyCandidate(candidate, channel, message, timeout);
        
        validateResult(result);
        
        return result;
    }
    
    protected void verifyCandidate(
            ClientSessionPublisher candidate,ClientSessionChannel channel, 
            Map<String, Object> message, long timeout) {
        verify(candidate, times(1)).publish(eq(channel), eq(message), eq(timeout));
    }
    
    protected void validateResult(Response response) {
        this.getTestConfig().testData().validateSuccessResponse(response);
    }

    @Test
    public void publish_GivenNullMessage_ShouldThrowException() {

        final ClientSessionChannel channel = this.getValidClientSessionChannel();
        final Map<String, Object> message = null;
        final long timeout = this.getValidTimeout();
        
        this.publish_GivenArgs_ShouldThrowException(
                "publish_GivenNullMessage_ShouldThrowException", 
                channel, message, timeout);
    }
    
    @Test
    public void publish_GivenNullChannel_ShouldThrowException() {

        final ClientSessionChannel channel = null;
        final Map<String, Object> message = this.getValidMessage();
        final long timeout = this.getValidTimeout();
            
        this.publish_GivenArgs_ShouldThrowException(
                "publish_GivenNullChannel_ShouldThrowException", 
                channel, message, timeout);
    }
    
    protected void publish_GivenArgs_ShouldThrowException(
            String methodName, ClientSessionChannel channel, 
            Map<String, Object> message, long timeout) {
        final String description = this.getDescription(methodName);
        System.out.println(description);
        
        final ClientSessionPublisher candidate = this.getCandidate();
        
        try{
            final Response result = candidate.publish(channel, message, timeout);

            fail(description + ", BUT completed execution");
            
        }catch(Exception expected) { }
    }

    protected String getDescription(String testMethodName) {
        return getTestConfig().testUtil().getDescription(this.getClass(), testMethodName);
    }

    protected ClientSessionChannel getValidClientSessionChannel(){
        return this.getMocker().lenient().mock(clientSessionChannel);
    }
    
    protected Map<String, Object> getValidMessage() {
        return new HashMap<>();
    }
    
    protected long getValidTimeout() {
        return 5000;
    }
    
    protected ClientSessionPublisher getCandidate() {
        return getMocker().mock(getClientSessionPublisher());
    }
    
    protected Mocker getMocker() {
        return getTestConfig().mocker();
    }
    
    protected TestConfig getTestConfig() {
        return new TestConfig();
    }

    protected ClientSessionPublisher getClientSessionPublisher() {
        return clientSessionPublisher;
    }
}
