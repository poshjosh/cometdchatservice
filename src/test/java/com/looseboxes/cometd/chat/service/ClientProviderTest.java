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

import com.looseboxes.cometd.chat.service.handlers.response.ResponseConfiguration;
import java.util.Map;
import java.util.Objects;
import org.cometd.bayeux.client.ClientSession;
import static org.junit.Assert.assertFalse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ClientProviderTest extends ClientProviderMockTest{
    
    private static AppConfiguration appConfig;
    
    public ClientProviderTest() { }
    
    @BeforeAll
    public static void setUpClass() {
        appConfig = new AppConfiguration(new ResponseConfiguration());
    }

    @Override
    protected void validateResult(ClientSession result) { 
        Objects.requireNonNull(result);
        assertFalse(result.isConnected());
        assertFalse(result.isHandshook());
    }

    @Override
    protected void verifyCandidate(ClientProvider candidate,
            String url, Map<String, Object> transportOptions) { }
    
    @Test
    @Override
    public void createClient_GivenNullUrl_ShouldThrowException() {
        super.createClient_GivenNullUrl_ShouldThrowException();
    }

    @Test
    @Override
    public void createClient_GivenInvalidUrl_ShouldThrowException() {
        super.createClient_GivenInvalidUrl_ShouldThrowException(); 
    }

    @Test
    @Override
    public void createClient_GivenEmptyUrl_ShouldThrowException() {
        super.createClient_GivenEmptyUrl_ShouldThrowException(); 
    }

    @Test
    @Override
    public void createClient_GivenValidUrlAndTransportOptions_ShouldCreateAndReturnNewClientSession() {
        super.createClient_GivenValidUrlAndTransportOptions_ShouldCreateAndReturnNewClientSession(); 
    }

    @Override
    protected ClientProvider getCandidate() {
        return this.getClientProvider();
    }

    @Override
    protected ClientProvider getClientProvider() {
        return appConfig.clientProvider();
    }
}
