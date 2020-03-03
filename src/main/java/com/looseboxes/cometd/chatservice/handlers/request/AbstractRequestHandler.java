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
package com.looseboxes.cometd.chatservice.handlers.request;

import com.looseboxes.cometd.chatservice.handlers.response.Response;
import com.looseboxes.cometd.chatservice.handlers.response.ResponseHandler;
import com.looseboxes.cometd.chatservice.handlers.exceptions.ProcessingRequestException;
import com.looseboxes.cometd.chatservice.handlers.exceptions.ProcessingRequestInterruptedException;
import com.looseboxes.cometd.chatservice.handlers.exceptions.ProcessingRequestTimeoutException;
import com.looseboxes.cometd.chatservice.handlers.response.ResponseBuilder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public abstract class AbstractRequestHandler implements RequestHandler<Response>{
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRequestHandler.class);
    
    public AbstractRequestHandler() { }

    protected abstract Response doProcess(HttpServletRequest req, HttpServletResponse res) 
            throws ProcessingRequestException;
    
    public ResponseBuilder getResponseBuilder(ServletRequest req) {

        final WebApplicationContext webAppCtx = getWebAppContext(req);

        return webAppCtx.getBean(ResponseBuilder.class);
    }
    
    @Override
    public void process(ServletRequest req, ServletResponse res, ResponseHandler<Response> callback) {
        
        Response data = null;
        try{
            
            data = this.process(req, res);

            if(data.isSuccess()) {
                callback.onSuccess(req, res, data);
            }else{
                callback.onFailure(req, res, data);
            }
        }finally{
            callback.onAlways(req, res, data);
        }
    }

    @Override
    public Response process(ServletRequest req, ServletResponse res) {
        try{
            if(req instanceof HttpServletRequest) {

                final HttpServletRequest httpReq = (HttpServletRequest)req;

                return this.doProcess(httpReq, (HttpServletResponse)res);

            }else{

                //@TODO
                throw new UnsupportedOperationException();
            }
        }catch(ProcessingRequestException | UnsupportedOperationException e) {
            
            LOG.warn("Unexpected Exception", e);
        
            return this.getResponseBuilder(req).buildErrorResponse(e);
        }
    }
    
    protected Response awaitFutureThenBuildResponseFromResult(String ID, 
            Future<Message> future, long timeout, ResponseBuilder resBuilder) {

        LOG.trace("Will wait at most {} millis for {} to return.", timeout, ID);

        final Message chatResponse = get(future, timeout);

        final boolean success = chatResponse.isSuccessful();

        return resBuilder.buildResponse(ID +" = " + success, chatResponse, ! success);
    }
    
    protected <T> T get(Future<T> future, long timeoutMillis) {
        
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
    
    protected WebApplicationContext getWebAppContext(ServletRequest req) {
        return WebApplicationContextUtils
            .getRequiredWebApplicationContext(req.getServletContext());
    }
}
