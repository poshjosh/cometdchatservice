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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
@TestConfiguration
public class MyTestConfiguration extends TestConfig{
    
    private static final Logger LOG = LoggerFactory.getLogger(MyTestConfiguration.class);
    
    public MyTestConfiguration(@Value("${server.servlet.context-path:}") String contextPath) { 
        super(contextPath);
    }
    
    @Bean
    public CacheManager cacheManager() {
        final CacheManager cacheManager = new NoOpCacheManager();
        LOG.debug("Created NoOp type: {}", cacheManager);
        return cacheManager;
    }
}
/**
 * 

    @Autowired private AppConfiguration appConfig;
    @Autowired private InitConfiguration initConfig;
    @Autowired private RequestConfiguration requestConfig;
    @Autowired private ResponseConfiguration responseConfig;
    @Autowired private ChatConfiguration chatConfig;
    
    @Override
    public AppConfiguration appConfig() {
        return appConfig;
    }

    @Override
    public InitConfiguration initConfig() {
        return initConfig;
    }

    @Override
    public RequestConfiguration requestConfig() {
        return requestConfig;
    }

    @Override
    public ResponseConfiguration responseConfig() {
        return responseConfig;
    }

    @Override
    public ChatConfiguration chatConfig() {
        return chatConfig;
    }
 * 
 */