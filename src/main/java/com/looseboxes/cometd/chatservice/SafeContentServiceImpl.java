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
    
    private final String endpoint;
    
    private final long timeout;

    public SafeContentServiceImpl(RestTemplate restTemplate, 
            String serviceUrl, String endpoint, long timeout) {
        this.restTemplate = Objects.requireNonNull(restTemplate);
        this.serviceUrl = serviceUrl.startsWith("http") ?
               serviceUrl : "http://" + serviceUrl;
        this.endpoint = Objects.requireNonNull(endpoint);
        this.timeout = timeout;
    }
    
    /**
     * This call may invoke third party services.
     * The result is cached using Spring's {@link org.springframework.cache.annotation.Cacheable @Cacheable} 
     * annotation. One limitation though, when using @Cacheable -> no synchronization.
     * <b>Reason: <code>sync</code> may not be used together with <code>unless</code></b>
     * @see https://github.com/spring-projects/spring-framework/issues/20956
     * @see https://docs.spring.io/spring-framework/docs/5.0.0.RELEASE/javadoc-api/org/springframework/cache/annotation/Cacheable.html#sync--
     * @param text The content to flag
     * @return The flags, if the content is flagged as unsafe. E.g of flags = 
     * <code>adult,violence,racy,graphic,medical,spoof</code>; empty text if the 
     * content is flagged as safe or <code>null</code> if the safety or otherwise
     * of the content could not be ascertained.
     */
    @Cacheable(value = "cometdchatservice_contentFlagCache", unless="#result == null")
    @Override
    public String flag(String text) {
        
        if(text == null || text.isEmpty()) {
            return null;
        }
        
        final String url = serviceUrl + endpoint;
        
        LOG.debug("URL: {}", url);
        
        final HttpEntity<Map> res = this.get(url, text, this.timeout);
        
        final Map body = res == null ? null : res.getBody();
        
        LOG.debug("URL: {}, response: {}", url, body);
        
        final Object value = body == null ? null : body.get(endpoint);

        final String flags = value == null ? null : value.toString();
        
        return flags;
    }

    private HttpEntity get(String url, String text, long timeout){
        try{
            
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
            
        }catch(RuntimeException e) {
            
            LOG.warn("Exception accessing service at: " + url, e);
        
            return HttpEntity.EMPTY;
        }
    }
}
