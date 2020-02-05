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
package com.looseboxes.cometd.chat.service.handlers.request;

import com.looseboxes.cometd.chat.service.AttributeNames;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseHandler;
import com.looseboxes.cometd.chat.service.handlers.exceptions.ProcessingRequestException;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.client.ClientSession;
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

    public abstract Response doProcess(HttpServletRequest req, HttpServletResponse res) throws ProcessingRequestException;
    
    /**
     * @param req
     * @param res
     * @return {@link com.looseboxes.cometd.chat.service.handlers.response.Response Response} 
     * object with success set to true if previously joined to chat or
     * successfully joined to chat during this methods execution, otherwise 
     * return false.
     */
    public Response joinIfNotAlready(HttpServletRequest req, HttpServletResponse res) {
        
        final WebApplicationContext webAppCtx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(req.getServletContext());
        
        final boolean joinedToChat = this.isJoinedToChat(req, res);
        
        if( ! joinedToChat) {
        
            final JoinHandler jh = webAppCtx.getBean(JoinHandler.class);

            final Response jhr = jh.process(req, res);

            if(!jhr.isSuccess()) {

                return jhr;
            }
        }
        
        return webAppCtx.getBean(ResponseBuilder.class).buildSuccessResponse();
    }

    public boolean isJoinedToChat(HttpServletRequest req, HttpServletResponse res) {
        
        final ClientSession client = (ClientSession)req.getSession().getAttribute(
                AttributeNames.Session.COMETD_CLIENT_SESSION);
        
        return client == null ? false : client.isHandshook();
    }
    
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
        
            final WebApplicationContext webAppCtx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(req.getServletContext());

            return webAppCtx.getBean(ResponseBuilder.class).buildErrorResponse(e);
        }
    }
}
