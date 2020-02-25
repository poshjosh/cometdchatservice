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
package com.looseboxes.cometd.chat.service.initializers;

import java.util.Arrays;
import java.util.List;
import org.cometd.bayeux.MarkedReference;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;

/**
 * @author USER
 */
public class CreateDefaultChannelsIfAbsentTest extends BayeuxInitActionMockTestBase<String>{

    private static final ConfigurableServerChannel.Initializer init = new ConfigurableServerChannel.Initializer.Persistent();
    
    private static final class ContextImpl implements BayeuxInitActionMockTestBase.Context<String>{
        private final BayeuxInitActionMockTestBase test;
        public ContextImpl() {
            this(null);
        }
        public ContextImpl(BayeuxInitActionMockTestBase test) {
            this.test = test;
        }
        @Override
        public BayeuxInitActionMockTestBase.Context<String> with(BayeuxInitActionMockTestBase test) {
            return new ContextImpl(test);
        }
        @Override
        public List<String> getArgs(){
            return Arrays.asList("/service/privatechat");
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List<String> args) {
            for(String channel : args) {
                server.createChannelIfAbsent(channel, init);
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List<String> args) { }
    }
    
    public CreateDefaultChannelsIfAbsentTest() { 
        super(new ContextImpl());
    }

    @Override
    public CreateDefaultChannelsIfAbsent createBayeuxInitAction() {
        final CreateDefaultChannelsIfAbsent bayeuxInitAction = mock(CreateDefaultChannelsIfAbsent.class);
        return bayeuxInitAction;
    }
    
    @Override
    public BayeuxServer createBayeuxServer(List<String> args) {
        final BayeuxServer bayeuxServer = super.createBayeuxServer(args);
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        for(String channel : args) {
            lenient().when(bayeuxServer.createChannelIfAbsent(channel, init))
                    .thenReturn(MarkedReference.empty());
        }
        return bayeuxServer;
    } 
}
