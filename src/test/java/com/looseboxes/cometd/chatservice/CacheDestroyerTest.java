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

import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author USER
 */
public class CacheDestroyerTest {
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    private Map<String, Cache> caches;
    
    @BeforeEach
    public void initCaches() {
        caches = this.getCaches();
    }

    @Test
    public void destroy_whenValidArg_shouldReturnNamesOfDestroyedCaches() {
        System.out.println("destroy_whenValidArg_shouldReturnNamesOfDestroyedCaches");

        final Set<String> expCacheNames = caches.keySet();
        
        final CacheManager cacheManager = whenMethodDestroyIsCalledWithValidArgs();

        final Collection<String> destroyed = thenReturnDestroyedCacheNames(cacheManager);
        
        for(String cacheName : expCacheNames) {
            verify(caches.get(cacheName), times(1)).clear();
            verify(caches.get(cacheName), times(1)).invalidate();
        }
        
        verify(cacheManager, times(1)).getCacheNames();
        verify(cacheManager, times(caches.keySet().size()))
                .getCache(Mockito.isA(String.class));
     
        assertThat(destroyed, is(expCacheNames));
    }
    
    public CacheManager whenMethodDestroyIsCalledWithValidArgs() {
        
        final Set<String> expCacheNames = caches.keySet();

        final CacheManager cacheManager = mock(CacheManager.class);
        when(cacheManager.getCacheNames()).thenReturn(expCacheNames);
        when(cacheManager.getCache(Mockito.isA(String.class))).thenAnswer(new Answer<Cache>(){
            @Override
            public Cache answer(InvocationOnMock iom) throws Throwable {
                return caches.get(iom.getArgument(0, String.class));
            }
        });
        
        return cacheManager;
    }
    
    public Collection<String> thenReturnDestroyedCacheNames(CacheManager cacheManager) {
        
        final CacheDestroyer cacheDestroyer = getCacheDestroyer();
        
        final Collection<String> destroyed = cacheDestroyer.destroy(cacheManager);
        
        return destroyed;
    }

    @Test
    public void destroy_whenNullArg_shouldThrowRuntimeException() {
        System.out.println("destroy_whenNullArg_shouldThrowRuntimeException");
        
        final CacheManager cacheManager = null;
        
        final CacheDestroyer cacheDestroyer = getCacheDestroyer();
        
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> cacheDestroyer.destroy(cacheManager));
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }
    
    public Map<String, Cache> getCaches() {
        final Map<String, Cache> result = new HashMap<>();
        for(String cacheName : CacheNames.all()) {
            final Cache cache = mock(Cache.class);
            doNothing().when(cache).clear();
            when(cache.invalidate()).thenReturn(true);
            result.put(cacheName, cache);
        }
        return result;
    }
    
    public CacheDestroyer getCacheDestroyer() {
        return new CacheDestroyerImpl();
    }
}
