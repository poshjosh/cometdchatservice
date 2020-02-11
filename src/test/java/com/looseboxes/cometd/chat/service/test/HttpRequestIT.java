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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
    @Order(1) 
    public void membersRequest_AfterJoinRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("membersRequest_AfterJoinRequest_ShouldReturnSuccessfully");
        
        final ResponseEntity<ResponseImpl> result = this.givenEndpoint_ShouldReturn(
                Endpoints.JOIN, 200, true);
        
        final List<String> cookies = result.getHeaders().get("Set-Cookie");
        
        final ResponseEntity<ResponseImpl> members = this.givenEndpoint_ShouldReturn(
                Endpoints.MEMBERS, cookies, 200, true);
        
        final ResponseImpl body = members.getBody();
        final Object data = body == null ? null : body.getData();
        System.out.println(data);
        System.out.println("----------------------------------");
        
        assertNotEquals("{members={}}", data);
    }

    @Test
    @Order(2) 
    public void joinRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("joinRequest_ShouldReturnSuccessfully");
        
        this.givenEndpoint_ShouldReturn(Endpoints.JOIN, 200, true);
    }

    @Test
    @Order(3) 
    public void chatRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("chatRequest_ShouldReturnSuccessfully");
        
        this.givenEndpoint_ShouldReturn(Endpoints.CHAT, 200, true);
    }
    
    @Test
    @Order(4) 
    public void shutdownRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("shutdownRequest_ShouldReturnSuccessfully");
    
        this.givenEndpoint_ShouldReturn(Endpoints.SHUTDOWN, 200, true);
    }

    private ResponseEntity<ResponseImpl> givenEndpoint_ShouldReturn(String endpoint, 
            int code, boolean success) throws Exception {
        
        return this.givenEndpoint_ShouldReturn(endpoint, Collections.EMPTY_LIST, code, success);
    }
    
    private ResponseEntity<ResponseImpl> givenEndpoint_ShouldReturn(String endpoint, List<String> cookies, 
            int code, boolean success) throws Exception {
        
        final String url = testUrls.getEndpointUrlWithParams(port, endpoint);
        
        return this.givenUrl_ShouldReturn(url, cookies, code, success);
    }

    private ResponseEntity<ResponseImpl> givenUrl_ShouldReturn(String url, 
            int code, boolean success) throws Exception {
        
        return this.givenUrl_ShouldReturn(url, Collections.EMPTY_LIST, code, success);
    }
    
    private ResponseEntity<ResponseImpl> givenUrl_ShouldReturn(String url, List<String> cookies, 
            int code, boolean success) throws Exception {
        
        System.out.println("----------------------------------");
        System.out.println("Executing request to: " + url);
        System.out.println("    With cookies: " + cookies);
        System.out.println("----------------------------------");
        
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        final ResponseEntity<ResponseImpl> result = restTemplate.exchange(url, HttpMethod.GET, entity, ResponseImpl.class);
        
        final List<String> cookiesReceived = result.getHeaders().get("Set-Cookie");
        System.out.println("Cookies received: " + cookiesReceived);
        System.out.println("----------------------------------");
        
        assertThat(result.getBody())
            .matches((r) -> r.isSuccess() == success && r.getCode() == code,
                    "{success="+success+", code="+code+"}");
        
        return result;
    }
}