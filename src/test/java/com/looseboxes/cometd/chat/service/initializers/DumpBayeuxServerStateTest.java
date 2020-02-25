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

import java.util.Collections;
import java.util.List;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;

/**
 * @author USER
 */
public class DumpBayeuxServerStateTest extends BayeuxInitActionMockTestBase{
    
    private static final String SAMPLE_DUMP = "Sample Dump";
       
    private static final class ContextImpl implements BayeuxInitActionMockTestBase.Context{
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
            return Collections.EMPTY_LIST;
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List args) {
            if(server instanceof BayeuxServerImpl) {
                ((BayeuxServerImpl)server).dump();
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List args) { }
    }
    
    public DumpBayeuxServerStateTest() { 
        super(new ContextImpl());
    }

    @Override
    public DumpBayeuxServerState createBayeuxInitAction() {
        final DumpBayeuxServerState bayeuxInitAction = mock(DumpBayeuxServerState.class);
        return bayeuxInitAction;
    }
    
    @Override
    public BayeuxServer createBayeuxServer(List args) {
        final BayeuxServerImpl bayeuxServer = mock(BayeuxServerImpl.class);
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        lenient().when(bayeuxServer.dump()).thenReturn(SAMPLE_DUMP);
        return bayeuxServer;
    } 
}

