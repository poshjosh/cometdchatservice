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
package com.looseboxes.cometd.chatservice.services.response;

import com.looseboxes.cometd.chatservice.AbstractBuilderTest;
import com.looseboxes.cometd.chatservice.Builder;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ResponseBuilderTest extends AbstractBuilderTest{

    @Test
    public void code_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "code", int.class, 100);
    }
    
    @Test
    public void data_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "data", new Object());
    }

    @Test
    public void message_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "message", "Test Message");
    }
    
    @Test
    public void success_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "success", boolean.class, true);
    }

    @Override
    public Builder getBuilder() {
        return new ChatResponseBuilder();
    }
}
