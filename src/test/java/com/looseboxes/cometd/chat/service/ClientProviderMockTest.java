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
import java.util.HashMap;
import java.util.Map;
import org.cometd.bayeux.client.ClientSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ClientProviderMockTest {
 
    @Mock private ClientProvider clientProvider;
    
    private final ClientSession clientSession = null;
    
    @Test
    public void createClient_GivenValidUrlAndTransportOptions_ShouldCreateAndReturnNewClientSession() {
        
        final String methodName = "createClient_GivenValidUrlAndTransportOptions_ShouldCreateAndReturnNewClientSession";
        
        final ClientSession result = createClient_GivenArguments_ShouldCreateAndReturnNewClientSession(
                methodName, this.getValidUrl(), this.getValidTransportOptions());
    }
    
    protected ClientSession createClient_GivenArguments_ShouldCreateAndReturnNewClientSession(
            String methodName, String url, Map<String, Object> transportOptions) {
        final String description = this.getDescription(methodName);
        System.out.println(description);
        
        final ClientProvider candidate = this.getCandidate();
        
        final ClientSession result = candidate.createClient(url, transportOptions);
        
        verifyCandidate(candidate, url, transportOptions);
        
        validateResult(result);
        
        return result;
    }
    
    protected void verifyCandidate(ClientProvider candidate,
            String url, Map<String, Object> transportOptions) {
        verify(candidate, times(1)).createClient(eq(url), eq(transportOptions));
    }
    
    protected void validateResult(ClientSession result) {
        assertEquals(result, this.getClientSession());
    }

    @Test
    public void createClient_GivenInvalidUrl_ShouldThrowException() {

        final String url = "http2://www";
        final Map<String, Object> transportOptions = this.getValidTransportOptions();
        
        this.createClient_GivenArgs_ShouldThrowException("createClient_GivenInvalidUrl_ShouldThrowException", url, transportOptions);
    }
    
    @Test
    public void createClient_GivenEmptyUrl_ShouldThrowException() {

        final String url = "";
        final Map<String, Object> transportOptions = this.getValidTransportOptions();
        
        this.createClient_GivenArgs_ShouldThrowException("createClient_GivenEmptyUrl_ShouldThrowException", url, transportOptions);
    }
    
    @Test
    public void createClient_GivenNullUrl_ShouldThrowException() {

        final String url = null;
        final Map<String, Object> transportOptions = this.getValidTransportOptions();
        
        this.createClient_GivenArgs_ShouldThrowException("createClient_GivenNullUrl_ShouldThrowException", url, transportOptions);
    }
    
    protected void createClient_GivenArgs_ShouldThrowException(
            String methodName, String url, Map<String, Object> transportOptions) {
        final String description = this.getDescription(methodName);
        System.out.println(description);
        
        final ClientProvider candidate = this.getCandidate();
        
        try{
            
            final ClientSession result = candidate.createClient(url, transportOptions);

            fail(description + ", BUT completed execution");
            
        }catch(Exception expected) { }
    }

    protected String getDescription(String testMethodName) {
        return getTestConfig().testUtil().getDescription(testMethodName);
    }

    protected String getValidUrl(){
        return "http://localhost:8080";
    }
    
    protected Map<String, Object> getValidTransportOptions() {
        return new HashMap<>();
    }
    
    protected ClientProvider getCandidate() {
        return getMocker().mock(getClientProvider(), getClientSession());
    }
    
    protected Mocker getMocker() {
        return getTestConfig().mocker();
    }
    
    protected TestConfig getTestConfig() {
        return new TestConfig();
    }

    protected ClientProvider getClientProvider() {
        return clientProvider;
    }

    protected ClientSession getClientSession() {
        return clientSession;
    }
}
