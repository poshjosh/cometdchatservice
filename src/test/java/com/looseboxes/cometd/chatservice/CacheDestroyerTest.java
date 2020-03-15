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
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author USER
 */
public class CacheDestroyerTest {
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    private Map<String, Cache> caches;
    
    @BeforeEach
    public void beforeEach() {
        caches = this.initCaches();
    }

    @Test
    public void destroy_whenCallingInstanceInitializedWithNullArg_shouldReturnEmptyResult() {
        System.out.println("destroy_whenCallingInstanceInitializedWithNullArg_shouldReturnEmptyResult");

        final Collection<String> destroyed = 
                destroyNoArgs_whenCallingInstanceInitializedWithArg(null);
        
        assertThat(destroyed.size(), is(0));
        
    }
    
    @Test
    public void destroy_whenValidArg_shouldReturnNamesOfDestroyedCaches() {
        System.out.println("destroy_whenValidArg_shouldReturnNamesOfDestroyedCaches");

        final CacheManager cacheManager = whenMethodDestroyIsCalledWithValidArgs();

        final Collection<String> destroyed = thenReturnDestroyedCacheNames(cacheManager);
        
        this.verifyMethodCalls(cacheManager, 1, expectedCacheCount());
     
        assertThat(new TreeSet(destroyed), is(new TreeSet(expectedCacheNames())));
    }
    
    @Test
    public void destroy_whenNullArg_shouldThrowRuntimeException() {
        System.out.println("destroy_whenNullArg_shouldThrowRuntimeException");
        
        final CacheManager cacheManager = null;
        
        final CacheDestroyer cacheDestroyer = getCacheDestroyer();
        
        final RuntimeException thrown = Assertions.assertThrows(
                RuntimeException.class, 
                () -> cacheDestroyer.destroyCaches(cacheManager));
        
        if(logStackTrace) {
            thrown.printStackTrace();
        }
    }
    
    public void verifyMethodCalls(CacheManager cacheManager, int single, int total) {

        final Set<String> expCacheNames = this.expectedCacheNames();
        
        for(String cacheName : expCacheNames) {
            verify(caches.get(cacheName), times(single)).clear();
            verify(caches.get(cacheName), times(single)).invalidate();
        }
        
        verify(cacheManager, times(single)).getCacheNames();
        verify(cacheManager, times(total))
                .getCache(Mockito.isA(String.class));
    }
    
    public CacheManager whenMethodDestroyIsCalledWithValidArgs() {
        
        final Set<String> expCacheNames = this.expectedCacheNames();

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
    
    public int expectedCacheCount() {
        return caches.size();
    }
    
    public Set<String> expectedCacheNames() {
        return caches.keySet();
    }
    
    public Collection<String> destroyNoArgs_whenCallingInstanceInitializedWithArg(
            CacheManager cacheManager) {
        
        final CacheDestroyer cacheDestroyer = getCacheDestroyer(cacheManager);
        
        final Collection<String> destroyed = cacheDestroyer.destroyCaches();
        
        return destroyed;
    }
   
    public Collection<String> thenReturnDestroyedCacheNames(CacheManager cacheManager) {
    
        return this.thenReturnDestroyedCacheNames(cacheManager, cacheManager);
    }
    
    public Collection<String> thenReturnDestroyedCacheNames(CacheManager init, CacheManager arg) {
        
        final CacheDestroyer cacheDestroyer = getCacheDestroyer(init);
        
        final Collection<String> destroyed = cacheDestroyer.destroyCaches(arg);
        
        return destroyed;
    }

    public Map<String, Cache> initCaches() {
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
        return this.getCacheDestroyer(null);
    }
    
    public CacheDestroyer getCacheDestroyer(CacheManager cacheManager) {
        return new CacheDestroyerImpl(cacheManager);
    }
}
