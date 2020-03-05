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
package com.looseboxes.cometd.chatservice.services;

import com.looseboxes.cometd.chatservice.services.exceptions.InvalidRequestParameterException;
import com.looseboxes.cometd.chatservice.services.exceptions.ProcessingRequestException;
import com.looseboxes.cometd.chatservice.services.exceptions.ProcessingRequestInterruptedException;
import com.looseboxes.cometd.chatservice.services.exceptions.ProcessingRequestTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public final class ServletUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(ServletUtil.class);

    public <T> T waitForFuture(Future<T> future, long timeoutMillis) {
        
        LOG.trace("Will wait at most {} millis for future to return.", timeoutMillis);
        
        try{
            
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            
        }catch(ExecutionException e) {
            throw new ProcessingRequestException(e);
        }catch(TimeoutException e) {    
            throw new ProcessingRequestTimeoutException(e);
        }catch(InterruptedException e) {
            throw new ProcessingRequestInterruptedException(e);
        }
    }

    public String requireNonNullOrEmpty(ServletRequest req, String paramName) {
        final String paramValue = req.getParameter(paramName);
        if(paramValue == null || paramValue.isEmpty()) {
            throw new InvalidRequestParameterException(paramName + '=' + paramValue);
        }
        return paramValue;
    }
}
