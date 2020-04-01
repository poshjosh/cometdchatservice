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

import com.looseboxes.cometd.chatservice.CacheDestroyer;
import com.looseboxes.cometd.chatservice.CacheDestroyerImpl;
import com.looseboxes.cometd.chatservice.CacheNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author USER
 */
@TestConfiguration
@EnableCaching
public class TestConfigurationForInMemoryCache {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(TestConfigurationForInMemoryCache.class);
    
    private CacheManager cacheManager; 

    @Bean @Primary public CacheManager cacheManager() {
        cacheManager = new ConcurrentMapCacheManager(
                CacheNames.all().toArray(new String[0]));
        LOG.debug("Created ConcurrentMap type: {}", cacheManager);
        return cacheManager;
    }
    
    @Bean public CacheDestroyer cacheDestroyer(){
        return new CacheDestroyerImpl(cacheManager());
    }
}
