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
package com.looseboxes.cometd.chat.service.test;

import com.looseboxes.cometd.chat.service.controllers.ChatController;
import com.looseboxes.cometd.chat.service.controllers.JoinController;
import com.looseboxes.cometd.chat.service.controllers.MembersController;
import com.looseboxes.cometd.chat.service.controllers.ShutdownController;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author USER
 */
@SpringBootTest
public class SmokeTest {

    @Autowired private JoinController joinController;
    @Autowired private ChatController chatController;
    @Autowired private MembersController membersController;
    @Autowired private ShutdownController shutdownController;

    @Test
    public void cometDApplication_whenRun_joinControllerLoads() throws Exception {
        assertThat(joinController).isNotNull();
    }

    @Test
    public void cometDApplication_whenRun_chatControllerLoads() throws Exception {
        assertThat(chatController).isNotNull();
    }

    @Test
    public void cometDApplication_whenRun_membersControllerLoads() throws Exception {
        assertThat(membersController).isNotNull();
    }

    @Test
    public void cometDApplication_whenRun_shutdownControllerLoads() throws Exception {
        assertThat(shutdownController).isNotNull();
    }
}