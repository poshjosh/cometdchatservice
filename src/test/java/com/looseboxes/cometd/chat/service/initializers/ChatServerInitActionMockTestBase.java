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
import java.util.Objects;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.times;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ChatServerInitActionMockTestBase<T>{

    private static final Logger LOG = LoggerFactory.getLogger(ChatServerInitActionMockTestBase.class);
    
    public static interface Context<T>{
        List<T> getArgs();
        void mockWhenApplyMethodIsCalled(BayeuxServer server, List<T> args);
        void assertThatResultsAreValid(BayeuxServer server, List<T> args);
    }
    
    private final ChatServerInitActionMockTestBase.Context<T> context;
    
    public ChatServerInitActionMockTestBase(ChatServerInitActionMockTestBase.Context<T> context) { 
        this.context = Objects.requireNonNull(context);
    }

    @Test
    public void apply_whenCalledMultipleTimesWithValidArgs_shouldReturnUpdatedResultForEachCall() {
        System.out.println("apply_whenCalledMultipleTimesWithValidArgs_shouldReturnUpdatedResultForEachCall");
        
        final List<T> args = context.getArgs();
        
        BayeuxServer bayeuxServer = this.getBayeuxServer(args);
        ChatServerInitAction serverInitAction = getServerInitAction(bayeuxServer, args);
        this.invokeActionThenAssertThatResultsAreValid(serverInitAction, bayeuxServer, args);

        bayeuxServer = this.getBayeuxServer(args);
        serverInitAction = getServerInitAction(bayeuxServer, args);
        this.invokeActionThenAssertThatResultsAreValid(serverInitAction, bayeuxServer, args);
    }
    
    @Test
    public void apply_whenCalledWithValidArgs_shouldReturnUpdatedResult() {
        System.out.println("apply_whenCalledWithValidArgs_shouldReturnUpdatedResult");
        
        final List<T> args = context.getArgs();
        
        final BayeuxServer bayeuxServer = this.getBayeuxServer(args);

        final ChatServerInitAction serverInitAction = getServerInitAction(bayeuxServer, args);

        this.invokeActionThenAssertThatResultsAreValid(serverInitAction, bayeuxServer, args);
    }
    
    @Test
    public void apply_whenCalledWithEmptyArgs_shouldReturnUnchangedResult() {
        System.out.println("apply_whenCalledWithEmptyArgs_shouldReturnUnchangedResult");
        
        final List<T> args = Collections.EMPTY_LIST;

        final BayeuxServer bayeuxServer = this.getBayeuxServer(args);

        final ChatServerInitAction serverInitAction = getServerInitAction(bayeuxServer, args);

        this.invokeActionThenAssertThatResultsAreValid(serverInitAction, bayeuxServer, args);
    }

    @Test
    public void apply_whenCalledWithNullArg_shouldThrowException() {
        System.out.println("apply_whenCalledWithNullArg_shouldReturnUnchangedResult");
        
        try{
            
            final List<T> args = null;
            
            final BayeuxServer bayeuxServer = this.getBayeuxServer(args);
            
            final ChatServerInitAction serverInitAction = getServerInitAction(bayeuxServer, args);

            this.invokeAction(serverInitAction, bayeuxServer, args);
            
            fail("Should fail but exection completed");
        
        }catch(Exception expected) { }
    }
    
    public BayeuxServer invokeActionThenAssertThatResultsAreValid(
            ChatServerInitAction<T> bayeuxInitAction, 
            BayeuxServer bayeuxServer, List<T> args){
        
        final BayeuxServer ret = this.invokeAction(bayeuxInitAction, bayeuxServer, args);
        
        LOG.debug("\nContext: {}, BayeuxServer: {}, arguments: {}", 
                context, bayeuxServer, args);
        
        context.assertThatResultsAreValid(bayeuxServer, args);
        
        return ret;
    }
    
    public BayeuxServer invokeAction(
            ChatServerInitAction<T> bayeuxInitAction, 
            BayeuxServer bayeuxServer, List<T> args){

        final BayeuxServer found = bayeuxInitAction.apply(bayeuxServer, args);

        verifyActionWasInvokedOnce(bayeuxInitAction, bayeuxServer, found, args);

        return found;
    }
    
    public void verifyActionWasInvokedOnce(
            ChatServerInitAction<T> bayeuxInitAction, 
            BayeuxServer a, BayeuxServer b, List<T> args){

        verify(bayeuxInitAction, times(1)).apply(a, args);
    }
    
    public ChatServerInitAction<T> getServerInitAction(BayeuxServer bayeuxServer, List<T> args) {
        
        final ChatServerInitAction bayeuxInitAction = mock(ChatServerInitAction.class);

        this.mockBayeuxInitAction(bayeuxInitAction);

        return bayeuxInitAction;
    }
    
    public ChatServerInitAction mockBayeuxInitAction(ChatServerInitAction bayeuxInitAction) {
        return this.mockWhenApplyMethodIsCalled(bayeuxInitAction);
    }
    
    private ChatServerInitAction mockWhenApplyMethodIsCalled(ChatServerInitAction bayeuxInitAction) {
        //@TODO remove lenient... Without lenient throws UnnecessaryStubbingException
        lenient().when(bayeuxInitAction.apply(isA(BayeuxServer.class), isA(List.class)))
            .thenAnswer((InvocationOnMock iom) -> {
                final BayeuxServer server = (BayeuxServer)iom.getArgument(0);
                LOG.debug("\nServer: {}", server);
                Objects.requireNonNull(server);
                final List<T> listArg = (List<T>)iom.getArgument(1);
                LOG.debug("\n   Args: {}", listArg);
                Objects.requireNonNull(listArg);
                context.mockWhenApplyMethodIsCalled(server, listArg);
                return server;
            }
        );
        lenient().when(bayeuxInitAction.apply(isA(BayeuxServer.class), (List)isNull()))
                .thenThrow(NullPointerException.class);
        lenient().when(bayeuxInitAction.apply(isNull(), isA(List.class)))
                .thenThrow(NullPointerException.class);
        return bayeuxInitAction;
    }
    
    public BayeuxServer getBayeuxServer(List<T> args) {
        return new BayeuxServerImpl();
    }
}
