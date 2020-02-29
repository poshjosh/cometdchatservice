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
package com.looseboxes.cometd.chatservice.initializers;

import java.lang.reflect.Method;
import java.util.List;
import org.cometd.bayeux.server.BayeuxServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public final class DumpChatServerState implements ChatServerInitAction<Object>{

    private static final Logger LOG = LoggerFactory.getLogger(DumpChatServerState.class);
    
    @Override
    public BayeuxServer apply(BayeuxServer bayeux, List args) {
        try{
            final Method dumpMethod = bayeux.getClass().getMethod("dump");
            final String dump = (String)dumpMethod.invoke(bayeux);
            LOG.info(dump);
        }catch(Exception e) {
            LOG.warn("Unable to dump BayeuxServer state, reason: " + e);
        }
        return bayeux;
    }
}
