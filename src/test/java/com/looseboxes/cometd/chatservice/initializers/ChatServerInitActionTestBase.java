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

import com.looseboxes.cometd.chatservice.initializers.ChatServerInitAction;

/**
 * @author USER
 */
public class ChatServerInitActionTestBase<T> extends ChatServerInitActionMockTestBase<T>{
    
//    private static final Logger LOG = LoggerFactory.getLogger(ChatServerInitActionTestBase.class);

    public ChatServerInitActionTestBase(Context<T> context) {
        super(context);
    }

    @Override
    public ChatServerInitAction mockBayeuxInitAction(ChatServerInitAction bayeuxInitAction) { 
        return bayeuxInitAction;
    }
}
