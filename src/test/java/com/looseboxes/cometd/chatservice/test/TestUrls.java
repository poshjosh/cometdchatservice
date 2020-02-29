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
package com.looseboxes.cometd.chatservice.test;

import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author USER
 */
public class TestUrls{
    
    private final String contextPath;
    
    private final EndpointRequestParams endpointReqParams;

    public TestUrls(String contextPath, EndpointRequestParams endpointReqParams) {
        this.contextPath = Objects.requireNonNull(contextPath);
        this.endpointReqParams = Objects.requireNonNull(endpointReqParams);
    }
    
    public TestUrls withContextPath(String s) {
        return new TestUrls(s, this.endpointReqParams);
    }
    
    public String getEndpointUrlWithParams(int port, String endpoint) {
        final StringBuilder builder = new StringBuilder(this.getEndpointUrl(port, endpoint));
        final Map<String, String> params = endpointReqParams.forEndpoint(endpoint);
        if( ! params.isEmpty()) {
            builder.append('?');
            final AtomicInteger index = new AtomicInteger(0);
            params.forEach((k, v) -> {
                if(index.get() > 0) {
                    builder.append('&');
                }
                builder.append(k).append('=').append(v);
                index.incrementAndGet();
            });
        }
        final String url = builder.toString();
        return validate(url);
    }

    public String getChatUrl(int port) {
        return this.getEndpointUrl(port, Endpoints.COMETD);
    }
    
    public String getEndpointUrl(int port, String endpoint) {
        final String url = this.getContextUrl(port) + endpoint;
        return validate(url);
    }

    public String getContextUrl(int port) {
        final String url = getBaseUrl(port) + Objects.requireNonNull(contextPath);
        return validate(url);
    }
    
    private String validate(String url) {
        try{
            new URL(url);
        }catch(MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }
    
    public String getBaseUrl(int port) {
        return "http://localhost:" + port;
    }
}
