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

import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.stream.Stream;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * As of the time of writing, this class used experimental types from 
 * the org.junit.jupiter.params package.
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestChatConfiguration.class})
public class MessageListenerWithDataFiltersTest {
    
    @Autowired private ServerChannel.MessageListener serverChannelMessageListener;
    
    static class ValidArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(
                ExtensionContext context) throws Exception {
            final TestArguments testObjs = new TestArguments();
            return Stream.of(
                    Arguments.of(testObjs.getServerSession(), 
                            testObjs.getServerChannel(),
                            testObjs.getServerMessage())
            );
        }
    }
    
    static class InvalidArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(
                ExtensionContext context) throws Exception {
            final TestArguments testObjs = new TestArguments();
            return Stream.of(
                    Arguments.of(null, 
                            testObjs.getServerChannel(),
                            testObjs.getServerMessage()),
                    Arguments.of(testObjs.getServerSession(), 
                            null,
                            testObjs.getServerMessage()),
                    Arguments.of(testObjs.getServerSession(), 
                            testObjs.getServerChannel(),
                            null)
            );
        }
    }
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;

    @DisplayName("When onMessage(...) is called with valid arguments, return true")
    @ParameterizedTest(name = "[{index}] -> session={0}, channel={1}, message={2}")
    @ArgumentsSource(ValidArgumentProvider.class)
    public void onMessage_whenValidArgs_shouldReturnSuccessfully(
            ServerSession session, ServerChannel channel, ServerMessage.Mutable message) {
        final boolean result = getServerChannelMessageListener()
                .onMessage(session, channel, message);
        assertThat(result, is(true));
    }

    @DisplayName("When onMessage(...) is called with invalid arguments, throw RuntimeException")
    @ParameterizedTest(name = "[{index}] -> session={0}, channel={1}, message={2}")
    @ArgumentsSource(InvalidArgumentProvider.class)
    public void onMessage_whenInvalidArgs_shouldThrowRuntimeException(
            ServerSession session, ServerChannel channel, ServerMessage.Mutable message) {
        
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> getServerChannelMessageListener().onMessage(session, channel, message));
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }

    public ServerChannel.MessageListener getServerChannelMessageListener() {
        return serverChannelMessageListener;
    }

    private static class TestArguments extends TestChatConfiguration{
        
        @Bean @Scope("prototype") public ServerChannel getServerChannel(){
            return getServerChannel(getChannelId());
        }

        @Override
        @Bean @Scope("prototype") public ServerMessage.Mutable getServerMessage(){
            return this.chatUtil().createSuccessMessage(
                    getClientId(), Chat.CHAT, getChatText());
        }
        
        public String getChannelId() {
            return "/chat/privatechat";
        }

        public String getClientId() {
            return Long.toHexString(System.currentTimeMillis());
        }

        public String getChatText() {
            return "Hi love";
        }
    }
}
