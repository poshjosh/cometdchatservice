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

import com.looseboxes.cometd.chat.service.requesthandlers.ResponseImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;

/**
 * @author USER
 */
@Import(MyTestConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    
    @Autowired private TestUrl testUrl;

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Test
//    @Ignore // Junit4 construct
    @Disabled("disabled until bug#1 is fixed")
    public void whenRequestJoin_shouldReturnSuccessfully() throws Exception {
        System.out.println("whenRequestJoin_shouldReturnSuccessfully");
        
        this.shouldReturnResponse("join", 200, true);
    }

    @Test
//    @Ignore // Junit4 construct
    @Disabled("disabled until bug#1 is fixed")
    public void whenRequestChat_shouldReturnSuccessfully() throws Exception {
        System.out.println("whenRequestChat_shouldReturnSuccessfully");
        
        this.shouldReturnResponse("chat", 200, true);
    }

    public void shouldReturnResponse(String reqHandlerName, int code, boolean success) throws Exception {
        
        final String url = testUrl.getUrl(port, reqHandlerName);
        
        assertThat(this.restTemplate.getForObject(url, ResponseImpl.class))
            .matches((r) -> r.isSuccess() == success && r.getCode() == code,
                    "{success="+success+", code="+code+"}");
    }
}