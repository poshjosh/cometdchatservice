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

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public final class RequestUrlImpl implements RequestUrl {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUrlImpl.class);
    
    private final String baseUrl;
    private final String contextPath;
    private final String requestUrl;

    public RequestUrlImpl(HttpServletRequest request) {
        this.requestUrl = request.getRequestURL().toString();
        this.baseUrl = requestUrl.substring(0, 
                requestUrl.length() - request.getRequestURI().length());
        this.contextPath = request.getContextPath();
        LOG.debug("Context path: {}, Base URL: {}", this.contextPath, this.baseUrl);
    }
            
    /**
     * <code><pre>
     * String endpointUrl = getContextUrl() + endpoint;
     * </pre></code>
     * @param endpoint
     * @see #getContextUrl(javax.servlet.http.HttpServletRequest) 
     * @return The URL to the endpoint
     */
    @Override
    public String getEndpointUrl(String endpoint) {
       
        final String result = this.getContextUrl() + endpoint;
        
        LOG.debug("Endpoint: {}, URL: {}", endpoint, result);
        
        return result;
    }

    /**
     * <code>String contextURL = baseURL + contextPath;</code>
     * @return 
     */
    @Override
    public String getContextUrl() {
        return contextPath;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }
}
