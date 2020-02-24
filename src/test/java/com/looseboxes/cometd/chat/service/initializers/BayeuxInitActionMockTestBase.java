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
import java.util.function.BiConsumer;
import org.cometd.bayeux.server.BayeuxServer;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class BayeuxInitActionMockTestBase<T> {
    
    @Mock private BayeuxInitAction<T> bayeuxInitAction;
    @Mock private BayeuxServer bayeuxServer;
    
    public static interface Context<T>{
        Context<T> with(BayeuxInitActionMockTestBase test);
        List<T> getArgs();
        BayeuxServer mockBayeuxServer(List<T> args);
        BiConsumer<BayeuxServer, T> getActionToInvokeWhenApplyMethodIsCalled();
    }
    
    private final Context<T> context;
    
    public BayeuxInitActionMockTestBase(Context<T> context) { 
        this.context = Objects.requireNonNull(context.with(this));
    }

    @Test
    public void apply_whenCalledMultipleTimesWithValidArgs_shouldReturnUpdatedResultForEachCall() {
        System.out.println("apply_whenCalledMultipleTimesWithValidArgs_shouldReturnUpdatedResultForEachCall");
        
        final List<T> args = this.mockWhenApplyIsCalled(
                context.getActionToInvokeWhenApplyMethodIsCalled());
        
        context.mockBayeuxServer(args);

        BayeuxServer result = this.callApplyThenVerify(args);
        assertThat(result.getExtensions(), is(args));

        result = this.callApplyThenVerify(args);
        assertThat(result.getExtensions(), is(args));
    }
    
    @Test
    public void apply_whenCalledWithValidArgs_shouldReturnUpdatedResult() {
        System.out.println("apply_whenCalledWithValidArgs_shouldReturnUpdatedResult");
        
        final List<T> args = this.mockWhenApplyIsCalled(
                context.getActionToInvokeWhenApplyMethodIsCalled());
        
        context.mockBayeuxServer(args);
        
        final BayeuxServer result = this.callApplyThenVerify(args);
        
        assertThat(result.getExtensions(), is(args));
    }
    
    @Test
    public void apply_whenCalledWithEmptyArgs_shouldReturnUnchangedResult() {
        System.out.println("apply_whenCalledWithEmptyArgs_shouldReturnUnchangedResult");
        
        final List<T> args = this.mockWhenApplyIsCalled(
                context.getActionToInvokeWhenApplyMethodIsCalled(), Collections.EMPTY_LIST);
        
        context.mockBayeuxServer(args);
        
        final BayeuxServer result = this.callApplyThenVerify(args);
        
        assertThat(result.getExtensions(), is(args));
    }

    @Test
    public void apply_whenCalledWithNullArg_shouldThrowException() {
        System.out.println("apply_whenCalledWithNullArg_shouldReturnUnchangedResult");
        
        try{
            final List<T> args = this.mockWhenApplyIsCalled(
                    context.getActionToInvokeWhenApplyMethodIsCalled(), null);

            context.mockBayeuxServer(args);

            final BayeuxServer result = this.callApplyThenVerify(args);
            
            fail("Should fail but exection completed");
        
        }catch(Exception expected) { }
    }
    
    public List<T> mockWhenApplyIsCalled(
            BiConsumer<BayeuxServer, T> actionToInvokeWhenApplyMethodIsCalled) {
        
        List<T> args = context.getArgs();
        
        args = this.mockWhenApplyIsCalled(actionToInvokeWhenApplyMethodIsCalled, args);
        
//        when(bayeuxInitAction.apply(isA(BayeuxServer.class), isNull())).thenThrow(NullPointerException.class);
        
        return args;
    }
    
    public List<T> mockWhenApplyIsCalled(
            BiConsumer<BayeuxServer, T> actionToInvokeWhenApplyMethodIsCalled, List<T> args) {
        when(bayeuxInitAction.apply(bayeuxServer, args)).thenAnswer((InvocationOnMock iom) -> {
            final BayeuxServer server = (BayeuxServer)iom.getArgument(0);
            for(T arg : args) {
                actionToInvokeWhenApplyMethodIsCalled.accept(server, arg);
            }
            return server;
        });
        return args;
    }

    public BayeuxServer callApplyThenVerify(List<T> args){
        final BayeuxServer result = bayeuxInitAction.apply(bayeuxServer, args);
        verify(bayeuxInitAction, times(1)).apply(bayeuxServer, args);
        return result;
    }

    public BayeuxInitAction<T> getBayeuxInitAction() {
        return bayeuxInitAction;
    }

    public BayeuxServer getBayeuxServer() {
        return bayeuxServer;
    }
}
