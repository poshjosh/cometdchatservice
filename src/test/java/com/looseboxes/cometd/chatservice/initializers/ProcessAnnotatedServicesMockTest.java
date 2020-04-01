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

import java.util.Arrays;
import java.util.List;
import org.cometd.annotation.Configure;
import org.cometd.annotation.RemoteCall;
import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.annotation.Service;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class ProcessAnnotatedServicesMockTest extends ChatServerInitActionMockTestBase{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProcessAnnotatedServicesMockTest.class);
    
    private static final class ContextImpl implements ChatServerInitActionMockTestBase.Context{
        @Override
        public List getArgs(){
            return Arrays.asList(new DummyAnnotatedService());
        }
        @Override
        public void onApplyMethodCalled(BayeuxServer server, List args) {
            final ServerAnnotationProcessor processor = new ServerAnnotationProcessor(server);
            for(Object svc : args) {
                processor.process(svc);
            }
        }
        @Override
        public void assertThatResultsAreValid(BayeuxServer server, List args) { }
    }
    
    public ProcessAnnotatedServicesMockTest() { 
        super(new ContextImpl());
    }
    
    @Service("DummyAnnotatedService")
    /**
     * Service classes must be public
     */
    public static final class DummyAnnotatedService{
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


