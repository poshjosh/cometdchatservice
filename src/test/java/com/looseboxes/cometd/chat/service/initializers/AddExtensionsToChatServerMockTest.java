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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class AddExtensionsToChatServerMockTest 
        extends ChatServerInitActionMockTestBase<Extension>{
    
    private static final Logger LOG = LoggerFactory.getLogger(AddExtensionsToChatServerMockTest.class);
       
    public static class ContextImpl 
            implements ChatServerInitActionMockTestBase.Context<Extension>{
        
        public ContextImpl() { }
        
        @Override
        public List<Extension> getArgs(){
            return Arrays.asList(new TimesyncExtension(), new AcknowledgedMessagesExtension());
        }
        @Override
        public void mockWhenApplyMethodIsCalled(BayeuxServer server, List<Extension> args) {
            for(Extension ext : args) {
                LOG.debug("\nAdding to mock BayeuxServer: {}", ext);
                server.addExtension(ext);
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List<Extension> args) {
            LOG.debug("Extensions added to BayeuxServer.\nExpected: {}\n   Found: {}",
                    args, server.getExtensions());
            assertThat(server.getExtensions(), is(args));
        }
    }
    
    public AddExtensionsToChatServerMockTest() { 
        super(new ContextImpl());
    }
}
/**
 * 
    @Override
    public BayeuxServer mockBayeuxServer(BayeuxServer bayeuxServer, List<Extension> args) {
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        lenient().when(bayeuxServer.getExtensions()).thenReturn(args);
        return bayeuxServer;
    }
 * 
 */