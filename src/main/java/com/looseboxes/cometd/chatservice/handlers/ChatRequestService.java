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
package com.looseboxes.cometd.chatservice.handlers;

import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.handlers.response.Response;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author USER
 */
public interface ChatRequestService {

    ChatSession getChatSession(HttpServletRequest req, boolean createIfNone);

    boolean isJoinedToChat(HttpServletRequest req);

    /**
     * @param req
     * @param res
     * @return {@link com.looseboxes.cometd.chatservice.handlers.response.Response Response}
     * object with success set to true if previously joined to chat or
     * successfully joined to chat during this methods execution, otherwise
     * return false.
     */
    Response joinChatIfNotAlready(HttpServletRequest req, HttpServletResponse res);
}
