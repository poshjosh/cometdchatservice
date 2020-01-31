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
package com.looseboxes.cometd.chat.service;

import com.looseboxes.cometd.chat.service.requesthandlers.Await;
import com.looseboxes.cometd.chat.service.requesthandlers.Response;
import com.looseboxes.cometd.chat.service.requesthandlers.ResponseImpl;
import com.looseboxes.cometd.chat.service.requesthandlers.exceptions.ProcessingRequestTimeoutException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public final class ClientSessionPublisherImpl implements ClientSessionPublisher, ClientSession.MessageListener{

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionPublisherImpl.class);
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final Supplier<ResponseImpl> responseSupplier;
          
    private final Await await;
            
    private long startTime;
    
    private ResponseImpl response;

    public ClientSessionPublisherImpl(Supplier<ResponseImpl> responseSupplier, Await await) {
        this.responseSupplier = Objects.requireNonNull(responseSupplier);
        this.await = Objects.requireNonNull(await);
    }
    
    @Override
    public Response publish(final ClientSessionChannel channel, Map<String, Object> message, long timeout) {
        
        this.publishSilently(channel, message, timeout);
    
        if( ! done.get()) {
            throw new ProcessingRequestTimeoutException("Timeout: " + timeout + " millis");
        }
        
        return response;
    }

    @Override
    public Response publishSilently(final ClientSessionChannel channel, Map<String, Object> message, long timeout) {
        
        this.requireNotDone();
        
        this.response = this.responseSupplier.get();

        this.startTime = System.currentTimeMillis();
        
        channel.publish(message, this);
        
        await.tillTrue(done, this, timeout);
    
        if( ! done.get()) {
            response.setCode(HttpServletResponse.SC_GATEWAY_TIMEOUT);
            response.setMessage("Timeout: " + timeout + " millis");
            response.setSuccess(false);
            response.setData(null);
        }
        
        return response;
    }

    @Override
    public void onMessage(Message msg) {

        this.requireNotDone();

        try{
            
            LOG.trace("Publication successful: {}, time spent: {} millis", 
                    msg.isSuccessful(), (System.currentTimeMillis() - startTime));
            
            this.response.setSuccess(msg.isSuccessful());

            if(msg.isSuccessful()) {
                this.onSuccess(msg);
            }else{
                this.onError(null, msg);
            }
        }catch(Exception e) {
            this.onError(e, msg);
        }finally{
            done.compareAndSet(false, true);
        }   
    }

    private void onSuccess(Message msg) {
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Published message");
        response.setData(msg);
        done.compareAndSet(false, true);
    }
    
    private void onError(Exception e, Message msg) {
        
        final String message = "Failed to publish message";
        
        if(e != null) {
            LOG.warn(message, e);
        }
        
        response.setMessage(message);
        response.setData(msg);
        
        done.compareAndSet(false, true);
    }
    
    private void requireNotDone() {
        if(done.get()) {
            throw new IllegalStateException();
        }
    }
}



