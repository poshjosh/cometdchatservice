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
import java.util.Objects;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.server.authorizer.GrantAuthorizer;

/**
 * @author USER
 */
public final class CreateDefaultChannelsIfAbsent implements ChatServerInitAction<String>{

    public CreateDefaultChannelsIfAbsent() { }
    
    @Override
    public BayeuxServer apply(BayeuxServer bayeux, List<String> channels) {
        
        Objects.requireNonNull(bayeux);
        Objects.requireNonNull(channels);
        
        // Deny unless granted
        bayeux.createChannelIfAbsent("/**", (ServerChannel.Initializer)channel -> channel.addAuthorizer(GrantAuthorizer.GRANT_NONE));

        // Allow anybody to handshake
        bayeux.getChannel(ServerChannel.META_HANDSHAKE).addAuthorizer(GrantAuthorizer.GRANT_PUBLISH);

        for(String channel : channels) {
            bayeux.createChannelIfAbsent(channel, new ConfigurableServerChannel.Initializer.Persistent());
        }
        
        return bayeux;
    }
}
