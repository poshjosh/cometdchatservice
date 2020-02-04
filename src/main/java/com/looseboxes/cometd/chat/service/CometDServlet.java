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

import com.looseboxes.cometd.chat.service.handlers.response.JsonResponseHandler;
import com.looseboxes.cometd.chat.service.handlers.request.RequestHandlerFactory;
import com.looseboxes.cometd.chat.service.handlers.request.RequestHandler;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import java.io.IOException;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class CometDServlet extends HttpServlet {
    
    private static final Logger LOG = LoggerFactory.getLogger(CometDServlet.class);

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        
        final RequestHandlerFactory reqHandlerFactory = this.getBean(req, RequestHandlerFactory.class);
        
        final RequestHandler<Response> reqHandler = reqHandlerFactory.getRequestHandler(req, res);

        LOG.debug("{}", reqHandler);
        
        final JsonResponseHandler resHandler = this.getBean(req, JsonResponseHandler.class);
        
        Response data = null;
        try{
            
            data = reqHandler.process(req, res);
            
            resHandler.onSuccess(req, res, data);
            
        }catch(RuntimeException e0) {
            
            final String msg = "Exception processing request";
            
            LOG.warn(msg, e0);
            
            final ResponseBuilder erp = this.getBean(req, ResponseBuilder.class);
            
            data = erp.buildErrorResponse(msg, e0);
            
            resHandler.onFailure(req, res, data);
            
        }finally{
        
            LOG.debug("{}", data);

            resHandler.onAlways(req, res, data);
        }
    }

    private <T extends Object> T getBean(ServletRequest req, Class<T> type) throws BeansException {
        return this.getBean(req.getServletContext(), type);
    }
    
    private <T extends Object> T getBean(ServletContext servletContext, Class<T> type) throws BeansException {
        return this.getWebApplicationContext(servletContext).getBean(type);
    }

    private WebApplicationContext getWebApplicationContext(ServletRequest req) {
        return this.getWebApplicationContext(req.getServletContext());
    }
    
    private WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext);
    }
}
