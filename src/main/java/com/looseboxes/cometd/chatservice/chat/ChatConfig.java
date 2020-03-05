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
package com.looseboxes.cometd.chatservice.chat;

/**
 * @author USER
 */
public interface ChatConfig {
    
    public static ChatConfig.Builder builder() {
        return new ChatConfigBuilderImpl();
    }
    
    public interface Builder {

        ChatConfig build();

        ChatConfig.Builder channel(String channel);

        ChatConfig.Builder logLevel(String logLevel);

        ChatConfig.Builder room(String room);

        ChatConfig.Builder user(String user);

        /**
         * If you enable web socket then there may be no HttpSession/HttpRequest. Hence,
         * calls to {@link org.cometd.bayeux.server.BayeuxContext#setHttpSessionAttribute(java.lang.String, java.lang.Object) BayeuxContext#setHttpSessionAttribute}
         * or {@link org.cometd.bayeux.server.BayeuxContext#getHttpSessionAttribute(java.lang.String) BayeuxContext#getHttpSessionAttribute}
         * amongst other methods accessing the HttpSession/HttpRequest will throw an Exception
         * @param websocketEnabled if true web socket will be enabled, otherwise it will be disabled
         * @return the calling instance
         */
        ChatConfig.Builder websocketEnabled(boolean websocketEnabled);
    }

    ChatConfig forUser(String user);

    String getChannel();

    String getRoom();

    String getUser();

    String getLogLevel();

    boolean isWebsocketEnabled();
}
