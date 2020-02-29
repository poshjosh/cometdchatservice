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

import java.util.Collections;
import java.util.List;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;

/**
 * @author USER
 */
public class DumpBayeuxServerStateMockTest extends ChatServerInitActionMockTestBase{
    
//    private static final String SAMPLE_DUMP = "Sample Dump";
       
    private static final class ContextImpl implements ChatServerInitActionMockTestBase.Context{
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
    
    public DumpBayeuxServerStateMockTest() { 
        super(new ContextImpl());
    }
}

