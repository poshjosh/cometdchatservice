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

/**
 * @author USER
 */
public interface ParamNames{
    
    String REQUEST = "req";
    
    /** The chat message content */
    String CHAT = Chat.CHAT; 
    
    /** The sender of the chat message */
    String USER = Chat.USER;
    
    /** The recipient of the chat message */
    String PEER = Chat.PEER;
    
    /** The chat room through which the message is being sent */
    String ROOM = Chat.ROOM;

    /** scope of the chat, may be private etc */
    String SCOPE = Chat.SCOPE;
    
    String ASYNC = "async";
    
    String TIMEOUT = "timeout";
    
    String DELAY = "delay";
}
