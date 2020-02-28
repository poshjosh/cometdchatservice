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

import java.util.Objects;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.filter.DataFilter;
import org.cometd.server.filter.JSONDataFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class BadWordFilter extends JSONDataFilter {

    private static final Logger LOG = LoggerFactory.getLogger(BadWordFilter.class);
    
    private final SafeContentService safeContentService;

    public BadWordFilter(SafeContentService safeContentService) {
        this.safeContentService = Objects.requireNonNull(safeContentService);
    }

    @Override
    protected Object filterString(ServerSession session, ServerChannel channel, String string) {

        if(string == null || string.isEmpty()) {
            return string;
        }

        if(LOG.isTraceEnabled()) {
            LOG.trace("Text to flag: {}", string);
        }

        final String flags = safeContentService.flag(string);

        final boolean safe = flags == null || flags.isEmpty();

        if ( ! safe) {
            throw new DataFilter.AbortException();
        }

        return string;
    }
}
