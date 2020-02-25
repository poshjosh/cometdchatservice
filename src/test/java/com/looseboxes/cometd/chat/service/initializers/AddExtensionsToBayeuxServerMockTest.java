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
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.BayeuxServer.Extension;
import org.cometd.server.ext.AcknowledgedMessagesExtension;
import org.cometd.server.ext.TimesyncExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.lenient;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class AddExtensionsToBayeuxServerMockTest extends BayeuxInitActionMockTestBase<Extension>{
       
    private static final class ContextImpl implements BayeuxInitActionMockTestBase.Context<Extension>{
        private final BayeuxInitActionMockTestBase test;
        public ContextImpl() {
            this(null);
        }
        public ContextImpl(BayeuxInitActionMockTestBase test) {
            this.test = test;
        }
        @Override
        public BayeuxInitActionMockTestBase.Context<Extension> with(BayeuxInitActionMockTestBase test) {
            return new ContextImpl(test);
        }
        @Override
        public List<Extension> getArgs(){
            return Arrays.asList(new TimesyncExtension(), new AcknowledgedMessagesExtension());
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List<Extension> args) {
            for(Extension ext : args) {
                server.addExtension(ext);
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List<Extension> args) {
            assertThat(server.getExtensions(), is(args));
        }
    }
    
    public AddExtensionsToBayeuxServerMockTest() { 
        super(new ContextImpl());
    }

    @Override
    public AddExtensionsToBayeuxServer createBayeuxInitAction() {
        final AddExtensionsToBayeuxServer bayeuxInitAction = mock(AddExtensionsToBayeuxServer.class);
        return bayeuxInitAction;
    }
    
    @Override
    public BayeuxServer createBayeuxServer(List<Extension> args) {
        final BayeuxServer bayeuxServer = super.createBayeuxServer(args);
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        lenient().when(bayeuxServer.getExtensions()).thenReturn(args);
        return bayeuxServer;
    } 
}
