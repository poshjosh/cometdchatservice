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

import com.looseboxes.cometd.chatservice.AbstractBuilderTest;
import com.looseboxes.cometd.chatservice.Builder;
import org.junit.jupiter.api.Test;

/**
 * @author USER
 */
public class ChatConfigBuilderTest extends AbstractBuilderTest{

    @Test
    public void channel_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "channel", "/chat/privatechat");
    }
    
    @Test
    public void room_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "room", "test_room");
    }

    @Test
    public void user_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "user", "test_user");
    }
    
    @Test
    public void logLevel_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "logLevel", Chat.LOG_LEVEL_VALUES.INFO);
    }

    @Test
    public void webSocketEnabled_whenCalled_shouldUpdateBuilderWithCallArg() {
        this.whenMethodCalled_shouldUpdateBuilderWithCallArg(
                "websocketEnabled", boolean.class, true);
    }

    @Override
    public Builder getBuilder() {
        return new ChatConfigBuilderImpl();
    }
}
