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

import com.looseboxes.cometd.chat.service.CometDProperties;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import com.looseboxes.cometd.chat.service.test.TestConfig;
import com.looseboxes.cometd.chat.service.test.TestEndpointRequests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Web MVC test which starts the spring application context without the web
 * server, thus narrowing the tests to only the web layer.
 * Uses @ExtendsWith(SpringExtention.class) which is JUnit5 construct for 
 * JUnit4 @RunWith(SpringRunner.class)
 * <p>
 * We provide mock objects to the spring application context by using the 
 * @MockBean annotation. @MockBean automatically replaces the bean of the same 
 * type in the application context with a Mockito mock.
 * </p>
 * @author USER
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ShutdownController.class)
@Import(TestConfig.class)
public class ShutdownControllerMockIT {
    
    public ShutdownControllerMockIT() { }
    
    @Autowired private TestEndpointRequests testEndpoints;
    
    @Autowired private MockMvc mockMvc;
    
    /** Required by called method(s) */
    @MockBean private CometDProperties cometDProperties;
    
    /** Required by the Controller being tested */
    @MockBean private ResponseBuilder resBuilder;

    @Test
    public void shutdown_ShouldReturnSuccessfully() throws Exception{
        System.out.println("shutdown_ShouldReturnSuccessfully");
        
        this.mockMvc.perform(testEndpoints.shutdown())
                .andDo(print())
                .andExpect(status().isOk());

        verify(resBuilder, times(1)).buildSuccessResponse();
    }
}
