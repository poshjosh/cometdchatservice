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

/**
 * @author USER
 */
public interface RequestUrl {

    String getBaseUrl();
    
    String getContextPath();

    /**
     * <code>String contextUrl = getBaseUrl() + getContextPath();</code>
     * @return The URL to the context path
     * @see #getBaseUrl() 
     * @see #getContextPath() 
     */
    default String getContextUrl() {
        return getBaseUrl() + getContextPath();
    }

    /**
     * <code>String endpointUrl = getContextUrl() + endpoint;</code>
     * @param endpoint
     * @return The URL to the endpoint
     * @see #getContextUrl() 
     */
    default String getEndpointUrl(String endpoint) {
        
        return getContextUrl() + endpoint;
    }
    
    String getRequestUrl();
}
