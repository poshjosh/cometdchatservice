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
import java.util.Objects;
import java.util.stream.Stream;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.filter.DataFilter;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * As of the time of writing, this class used experimental types from 
 * the org.junit.jupiter.params package.
 * @author USER
 */
public class MessageListenerWithDataFiltersTest {
    
    static class ValidArgumentProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(
                ExtensionContext context) throws Exception {
            final TestObjects testObjs = new TestObjects();
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
            final TestObjects testObjs = new TestObjects();
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
        final boolean result = getMessageListener()
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
                () -> getMessageListener().onMessage(session, channel, message));
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }
    
    public ServerChannel.MessageListener getMessageListener() {
        return new TestObjects().getServerChannelMessageListener();
    }

    static class TestObjects extends TestConfig{
        public ServerSession getServerSession() {
            return testChatObjects().getServerSession();
        }

        public ServerChannel getServerChannel(){
            return testChatObjects().getServerChannel(getChannelId());
        }

        public ServerMessage.Mutable getServerMessage(){
            return testChatObjects().createSuccessMessage(
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

        public ServerChannel.MessageListener getServerChannelMessageListener(){
            return new MessageListenerWithDataFilters(getDataFilterDummy());
        }

        public ServerChannel.MessageListener getServerChannelMessageListenerDummy(){
            return new ServerChannelMessageListenerDummy();
        }

        public DataFilter getDataFilterDummy() {
            return new DataFilterDummy();
        }

        private static final class ServerChannelMessageListenerDummy 
                implements ServerChannel.MessageListener{
            @Override
            public boolean onMessage(ServerSession sender, 
                    ServerChannel channel, 
                    ServerMessage.Mutable message) {
                Objects.requireNonNull(sender);
                Objects.requireNonNull(channel);
                return true;
            }
        }

        private static final class DataFilterDummy implements DataFilter{
            @Override
            public Object filter(ServerSession ss, ServerChannel sc, Object o) throws DataFilter.AbortException {
                Objects.requireNonNull(ss);
                Objects.requireNonNull(sc);
                return o;
            }
        }
    }
}
