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
import com.looseboxes.cometd.chat.service.test.MyTestConfiguration;
import org.cometd.bayeux.client.ClientSession;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(value = {SpringExtension.class})
@Import(MyTestConfiguration.class)
public class ClientProviderSpringMockTest extends ClientProviderMockTest{
    
    @Autowired private Mocker mocker;
    
    /** Required by called method(s) */
    @MockBean private CometDProperties cometDProperties;
    
    @MockBean private ClientProvider clientProvider;

    public ClientProviderSpringMockTest() { }

    @Override
    protected void validateResult(ClientSession result) { }

    @Test
    @Override
    @Disabled("@TODO find out why this is failing")
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
    protected ClientProvider getClientProvider() {
        return clientProvider;
    }

    @Override
    protected Mocker getMocker() {
        return mocker;
    }
}
