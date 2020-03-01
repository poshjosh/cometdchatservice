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
package com.looseboxes.cometd.chatservice.chat;

import com.looseboxes.cometd.chatservice.test.TestConfig;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.component.LifeCycle;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.Assertions;
import static org.hamcrest.CoreMatchers.is;

/**
 * @author USER
 */
public class HttpClientInitializerTest {
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    @Test
    public void init_whenNullArg_shouldThrowRuntimeException() {
        System.out.println("init_whenNullArg_shouldThrowRuntimeException");
        
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> this.init_givenArg_shouldReturn(null));
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }

    @Test
    public void init_whenValidArg_shouldReturnValidResult() {
        System.out.println("init_whenValidArg_shouldReturnValidResult");
        final HttpClient expResult = this.getHttpClient();

        final HttpClient result = this.init_givenArg_shouldReturn(expResult);
        
        assertTrue(result.isStarted());
    }

    @Test
    public void init_whenValidArg_shouldReturnSameArgument() {
        System.out.println("init_whenValidArg_shouldReturnSameArgument");
        final HttpClient expResult = this.getHttpClient();

        final HttpClient result = this.init_givenArg_shouldReturn(expResult);
        
        assertThat(result, is(expResult));
    }

    public HttpClient init_givenArg_shouldReturn(HttpClient arg) {

        final HttpClientInitializer initializer = this.getHttpClientInitializer();

        return initializer.init(arg);
    }
    
    public HttpClientInitializer getHttpClientInitializer() {
        return new HttpClientInitializerImpl();
    }
    
    public HttpClient getHttpClient() {
        return new HttpClientImpl();
    }
    
    private static final class HttpClientImpl extends HttpClient{
        private boolean started;
        private boolean stopped;
        @Override
        protected void doStart() throws Exception { started = true; }
        @Override
        protected void doStop() throws Exception { stopped = true; }
        @Override
        protected void start(LifeCycle l) throws Exception { started = true; }
        @Override
        protected void stop(LifeCycle l) throws Exception { stopped = true; }
        @Override
        public boolean isStarted() { return started; }
        @Override
        public boolean isStopped() { return stopped; }
        @Override
        public boolean isRunning() { return this.isStarted() && ! this.isStopped(); }
    }
}
