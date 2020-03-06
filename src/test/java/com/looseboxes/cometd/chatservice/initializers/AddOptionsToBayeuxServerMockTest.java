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

import com.looseboxes.cometd.chatservice.chat.ChatServerOptionNames;
import com.looseboxes.cometd.chatservice.chat.MembersServiceInMemoryCache;
import com.looseboxes.cometd.chatservice.chat.MessageListenerWithDataFilters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class AddOptionsToBayeuxServerMockTest extends ChatServerInitActionMockTestBase{
       
    private static final Logger LOG = LoggerFactory.getLogger(ContextImpl.class);
    
    private static final class ContextImpl 
            implements ChatServerInitActionMockTestBase.Context<Map.Entry<String, Object>>{

        @Override
        public List<Map.Entry<String, Object>> getArgs(){
            return Arrays.asList(
                    getOption(ChatServerOptionNames.MEMBERS_SERVICE, 
                            new MembersServiceInMemoryCache()), 
                    getOption(ChatServerOptionNames.CHANNEL_MESSAGE_LISTENER, 
                            new MessageListenerWithDataFilters(
                                    (session, channel, object) -> { return object; }
                            )));
        }
        
        public Map.Entry<String, Object> getOption(String key, Object value) {
            return new HashMap.SimpleImmutableEntry<>(key, value);
        }

        @Override
        public void onApplyMethodCalled(BayeuxServer server, 
                List<Map.Entry<String, Object>> args) {
            for(Map.Entry<String, Object> entry : args) {
                LOG.debug("\nAdding option to BayeuxServer: {} = {}", 
                        entry.getKey(), entry.getValue());
                server.setOption(entry.getKey(), entry.getValue());
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, 
                List<Map.Entry<String, Object>> args) {

            final Set<String> expOptionNames = (Set<String>)args.stream()
                    .map((obj) -> obj.getKey())
                    .collect(Collectors.toSet());
            
            LOG.debug("Option names.\nExpected: {}\n   Found: {}",
                    expOptionNames, server.getOptionNames());
            
            assertThat(server.getOptionNames(), is(expOptionNames));
        }
    }
    
    public AddOptionsToBayeuxServerMockTest() { 
        super(new ContextImpl());
    }
}
/**
 * 
    
    @Override
    public BayeuxServer mockBayeuxServer(BayeuxServer bayeuxServer, List args) {
        
//@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        
        final Set<String> expOptionNames = (Set<String>)args.stream()
                .map((obj) -> ChatServerOptionNames.from(obj))
                .collect(Collectors.toSet());
        
        lenient().when(bayeuxServer.getOptionNames()).thenReturn(expOptionNames);
        
        for(Object opt : args) {
            lenient().when(bayeuxServer.getOption(ChatServerOptionNames.from(opt)).thenReturn(opt);
        }
        
        return bayeuxServer;
    }
 * 
 */