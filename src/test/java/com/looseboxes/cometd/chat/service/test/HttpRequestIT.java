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

import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author USER
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(MyTestConfiguration.class)
@TestMethodOrder(OrderAnnotation.class)
public class HttpRequestIT {
    
    @Autowired private TestUrls testUrls;

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Test
//    @Ignore // Junit4 construct
    @Disabled("disabled until issue #1 is fixed")
    @Order(1) 
    public void joinRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("joinRequest_ShouldReturnSuccessfully");
        
        this.givenEndpoint_ShouldReturn(Endpoints.JOIN, 200, true);
    }

    @Test
//    @Ignore // Junit4 construct
    @Disabled("disabled until issue #1 is fixed")
    @Order(2) 
    public void chatRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("chatRequest_ShouldReturnSuccessfully");
        
        this.givenEndpoint_ShouldReturn(Endpoints.CHAT, 200, true);
    }
    
    @Test
//    @Ignore // Junit4 construct
    @Disabled("disabled until issue #1 is fixed")
    @Order(3) 
    public void membersRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("membersRequest_ShouldReturnSuccessfully");
        
        this.givenEndpoint_ShouldReturn(Endpoints.MEMBERS, 200, true);
    }

    @Test
    @Order(4) 
    public void shutdown_ShouldReturnSuccessfully() throws Exception {
        System.out.println("shutdown_ShouldReturnSuccessfully");
    
        this.givenEndpoint_ShouldReturn(Endpoints.SHUTDOWN, 200, true);
    }

    private void givenEndpoint_ShouldReturn(String endpoint, int code, boolean success) throws Exception {
        
        final String url = testUrls.getEndPointUrl(port, endpoint);
        
        this.givenUrl_ShouldReturn(url, code, success);
    }

    private void givenUrl_ShouldReturn(String url, int code, boolean success) throws Exception {
        
        assertThat(this.restTemplate.getForObject(url, ResponseImpl.class))
            .matches((r) -> r.isSuccess() == success && r.getCode() == code,
                    "{success="+success+", code="+code+"}");
    }
}