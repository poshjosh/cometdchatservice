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

import java.util.Collections;
import java.util.List;

/**
 *
 * @author USER
 */
public interface CacheNames {
    
    String CONTENT_FLAG_CACHE = "cometdchatservice_contentFlagCache";
    
    static List<String> all(){
        return Collections.singletonList(CONTENT_FLAG_CACHE);
    }
    
    static String buildUniqueName(Object ref) {
        if(ref instanceof Class) {
            throw new IllegalArgumentException("Not an instance: " + ref);
        }
        return ref.getClass().getSimpleName() + "Cache" + Integer.toHexString(ref.hashCode());
    }
}
