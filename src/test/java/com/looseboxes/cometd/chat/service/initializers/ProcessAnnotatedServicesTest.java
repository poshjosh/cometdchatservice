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
import org.cometd.annotation.Configure;
import org.cometd.annotation.RemoteCall;
import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.annotation.Service;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import static org.mockito.Mockito.mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mock;

/**
 * @author USER
 */
public class ProcessAnnotatedServicesTest extends BayeuxInitActionMockTestBase{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProcessAnnotatedServicesTest.class);
    
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
            return Arrays.asList(new MembersServiceInMemoryCache());
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List args) {
            final ServerAnnotationProcessor processor = new ServerAnnotationProcessor(server);
            processor.process(new DummyAnnotatedService());
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List args) { }
    }
    
    public ProcessAnnotatedServicesTest() { 
        super(new ContextImpl());
    }

    @Override
    public ProcessAnnotatedServices createBayeuxInitAction() {
        final ProcessAnnotatedServices bayeuxInitAction = mock(ProcessAnnotatedServices.class);
        return bayeuxInitAction;
    }
    
    @Service("DummyAnnotatedService")
    private static final class DummyAnnotatedService{
        @Configure("/service/echo")
        private void configureActor(ConfigurableServerChannel channel) {
            LOG.info("Configuring: {}", channel);
        }
        @RemoteCall("echo")
        public void doAction(RemoteCall.Caller caller, Object data) {
            LOG.info("ECHO from " + caller.getServerSession() + ": " + data);
            caller.result(data);
        }
    }
}


