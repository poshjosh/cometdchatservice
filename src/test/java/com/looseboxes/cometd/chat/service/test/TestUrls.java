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
package com.looseboxes.cometd.chat.service.test;

import java.util.Map;
import java.util.Objects;

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
    
    public String getEndPointUrl(int port, String endpoint) {
        final StringBuilder builder = new StringBuilder(getContextUrl(port));
        builder.append(endpoint);
        final Map<String, String> params = endpointReqParams.forEndpoint(endpoint);
        if( ! params.isEmpty()) {
            builder.append('?');
            params.forEach((k, v) -> {
                builder.append(k).append('=').append(v);
            });
        }
        return builder.toString();
    }

    public String getContextUrl(int port) {
        return getBaseUrl(port) + Objects.requireNonNull(contextPath);
    }
    
    public String getBaseUrl(int port) {
        return "http://localhost:" + port;
    }
}
