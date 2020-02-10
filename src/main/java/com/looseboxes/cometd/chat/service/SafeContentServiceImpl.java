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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.client.RestTemplate;

/**
 * @author USER
 */
public class SafeContentServiceImpl implements SafeContentService {

    private static final Logger LOG = LoggerFactory.getLogger(SafeContentServiceImpl.class);
    
    // Request the load-balanced template with Ribbon built-in
    @Autowired        
    @LoadBalanced     
    private RestTemplate restTemplate; 

    private final String serviceUrl;

    public SafeContentServiceImpl(String serviceUrl) {
        this.serviceUrl = serviceUrl.startsWith("http") ?
               serviceUrl : "http://" + serviceUrl;
    }
    
    @Override
    public boolean isSafe(String text) {
        
        if(text == null || text.isEmpty()) {
            return true;
        }
        
        //@TODO add caching

        //@TODO make these properties
        final String endpoint = "/issafe";
        final String textName = "text";
        final String timeoutName = "timeout";
        final Map<String, Object> params = new HashMap<>();
        params.put(textName, text);
        params.put(timeoutName, "10000");
        
        final Map response = restTemplate.getForObject(serviceUrl
                + endpoint, Map.class, Collections.singletonMap(textName, text));
        
        LOG.debug("{}", response);
        
        final Object value = response == null ? null : response.get(endpoint);
        
        return value == null ? false : Boolean.parseBoolean(value.toString());
    }
}
