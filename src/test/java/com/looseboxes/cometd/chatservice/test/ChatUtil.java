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
package com.looseboxes.cometd.chatservice.test;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.server.ServerMessage;
import org.cometd.server.ServerMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USER
 */
public class ChatUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(ChatUtil.class);
    
    public Message.Mutable sendSuccessMessage(String clientId, ClientSession.MessageListener ml) { 
        return sendSuccessMessage(clientId, null, ml);
    }

    public Message.Mutable sendSuccessMessage(String clientId, Object data, ClientSession.MessageListener ml) { 
        LOG.trace("Sending success message");
        final Message.Mutable message = this.createSuccessMessage(clientId, data);
        ml.onMessage(message);
        LOG.trace("Done sending message: {}", message);
        return message;
    }
    
    public ServerMessage.Mutable createSuccessMessage(String clientId, Object data) {
        return this.createMessage(true, clientId, data);
    }
    
    public ServerMessage.Mutable createSuccessMessage(String clientId, String key, Object value) {
        return this.createMessage(true, clientId, key, value);
    }

    public ServerMessage.Mutable createMessage(boolean success, String clientId, Object data) {
        return createMessage(success, clientId, "data", data);
    }
    
    public ServerMessage.Mutable createMessage(boolean success, 
            String clientId, String key, Object value) {
        final ServerMessage.Mutable msg = new ServerMessageImpl();
        msg.put("id", Long.toHexString(System.currentTimeMillis()));
        msg.put("clientId", clientId);
        msg.put("meta", true);
        msg.put("successful", success);
        if(value != null) {
            msg.put(key, value);
        }
        msg.put("empty", value == null);
        msg.put("publishReply", false);
        return msg;
    }
}
