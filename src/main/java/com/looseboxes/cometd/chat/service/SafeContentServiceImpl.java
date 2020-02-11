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

import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author USER
 */
public class SafeContentServiceImpl implements SafeContentService {

    private static final Logger LOG = LoggerFactory.getLogger(SafeContentServiceImpl.class);

    private final RestTemplate restTemplate; 

    private final String serviceUrl;

    public SafeContentServiceImpl(RestTemplate restTemplate, String serviceUrl) {
        this.restTemplate = Objects.requireNonNull(restTemplate);
        this.serviceUrl = serviceUrl.startsWith("http") ?
               serviceUrl : "http://" + serviceUrl;
    }
    
    @Cacheable(value="safeContentCache", sync=true)
    @Override
    public boolean isSafe(String text) {
        
        if(text == null || text.isEmpty()) {
            return true;
        }
        
        //@TODO add caching

        //@TODO make this a property
        final String endpoint = "/issafe";
        final String url = serviceUrl + endpoint;
        
        LOG.debug("URL: {}", url);
        
        //@TODO make this number literal a property
        final HttpEntity<Map> res = this.get(url, text, 10_000);
        
        final Map body = res == null ? null : res.getBody();
        
        LOG.debug("URL: {}, response: {}", url, body);
        
        final Object value = body == null ? null : body.get(endpoint);

        final String sval = value == null ? null : value.toString();
        
        return sval == null || sval.isEmpty() ? false : Boolean.parseBoolean(sval);
    }
    
    private HttpEntity<Map> get(String url, String text, long timeout){
        
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        //@TODO make these properties
        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("text", text)
                .queryParam("timeout", timeout);

        final HttpEntity<?> entity = new HttpEntity<>(headers);

        url = builder.toUriString();
        LOG.debug("URL: {}", url);
        
        return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);    
    }
}
