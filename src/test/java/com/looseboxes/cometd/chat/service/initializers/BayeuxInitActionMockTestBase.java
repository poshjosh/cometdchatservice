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

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.cometd.bayeux.server.BayeuxServer;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import static org.mockito.Mockito.verify;

/**
 * @author USER
 */
public class BayeuxInitActionMockTestBase<T> {
    
    @Mock private BayeuxInitAction<T> bayeuxInitAction;
    @Mock private BayeuxServer bayeuxServer;
    
    private final Supplier<List<T>> argsSupplier;
    
    public BayeuxInitActionMockTestBase(Supplier<List<T>> argsSupplier) { 
        this.argsSupplier = Objects.requireNonNull(argsSupplier);
    }
    
    public List<T> mockWhenApplyIsCalled(
            BiConsumer<BayeuxServer, T> actionToInvokeWhenApplyMethodIsCalled) {
        final List<T> args = argsSupplier.get();
        return this.mockWhenApplyIsCalled(actionToInvokeWhenApplyMethodIsCalled, args);
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
