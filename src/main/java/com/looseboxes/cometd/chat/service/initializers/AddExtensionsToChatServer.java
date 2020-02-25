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
package com.looseboxes.cometd.chat.service.initializers;

import java.util.List;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.BayeuxServer.Extension;

/**
 * @author USER
 */
public final class AddExtensionsToChatServer implements ChatServerInitAction<Extension>{
    
    public AddExtensionsToChatServer() { }
    
    @Override
    public BayeuxServer apply(BayeuxServer bayeux, List<Extension> extensions) {
        for(Extension ext : extensions) {
            bayeux.addExtension(ext);
        }
        return bayeux;
    }
}
