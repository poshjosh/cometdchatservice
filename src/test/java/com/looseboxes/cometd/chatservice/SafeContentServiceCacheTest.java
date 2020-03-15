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

import com.looseboxes.cometd.chatservice.test.CacheEvicter;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import com.looseboxes.cometd.chatservice.test.TestConfigurationForInMemoryCache;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.isNotNull;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;

/**
 * @author USER
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    MyTestConfiguration.class, TestConfigurationForInMemoryCache.class})
public class SafeContentServiceCacheTest {

    @Mock private SafeContentService safeContentService;

    @Autowired private CacheEvicter cacheEvicter;
    
    @Autowired private CacheManager cacheManager;
    
    public SafeContentServiceCacheTest() { }
    
    @BeforeEach
    public void evictAllCaches() {
        cacheEvicter.evictAllCaches();
    }
    
    @Test
    @Disabled("@TODO Failing, why? see https://stackoverflow.com/questions/24221569/how-to-test-springs-declarative-caching-support-on-spring-data-repositories")
    public void flag_whenCalled_shouldCacheResult() {

        //@TODO test null return value from cache
        
        final String arg0 = "arg_0";
        final String expResult0 = "adult,racy";
        final String arg1 = "arg_1";
        final String expResult1 = "";
        
        final SafeContentService svc = this.getSafeContentService();

        // Set up the mock to return *different* objects for the first and second call
        Mockito.when(svc.flag(Mockito.any(String.class))).thenReturn(expResult0, expResult1);

        // First invocation returns object returned by the method
        Object result = svc.flag(arg0);
        assertThat(result, is(expResult0));

        // Second invocation should return cached value, *not* second (as set up above)
        result = svc.flag(arg0);
        assertThat(result, is(expResult0));

        // Verify repository method was invoked once
        Mockito.verify(svc, Mockito.times(1)).flag(arg0);
        assertThat(cacheManager.getCache(CacheNames.CONTENT_FLAG_CACHE).get(arg0), isNotNull());

        // Third invocation with different key is triggers the second invocation of the repo method
        result = svc.flag(arg1);
        assertThat(result, is(expResult1));
    }

    public SafeContentService getSafeContentService() {
        return safeContentService;
    }
}
