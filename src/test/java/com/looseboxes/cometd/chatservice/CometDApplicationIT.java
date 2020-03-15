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

import com.looseboxes.cometd.chatservice.controllers.Endpoints;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import com.looseboxes.cometd.chatservice.test.TestUrls;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author USER
 */
@SpringBootTest(
        webEnvironment = WebEnvironment.DEFINED_PORT, 
        classes=CometDApplication.class)
public class CometDApplicationIT {
    
    private final boolean debug = TestConfig.DEBUG;
    
    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;
    
    @Test
    public void membersRequest_AfterJoinRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("membersRequest_AfterJoinRequest_ShouldReturnSuccessfully");
        
        final ResponseEntity<Map> joinResponse = 
                this.givenUrl_ShouldReturnSuccess(Endpoints.JOIN);
        
        final List<String> cookies = this.getCookies(joinResponse);
        
        final ResponseEntity<Map> membersResponse = givenUrl_ShouldReturnSuccess(
                Endpoints.MEMBERS, cookies);
        
        final Object data = this.getBodyValue(membersResponse, "data");
        
        assertThat("{members={}}", not(data));
    }

    @Test
    public void joinRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("joinRequest_ShouldReturnSuccessfully");
        
        this.givenUrl_ShouldReturnSuccess(Endpoints.JOIN);
    }

    @Test
    public void chatRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("chatRequest_ShouldReturnSuccessfully");

        this.givenUrl_ShouldReturnSuccess(Endpoints.CHAT);
    }
    
    private ResponseEntity<Map> givenUrl_ShouldReturnSuccess(String url) throws Exception {
        
        final ResponseEntity<Map> result = this.givenUrl_ShouldReturnSuccess(
                url, Collections.EMPTY_LIST);
        
        return result;
    }
    
    private ResponseEntity<Map> givenUrl_ShouldReturnSuccess(
            String url, List<String> cookies) throws Exception {
        
        final ResponseEntity<Map> result = this.givenUrl_ShouldReturn(
                url, cookies, 200, "success", true);
        
        return result;
    }

    private ResponseEntity<Map> givenUrl_ShouldReturn(
            String url, List<String> cookies, 
            int code, String expectedKey, Object expectedValue) throws Exception {
        
        final ResponseEntity<Map> result = this.givenUrl_ShouldReturn(
                url, cookies, code, Map.class);
        
        final Map body = result.getBody();
        
        final Object value = body == null ? null : body.get(expectedKey);
        
        if(expectedValue == null) {
            assertThat(value, isNull());
        }else{
            assertThat(value, is(expectedValue));
        }
        
        return result;
    }
    
    private <T> ResponseEntity<T> givenUrl_ShouldReturn(
            String url, List<String> cookies, 
            int code, Class<T> responseType) throws Exception {
        
        if( ! url.startsWith("http")) {
            url = this.getTestUrls().getEndpointUrlWithParams(port, url);
        }
        
        if(debug) {
            System.out.println("----------------------------------");
            System.out.println("  Request to: " + url);
            System.out.println("With cookies: " + cookies);
            System.out.println("----------------------------------");
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        final ResponseEntity<T> result = restTemplate.exchange(
                url, HttpMethod.GET, entity, responseType);
        
        this.getCookies(result);
        
        assertThat(result.getStatusCodeValue(), is(code));

        return result;
    }
    
    public List<String> getCookies(ResponseEntity responseEntity) {
        final List<String> cookiesReceived = responseEntity.getHeaders().get("Set-Cookie");
        if(debug){
            System.out.println("Cookies received: " + cookiesReceived);
            System.out.println("----------------------------------");
        }
        return cookiesReceived;
    }
    
    public Object getBodyValue(ResponseEntity<Map> responseEntity, String key) {
        final Map body = responseEntity.getBody();
        final Object value = body == null ? null : body.get(key);
        if(debug) {
            System.out.println(value);
            System.out.println("----------------------------------");
        }
        return value;
    }
    
    private final MyTestConfiguration testConfig = new MyTestConfiguration();
    public TestUrls getTestUrls() {
        return testConfig.testUrls();
    }
}