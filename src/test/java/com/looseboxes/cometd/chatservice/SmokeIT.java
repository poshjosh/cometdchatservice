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

import com.looseboxes.cometd.chatservice.controllers.ChatController;
import com.looseboxes.cometd.chatservice.controllers.JoinController;
import com.looseboxes.cometd.chatservice.controllers.MembersController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

/**
 * @author USER
 */
@SpringBootTest
public class SmokeIT {

    @Autowired private JoinController joinController;
    @Autowired private ChatController chatController;
    @Autowired private MembersController membersController;

    @Test
    public void cometDApplication_WhenRun_ShouldLoadJoinController() throws Exception {
        System.out.println("cometDApplication_WhenRun_ShouldLoadJoinController");
        assertThat(joinController).isNotNull();
    }

    @Test
    public void cometDApplication_WhenRun_ShouldLoadChatController() throws Exception {
        System.out.println("cometDApplication_WhenRun_ShouldLoadChatController");
        assertThat(chatController).isNotNull();
    }

    @Test
    public void cometDApplication_WhenRun_ShouldLoadMembersController() throws Exception {
        System.out.println("cometDApplication_WhenRun_ShouldLoadMembersController");
        assertThat(membersController).isNotNull();
    }
}