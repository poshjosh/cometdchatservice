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
import com.looseboxes.cometd.chat.service.handlers.response.ResponseConfiguration;
import java.util.Objects;
import org.cometd.bayeux.client.ClientSession;
import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ClientSessionChannelSubscriptionTest extends ClientSessionChannelSubscriptionMockTest{

    private static AppConfiguration appConfig;
    
    public ClientSessionChannelSubscriptionTest() { }
    
    @BeforeAll
    public static void setUpClass() {
        appConfig = new AppConfiguration(new ResponseConfiguration());
    }

    @Override
    protected void validateResult(Response result) { 
        Objects.requireNonNull(result);
        assertTrue(result.isSuccess());
        assertEquals(result.getCode(), 200);
    }

    @Override
    protected void verifyCandidate(ClientSessionChannelSubscription candidate,
            ClientSession clientSession, String channel, long timeout) { }

    @Test
    @Override
    public void subscribe_GivenNullClientSession_ShouldThrowException() {
        super.subscribe_GivenNullClientSession_ShouldThrowException(); 
    }

    @Test
    @Override
    public void subscribe_GivenNullChannel_ShouldThrowException() {
        super.subscribe_GivenNullChannel_ShouldThrowException(); 
    }

    @Test
    @Override
    public void subscribe_GivenEmptyChannelText_ShouldThrowException() {
        super.subscribe_GivenEmptyChannelText_ShouldThrowException(); 
    }

    /**
     * WE CAN'T TEST THIS as we don't have a valid instance of
     * {@link org.cometd.bayeux.client.ClientSession ClientSession}
     */
    @Override
    public void subscribe_GivenValidArguments_ShouldCreateAndReturnValidResponse() { }
    
    @Override
    protected ClientSessionChannelSubscription getCandidate() {
        return this.getClientSessionChannelSubscription();
    }

    @Override
    protected ClientSessionChannelSubscription getClientSessionChannelSubscription() {
        return appConfig.clientSessionChannelSubscription();
    }
}
