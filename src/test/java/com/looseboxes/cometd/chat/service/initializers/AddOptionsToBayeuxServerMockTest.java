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

import com.looseboxes.cometd.chat.service.MembersServiceInMemoryCache;
import java.util.Arrays;
import java.util.List;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author USER
 */
public class AddOptionsToBayeuxServerMockTest extends BayeuxInitActionMockTestBase{
       
    // @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
    @ExtendWith(MockitoExtension.class)
    private static final class ContextImpl implements BayeuxInitActionMockTestBase.Context{

        private static final Logger LOG = LoggerFactory.getLogger(ContextImpl.class);
        
        private final BayeuxInitActionMockTestBase test;
        
        public ContextImpl() {
            this(null);
        }
        public ContextImpl(BayeuxInitActionMockTestBase test) {
            this.test = test;
        }
        @Override
        public BayeuxInitActionMockTestBase.Context with(BayeuxInitActionMockTestBase test) {
            return new ContextImpl(test);
        }
        @Override
        public List getArgs(){
            return Arrays.asList(new MembersServiceInMemoryCache());
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List args) {
            for(Object opt : args) {
                server.setOption(opt.getClass().getSimpleName(), opt);
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List args) {
            for(Object opt : args) {
                assertThat(server.getOption(opt.getClass().getSimpleName()), is(opt));
            }
        }
    }
    
    public AddOptionsToBayeuxServerMockTest() { 
        super(new ContextImpl());
    }

    @Override
    public AddOptionsToBayeuxServer createBayeuxInitAction() {
        final AddOptionsToBayeuxServer bayeuxInitAction = mock(AddOptionsToBayeuxServer.class);
        return bayeuxInitAction;
    }
    
    @Override
    public BayeuxServer createBayeuxServer(List args) {
        final BayeuxServer bayeuxServer = super.createBayeuxServer(args);
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        for(Object opt : args) {
            lenient().when(bayeuxServer.getOption(opt.getClass().getSimpleName())).thenReturn(opt);
        }
        return bayeuxServer;
    }
}
