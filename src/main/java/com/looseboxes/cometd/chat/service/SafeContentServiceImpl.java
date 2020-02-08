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
package com.looseboxes.cometd.chat.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class SafeContentServiceImpl implements SafeContentService {

    private static Logger LOG = LoggerFactory.getLogger(SafeContentServiceImpl.class);
    
    public SafeContentServiceImpl() { }
    
    @Override
    public boolean isSafe(String text) {
        return !text.contains("dang");
    }
}
