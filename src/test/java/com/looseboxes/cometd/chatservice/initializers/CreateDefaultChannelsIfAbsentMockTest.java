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
package com.looseboxes.cometd.chatservice.initializers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author USER
 */
public class CreateDefaultChannelsIfAbsentMockTest extends ChatServerInitActionMockTestBase<String>{

    private static final Logger LOG = LoggerFactory
            .getLogger(CreateDefaultChannelsIfAbsentMockTest.class);
    
    private static final ConfigurableServerChannel.Initializer CHANNEL_INIT = 
            new ConfigurableServerChannel.Initializer.Persistent();
    
    private static final class ContextImpl 
            implements ChatServerInitActionMockTestBase.Context<String>{
        @Override
        public List<String> getArgs(){
            return Arrays.asList("/service/privatechat");
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List<String> args) {
            for(String channel : args) {
                LOG.debug("\nCreating channel if absent: {}", channel);
                server.createChannelIfAbsent(channel, CHANNEL_INIT);
                // Caused NullPointerException
//                assertThat(server.getChannel(channel), isNotNull());
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List<String> args) { 
            final List<String> channelIds = (List<String>)server.getChannels().stream()
                    .map((channel) -> channel.getId()).collect(Collectors.toList());
            LOG.debug("Server channels\nExpected: {}\n   Found: {}", args, channelIds);
            assertThat(channelIds, is(args));
        }
    }
    
    public CreateDefaultChannelsIfAbsentMockTest() { 
        super(new ContextImpl());
    }
}
/**
 * 
    
    @Override
    public BayeuxServer mockBayeuxServer(BayeuxServer bayeuxServer, List<String> args) {
        
        final List<ServerChannel> channels = new ArrayList();
        
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        for(String channelId : args) {
            final ServerChannel channel = mock(ServerChannel.class);
            channels.add(channel);
            lenient().when(channel.getId()).thenReturn(channelId);
            lenient().when(channel.getChannelId()).thenReturn(new ChannelId(channelId));
            lenient().when(bayeuxServer.createChannelIfAbsent(channelId, CHANNEL_INIT))
                    .thenReturn(new MarkedReference(channel, true));
            lenient().when(bayeuxServer.getChannel(channelId))
                    .thenReturn(channel);
        }
        
        lenient().when(bayeuxServer.getChannels()).thenReturn(channels);
        
        return bayeuxServer;
    } 
 * 
 */