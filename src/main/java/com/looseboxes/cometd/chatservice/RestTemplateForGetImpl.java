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
package com.looseboxes.cometd.chatservice;

import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author USER
 */
public class RestTemplateForGetImpl implements RestTemplateForGet {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestTemplateForGetImpl.class);
    
    private final RestTemplate restTemplate; 

    public RestTemplateForGetImpl(RestTemplate restTemplate) {
        this.restTemplate = Objects.requireNonNull(restTemplate);
    }
    
    @Override
    public <T> HttpEntity<T> get(String url, HttpHeaders headers, 
            Map<String, Object> params, Class<T> bodyType){
        try{
            
            final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            
            params.forEach((name, value) -> {
                builder.queryParam(name, value);
            });

            final HttpEntity<?> entity = new HttpEntity<>(headers);

            url = builder.toUriString();
            LOG.debug("URL: {}", url);

            return restTemplate.exchange(url, HttpMethod.GET, entity, bodyType);    
            
        }catch(RuntimeException e) {
            
            LOG.warn("Exception accessing service at: " + url, e);
        
            return (HttpEntity<T>)HttpEntity.EMPTY;
        }
    }
}
