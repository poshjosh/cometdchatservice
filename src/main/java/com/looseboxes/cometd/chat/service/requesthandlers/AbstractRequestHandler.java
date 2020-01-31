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
package com.looseboxes.cometd.chat.service.requesthandlers;

import com.looseboxes.cometd.chat.service.requesthandlers.exceptions.ProcessingRequestException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public abstract class AbstractRequestHandler implements RequestHandler<Response>{
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractRequestHandler.class);
    
    public AbstractRequestHandler() { }

    public abstract Response doProcess(HttpServletRequest req, HttpServletResponse res) throws ProcessingRequestException;

    @Override
    public void process(ServletRequest req, ServletResponse res, ResponseHandler<Response> callback) {
        
        final Response data = this.process(req, res);
        
        if(data.isSuccess()) {
            callback.onSuccess(req, res, data);
        }else{
            callback.onFailure(req, res, data);
        }
        
        callback.onAlways(req, res, data);
    }

    @Override
    public Response process(ServletRequest req, ServletResponse res) throws ProcessingRequestException{
    
        if(req instanceof HttpServletRequest) {
        
            final HttpServletRequest httpReq = (HttpServletRequest)req;
            
            return this.doProcess(httpReq, (HttpServletResponse)res);
            
        }else{

            //@TODO
            throw new UnsupportedOperationException();
        }
    }
}
