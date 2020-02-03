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
import java.util.Map;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ClientSessionPublisherTest extends ClientSessionPublisherMockTest{
    
    public ClientSessionPublisherTest() { }

    @Override
    protected void validateResult(Response result) { 
        getTestConfig().testData().validateSuccessResponse(result);
    }

    @Override
    protected void verifyCandidate(
            ClientSessionPublisher candidate, ClientSessionChannel channel, 
            Map<String, Object> message, long timeout) { }

    /**
     * WE SHOULDN'T TEST THIS HERE as it will call the actual server
     */
    @Override
    public void publish_GivenValidArguments_ShouldReturnSuccessResponse() { }

    /**
     * WE SHOULDN'T TEST THIS HERE as it will call the actual server
     */
    @Override
    public void publish_GivenNegativeTimeout_ShouldReturnSuccessResponse() { }

    /**
     * WE SHOULDN'T TEST THIS HERE as it will call the actual server
     */
    @Override
    public void publish_GivenEmptyMessage_ShouldReturnSuccessResponse() { }

    @Test
    @Override
    public void publish_GivenNullMessage_ShouldThrowException() {
        super.publish_GivenNullMessage_ShouldThrowException();
    }

    @Test
    @Override
    public void publish_GivenNullChannel_ShouldThrowException() {
        super.publish_GivenNullChannel_ShouldThrowException();
    }

    @Override
    protected ClientSessionPublisher getCandidate() {
        return this.getClientSessionPublisher();
    }

    @Override
    protected ClientSessionPublisher getClientSessionPublisher() {
        return getTestConfig().appConfig().clientSessionPublisher();
    }
}
