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

import com.looseboxes.cometd.chat.service.MembersService;
import com.looseboxes.cometd.chat.service.SafeContentService;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class AddOptionsToBayeuxServerMockTest extends ChatServerInitActionMockTestBase{
       
    private static final Logger LOG = LoggerFactory.getLogger(ContextImpl.class);
    
    private static final class ContextImpl 
            implements ChatServerInitActionMockTestBase.Context{

        @Override
        public List getArgs(){
            return Arrays.asList(mock(MembersService.class), mock(SafeContentService.class));
        }

        @Override
        public void onApplyMethodCalled(BayeuxServer server, List args) {
            for(Object opt : args) {
                LOG.debug("\nAdding option to BayeuxServer: {} = {}", 
                        opt.getClass().getSimpleName(), opt);
                server.setOption(opt.getClass().getSimpleName(), opt);
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List args) {

            final Set<String> expOptionNames = (Set<String>)args.stream()
                    .map((obj) -> obj.getClass().getSimpleName())
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
                .map((obj) -> obj.getClass().getSimpleName())
                .collect(Collectors.toSet());
        
        lenient().when(bayeuxServer.getOptionNames()).thenReturn(expOptionNames);
        
        for(Object opt : args) {
            lenient().when(bayeuxServer.getOption(opt.getClass().getSimpleName())).thenReturn(opt);
        }
        
        return bayeuxServer;
    }
 * 
 */