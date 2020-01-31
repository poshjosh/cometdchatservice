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
import com.looseboxes.cometd.chat.service.requesthandlers.exceptions.RequestHandlerNotFoundException;
import com.looseboxes.cometd.chat.service.ParamNames;
import com.looseboxes.cometd.chat.service.PropertyNames;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public final class RequestHandlerFactoryImpl implements RequestHandlerFactory{
    
    private static final Logger LOG = LoggerFactory.getLogger(RequestHandlerFactoryImpl.class);
    
    public RequestHandlerFactoryImpl() { }

    @Override
    public RequestHandler getRequestHandler(ServletRequest req, ServletResponse res) {
        
        if(req instanceof HttpServletRequest) {
        
            final HttpServletRequest httpReq = (HttpServletRequest)req;
            
            final RequestHandler reqHandler = this.getRequestHandler(httpReq);
            
            return reqHandler;
            
        }else{
            
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private RequestHandler getRequestHandler(final HttpServletRequest httpReq) {
        
        final String reqHandlerTypeName = this.getRequestHandlerTypeName(httpReq);

        final RequestHandler reqHandler = this.getRequestHandler(httpReq.getServletContext(), reqHandlerTypeName);

        return reqHandler;
    }
    
    private RequestHandler getRequestHandler(ServletContext servletContext, final String reqHandlerTypeName) {
        
        RequestHandler reqHandler;

        if(reqHandlerTypeName == null || reqHandlerTypeName.isEmpty()) {

            throw new RequestHandlerNotFoundException();
            
        }else{

            try{

                final Class reqHandlerType = Class.forName(reqHandlerTypeName);

                final WebApplicationContext webAppCtx = WebApplicationContextUtils
                        .getRequiredWebApplicationContext(servletContext);

                if(webAppCtx == null) {
                    
                    throw new ProcessingRequestException("WebApplicationContextUtils.getWebApplicationContext(ServletContext) returned NULL");

                }else{
                    
                    reqHandler = this.getRequestHandler(webAppCtx, reqHandlerType);
                }
            }catch(ClassNotFoundException e) {

                throw new ProcessingRequestException("Exception loading class named: " + reqHandlerTypeName, e);
            }
        }

        return reqHandler;
    }

    private RequestHandler getRequestHandler(final WebApplicationContext webAppCtx, final Class reqHandlerType) {
        
        RequestHandler reqHandler;
        
        try{

            reqHandler = (RequestHandler)webAppCtx.getBean(reqHandlerType);

        }catch(BeansException e) {

            LOG.warn("Exception retrieving bean of type: " + reqHandlerType, e);

            reqHandler = this.createRequestHandler(reqHandlerType);

        }catch(ClassCastException e) {

            throw new ProcessingRequestException("Exception casting type: " + 
                    reqHandlerType + " to type: " + RequestHandler.class, e);
        }

        return reqHandler;
    }

    private String getRequestHandlerTypeName(final HttpServletRequest httpReq) {
        
        final String servletPath = httpReq.getServletPath();

        final WebApplicationContext webAppCtx = this.getWebAppCtx(httpReq.getServletContext());
        
        final String reqHandlerName = webAppCtx.getBean(ServletUtil.class).requireNonNullOrEmpty(httpReq, ParamNames.REQUEST);

        final String reqHandlerTypeName = reqHandlerName == null || reqHandlerName.isEmpty() ? null : 
                webAppCtx.getBean(Environment.class).getProperty(PropertyNames.Prefixes.REQUEST_HANDLER_TYPE + '.' + reqHandlerName);
        
        if(reqHandlerTypeName == null || reqHandlerTypeName.isEmpty()) {

            LOG.debug("Request handler not found: {} -> {} -> {}", 
                    servletPath, reqHandlerName, reqHandlerTypeName);
        }    
        
        return reqHandlerTypeName;
    }    

    private RequestHandler createRequestHandler(final Class type) {
        try{

            return (RequestHandler)type.getConstructors()[0].newInstance();

        }catch(SecurityException | InstantiationException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException | ClassCastException e) {

            throw new ProcessingRequestException("Exception creating instance of: " + type, e);
        }
    }
    
    private WebApplicationContext getWebAppCtx(ServletContext servletContext) {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }
}
