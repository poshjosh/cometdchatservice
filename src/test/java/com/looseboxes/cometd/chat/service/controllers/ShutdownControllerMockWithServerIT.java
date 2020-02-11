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
package com.looseboxes.cometd.chat.service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import com.looseboxes.cometd.chat.service.test.Mocker;
import com.looseboxes.cometd.chat.service.test.EndpointRequestBuilders;
import com.looseboxes.cometd.chat.service.test.MyTestConfiguration;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
/**
 * The web server is started in this test case. 
 * @author USER
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(MyTestConfiguration.class)
public class ShutdownControllerMockWithServerIT {

    @Autowired private EndpointRequestBuilders endpointReqBuilders;
    
    @Autowired private Mocker mocker;

    @Autowired private MockMvc mockMvc;
    
    @MockBean private ResponseBuilder mockResBuilder;
    
    @Autowired private ObjectMapper mapper;

    @Test
    public void whenRequesShutdown_shouldReturnSuccessfully() throws Exception {
        System.out.println("whenRequesShutdown_shouldReturnSuccessfully");
        
        mockResBuilder = mocker.mock(mockResBuilder);
        
        int invocations = 0;
        
        final String expectedJson = mapper.writeValueAsString(mockResBuilder.buildSuccessResponse());
        ++invocations;
        
        this.mockMvc.perform(endpointReqBuilders.builder(Endpoints.SHUTDOWN))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("success", true))
                .andExpect(jsonPath("$.success", is(true)));
                // Timestamp is different
//                .andExpect(content().json(expectedJson, false));
        ++invocations;

        verify(mockResBuilder, times(invocations)).buildSuccessResponse();
    }
}