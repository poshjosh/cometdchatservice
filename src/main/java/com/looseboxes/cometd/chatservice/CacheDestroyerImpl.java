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

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * @author USER
 */
public class CacheDestroyerImpl implements CacheDestroyer{

    private static final Logger LOG = LoggerFactory.getLogger(CacheDestroyerImpl.class);
    
    private final CacheManager cacheManager;

    public CacheDestroyerImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Collection<String> destroy() {
        return cacheManager == null ? 
                Collections.EMPTY_LIST : this.destroy(cacheManager);
    }

    @Override
    public Collection<String> destroy(CacheManager cacheManager) {
        final Collection<String> cacheNames = cacheManager.getCacheNames();
        final Collection<String> done = new TreeSet<>();
        try{
            for(String cacheName : cacheNames) {
                final Cache cache  = cacheManager.getCache(cacheName);
                cache.clear();
                cache.invalidate();
                done.add(cacheName);
            }
        }catch(Exception e) {
            LOG.warn("Encountered exception while clearing caches.", e);
        }
        final Collection<String> failed = new TreeSet<>(cacheNames);
        failed.removeAll(done);
        LOG.info("Cleared and invalidated {}/{} caches: {}\nSuccess: {}, Failed: {}", 
                done.size(), cacheNames.size(), done, failed);
        return done;
    }
}
