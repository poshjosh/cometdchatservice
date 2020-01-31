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

import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * The web server is started in this case. To Test without the web server,
 * we use {@link com.looseboxes.cometd.chat.service.WebLayerTest WebLayerTest}
 * @author USER
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WebAppTest {

    @Autowired private MockMvc mockMvc;

    @Test
    public void shouldReturnSuccessfully() throws Exception {
        System.out.println("shouldReturnSuccessfully");
        this.mockMvc.perform(get(Endpoints.SHUTDOWN)).andDo(print()).andExpect(status().isOk())
                        .andExpect(content().string(containsString("\"success\":true")));
    }
}