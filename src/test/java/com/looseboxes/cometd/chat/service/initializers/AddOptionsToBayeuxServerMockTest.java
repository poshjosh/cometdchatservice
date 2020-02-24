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
import java.util.function.BiConsumer;
import org.cometd.bayeux.server.BayeuxServer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author USER
 */
public class AddOptionsToBayeuxServerMockTest extends BayeuxInitActionMockTestBase{
       
    // @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
    @ExtendWith(MockitoExtension.class)
    private static final class ContextImpl implements BayeuxInitActionMockTestBase.Context{
        
        @Mock private MembersService membersService;
        @Mock private SafeContentService safeContentService;
        
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
            return Arrays.asList(membersService, safeContentService);
        }
        @Override
        public BayeuxServer mockBayeuxServer(List args) {
            for(Object option : args) {
                doNothing().when(test.getBayeuxServer()).setOption(getId(option), option);
            }
            return test.getBayeuxServer();
        }
        @Override
        public BiConsumer<BayeuxServer, Object> getActionToInvokeWhenApplyMethodIsCalled() {
            return (server, option) -> {
                server.setOption(getId(option), option);
            };
        }
        private String getId(Object obj) {
            return obj.getClass().getName();
        }
    }
    
    public AddOptionsToBayeuxServerMockTest() { 
        super(new ContextImpl());
    }
}
