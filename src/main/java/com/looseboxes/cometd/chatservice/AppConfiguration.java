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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;

/**
 * @author USER
 */
@Configuration
public class AppConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);
    
    public AppConfiguration(){}
    
    @Bean CacheDestroyer cacheDestroyer() {
        return new CacheDestroyerImpl(this.cacheManager());
    }
    
    @Bean
    public CacheManager cacheManager() {
        final CacheManager cacheManager = 
                new EhCacheCacheManager(ehCacheCacheManager().getObject());
        LOG.debug("Created EhCache type: {}", cacheManager);
        return cacheManager;
    }
 
    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        
        final EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
        
        factory.setConfigLocation(new ClassPathResource("ehcache.xml"));
        
        // @NOTE Setting shared to false (the default) led to complaint that
        // the CacheManager already exists, in test cases. However setting shared
        // to true can result to inconsistencies. 
        //
        // This is the approache we choose. Each CacheManager is unique this way
        factory.setCacheManagerName(CacheNames.cacheManagerName(this));
//        factory.setShared(true);
        
        return factory;
    }

    @Bean @Scope("prototype") public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean public TerminateBean terminateBean() {
        return new TerminateBean();
    }
}
