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

import org.cometd.bayeux.client.ClientSessionChannel;

/**
 * @author USER
 */
public interface Chat {
    
    /** The chat message content */
    String CHAT = "chat"; 
    
    /** The sender of the chat message */
    String USER = "user";
    
    /** The recipient of the chat message */
    String PEER = "peer";
    
    /** The chat room through which the message is being sent */
    String ROOM = "room";

    /** scope of the chat, may be private etc */
    String SCOPE = "scope";
    
    String MEMBERS_SERVICE_CHANNEL = ClientSessionChannel.SERVICE+"/members";

    String WEBSOCKET_ENABLED = "websocketEnabled";
    
    String LOG_LEVEL = "logLevel";
    
    interface LOG_LEVEL_VALUES{
        String WARN = "warn";
        String INFO = "info";
        String DEBUG = "debug";
    }
    String LOG_LEVEL_WARN = "warn";
    String LOG_LEVEL_INFO = "info";
    String LOG_LEVEL_DEBUG = "debug";
}
