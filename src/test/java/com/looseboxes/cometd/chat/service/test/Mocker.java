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

import com.looseboxes.cometd.chat.service.ChatSession;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseImpl;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.common.HashMapMessage;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.OngoingStubbing;
import static org.mockito.ArgumentMatchers.any;

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
        public LinientMocker(TestConfig testConfig) {
            super(testConfig);
        }
        @Override
        protected <T extends Object> OngoingStubbing<T> when(T methodCall) {
            return Mockito.lenient().when(methodCall);
        }
    }

    private final TestConfig testConfig;

    public Mocker(TestConfig testConfig) {
        this.testConfig = Objects.requireNonNull(testConfig);
    }
    
    /**
     * {@link org.mockito.Mockito Mockito} throws an UnsupportedStubbingException, 
     * when an initialised mock is not called by one of the test methods during 
     * execution. The instance returned by this method avoids this strict stub 
     * checking by using the {@link org.mockito.Mockito#lenient() Mockito#lenient()} 
     * method.
     * @return A Mocker which does not carry out strict stub checking
     */
    public Mocker lenient() {
        return new LinientMocker(testConfig);
    }
    
    public ChatSession mock(ChatSession instance) {
        
        when(instance.connect()).thenReturn(this.createMessageFuture(true, true));
        when(instance.disconnect()).thenReturn(this.createMessageFuture(true, true));
        when(instance.getStatus()).thenReturn(new ChatSession.Status() {
            @Override
            public boolean isConnected() { return true; }
            @Override
            public boolean isDisconnecting() { return false; }
            @Override
            public boolean isSubscribedToChat() { return true; }
            @Override
            public boolean isSubscribedToMembers() { return true; }
        });
        when(instance.join()).thenReturn(this.createMessageFuture(true, true));
        when(instance.leave()).thenReturn(this.createMessageFuture(true, true));
        when(instance.send(any(String.class), any(String.class))).thenReturn(this.createMessageFuture(false, true));
//        when(instance.send(any(String.class), any(String.class), any(ClientSession.MessageListener.class)))
        when(instance.subscribe()).thenReturn(this.createMessageFuture(true, true));
        when(instance.unsubscribe()).thenReturn(this.createMessageFuture(true, true));
        return instance;
    }
    
    public Future<Message> createMessageFuture(boolean meta, boolean successful) {
        return CompletableFuture.completedFuture(this.createMessage(meta, successful));
    }
    
    private Message createMessage(boolean meta, boolean successful) {
        return this.createMessage(false, meta, false, successful);
    }
    
    private Message createMessage(boolean empty, boolean meta, 
            boolean publishReply, boolean successful) {
        final Message msg = new HashMapMessage();
        msg.put("empty", empty);
        msg.put("meta", meta);
        msg.put("publishReply", publishReply);
        msg.put("successful", successful);
        return msg;
    }

    public ClientSessionChannel mock(ClientSessionChannel instance) {
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
