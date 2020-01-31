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

import com.looseboxes.cometd.chat.service.requesthandler.Await;
import com.looseboxes.cometd.chat.service.requesthandler.Response;
import com.looseboxes.cometd.chat.service.requesthandler.ResponseImpl;
import com.looseboxes.cometd.chat.service.requesthandler.exceptions.ProcessingRequestTimeoutException;
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
public final class ClientSessionChannelSubscriptionImpl implements 
        ClientSessionChannelSubscription, ClientSessionChannel.MessageListener{

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionChannelSubscriptionImpl.class);
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final Supplier<ResponseImpl> responseSupplier;
          
    private final Await await;
            
    private long startTime;
    
    private ResponseImpl response;

    public ClientSessionChannelSubscriptionImpl(Supplier<ResponseImpl> responseSupplier, Await await) {
        this.responseSupplier = Objects.requireNonNull(responseSupplier);
        this.await = Objects.requireNonNull(await);
    }
    
    @Override
    public Response subscribe(ClientSession client, String channel, long timeout) {
        
        this.subscribeSilently(client, channel, timeout);
        
        if( ! done.get()) {
            throw new ProcessingRequestTimeoutException("Timeout: " + timeout + " millis");
        }
    
        return response;
    }

    @Override
    public Response subscribeSilently(ClientSession client, String channel, long timeout) {
        
        this.requireNotDone();
        
        this.response = this.responseSupplier.get();

        this.startTime = System.currentTimeMillis();
    
        client.getChannel(channel).subscribe(this);
        
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
    public void onMessage(ClientSessionChannel csc, Message msg) {

        this.requireNotDone();

        try{
            
            LOG.trace("Subscription successful: {}, time spent: {} millis", 
                    msg.isSuccessful(), (System.currentTimeMillis() - startTime));
            
            this.response.setSuccess(msg.isSuccessful());

            if(msg.isSuccessful()) {
                this.onSuccess(csc, msg);
            }else{
                this.onError(null, csc, msg);
            }
        }catch(Exception e) {
            this.onError(e, csc, msg);
        }finally{
            done.compareAndSet(false, true);
        }   
    }

    private void onSuccess(ClientSessionChannel csc, Message msg) {
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Subcsribed to chat");
        response.setData(msg);
        done.compareAndSet(false, true);
    }
    
    private void onError(Exception e, ClientSessionChannel csc, Message msg) {
        
        final String message = "Subscription failed. Chat channel: " + csc.getChannelId();
        
        if(e != null) {
            LOG.warn(message, e);
        }
        
        response.setMessage("Failed to join chat");
        response.setData(msg);
        
        done.compareAndSet(false, true);
    }
    
    private void requireNotDone() {
        if(done.get()) {
            throw new IllegalStateException();
        }
    }
}


