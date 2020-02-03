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
package com.looseboxes.cometd.chat.service.test;

import com.looseboxes.cometd.chat.service.ClientProvider;
import com.looseboxes.cometd.chat.service.ClientSessionChannelSubscription;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseImpl;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

/**
 * Provide default mocks for object methods.
 * @author USER
 */
public class Mocker {
    
    /**
     * {@link org.mockito.Mockito Mockito} throws an UnsupportedStubbingException, 
     * when an initialised mock is not called by one of the test methods during 
     * execution. This class avoids this strict stub checking by using the 
     * {@link org.mockito.Mockito#lenient() Mockito#lenient()} method.
     */
    private static final class LinientMocker extends Mocker{
        @Override
        protected <T extends Object> OngoingStubbing<T> when(T methodCall) {
            return Mockito.lenient().when(methodCall);
        }
    }
    
    /**
     * @return A Mocker which does not carry out strict stub checking
     * @see Mocker#lenientInstance() 
     */
    public Mocker lenient() {
        return Mocker.lenientInstance();
    }
    
    /**
     * {@link org.mockito.Mockito Mockito} throws an UnsupportedStubbingException, 
     * when an initialised mock is not called by one of the test methods during 
     * execution. The instance returned by this method avoids this strict stub 
     * checking by using the {@link org.mockito.Mockito#lenient() Mockito#lenient()} 
     * method.
     * @return A Mocker which does not carry out strict stub checking
     */
    public static Mocker lenientInstance() {
        return new LinientMocker();
    }
    
    public ClientSessionChannelSubscription mock(ClientSessionChannelSubscription instance) {
        when(instance.subscribe(any(ClientSession.class), any(String.class), any(long.class)))
                .thenAnswer((InvocationOnMock invoc) -> {
                    final Object arg0 = invoc.getArgument(0);
                    Objects.requireNonNull(arg0);
                    final String channel = invoc.getArgument(1, String.class);
                    Objects.requireNonNull(channel);
                    if(channel.isEmpty()) {
                        throw new IllegalArgumentException();
                    }
                    final ResponseImpl res = new ResponseImpl();
                    res.setCode(200);
                    res.setSuccess(true);
                    res.setMessage("subcsribed");
                    return res;
        });
        return instance;
    }

    public ResponseBuilder mock(ResponseBuilder instance) {
        return mock(instance, (invoc) -> invoc.getArgument(2, Boolean.class) ? 500 : 200);
    }
    
    public ResponseBuilder mock(ResponseBuilder instance, Integer responseCode) {
        return mock(instance, (invoc) -> responseCode);
    }
    
    private ResponseBuilder mock(ResponseBuilder instance, 
            Function<InvocationOnMock, Integer> codeProvider) {
        when(instance.buildErrorResponse(any(Object.class))).thenCallRealMethod();
        when(instance.buildErrorResponse(any(Throwable.class))).thenCallRealMethod();
        when(instance.buildErrorResponse(any(Object.class), any(Throwable.class))).thenCallRealMethod();
        when(instance.buildResponse(any(Object.class), any(String.class), any(Object.class), any(boolean.class))).thenCallRealMethod();
        when(instance.buildSuccessResponse()).thenCallRealMethod();
        when(instance.buildErrorResponse(any(Object.class))).thenCallRealMethod();
        when(instance.buildResponse(any(Object.class), any(Object.class), any(boolean.class))).then(
            (InvocationOnMock invocation) -> {
                final ResponseImpl res = new ResponseImpl();
                res.setMessage(invocation.getArgument(0)==null?null:invocation.getArgument(0).toString());
                res.setData(invocation.getArgument(1));
                res.setSuccess( ! invocation.getArgument(2, Boolean.class));
                res.setCode(codeProvider.apply(invocation));
                return res;
            }
        );
        return instance;
    }
    
    public ClientProvider mock(ClientProvider instance, ClientSession clientSession) {
        when(instance.createClient(any(String.class), any(Map.class)))
                .thenAnswer((InvocationOnMock iom) -> {
            final String url = iom.getArgument(0, String.class);
            Objects.requireNonNull(url);
            new URL(url); // If invalid url, will throw MalformedURLException
            final Map transportOptions = iom.getArgument(1, Map.class);
            Objects.requireNonNull(transportOptions);
            return clientSession;
        });
        return instance;
    }
    
    public ClientSession mock(ClientSession instance, ClientSessionChannel channel) {
        when(instance.endBatch()).thenReturn(true);
        when(instance.getChannel(any(String.class))).thenReturn(channel);
        when(instance.getId()).thenReturn("1");
        when(instance.isConnected()).thenReturn(true);
        when(instance.isHandshook()).thenReturn(true);
        return instance;
    }
    
    protected <T extends Object> OngoingStubbing<T> when(T methodCall) {
        return Mockito.when(methodCall);
    }
}
