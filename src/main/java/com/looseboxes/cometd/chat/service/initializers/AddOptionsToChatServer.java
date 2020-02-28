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

import com.looseboxes.cometd.chat.service.ChatServerOptionNames;
import java.util.List;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * @author USER
 */
public final class AddOptionsToChatServer implements ChatServerInitAction<Object>{

    public AddOptionsToChatServer() { }
    
    @Override
    public BayeuxServer apply(BayeuxServer bayeux, List options) {
        options.stream().forEach((option) -> {
            bayeux.setOption(ChatServerOptionNames.from(option), option);
        });
        return bayeux;
    }
}
