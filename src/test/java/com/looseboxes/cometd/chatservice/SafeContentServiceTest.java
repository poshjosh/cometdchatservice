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

import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author USER
 */
public class SafeContentServiceTest {
    
    public SafeContentServiceTest() { }

    @Test
    public void flag_whenValidText_shouldReturnValidOutput() {
        System.out.println("flag_whenValidText_shouldReturnValidOutput");
        
        final String expResult = "violence,medical";
        
        final RestTemplateForGet whenHttpGetIsCalled = 
                this.whenHttpGetIsSentToRemoteServer(expResult);
        
        final SafeContentService returnFlagsForText = 
                this.returnFlagsForText(whenHttpGetIsCalled);
        
        final String text = "A severed human head was on the bloodied operating table";
        final String flagsForText = returnFlagsForText.flag(text);
        
        verify(whenHttpGetIsCalled).get(isA(String.class), isA(HttpHeaders.class), 
                isA(Map.class), isA(Class.class));
        
        assertEquals(expResult, flagsForText);
    }
    
    @Test
    public void flag_whenEmptyText_shouldReturnEmptyText() {
        System.out.println("flag_whenEmptyText_shouldReturnEmptyText");
        this.flag_givenArg_shouldReturnEmptyText("");
    }

    @Test
    public void flag_whenNullText_shouldReturnEmptyText() {
        System.out.println("flag_whenNullText_shouldReturnEmptyText");
        this.flag_givenArg_shouldReturnEmptyText(null);
    }
    
    public RestTemplateForGet whenHttpGetIsSentToRemoteServer(String expResult) {
        
        final Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(this.getEnpoint(), expResult);
        
        final RestTemplateForGet restTemplate = this.getRestTemplateForGet();
        final HttpEntity<Map> response = new HttpEntity<>(responseBody);
        when(restTemplate.get(
                isA(String.class), isA(HttpHeaders.class), 
                isA(Map.class), isA(Class.class))).thenReturn(response);
        
        return restTemplate;
    }
    
    public void flag_givenArg_shouldReturnEmptyText(String text) {
        this.flag_givenArg_shouldReturn(text, "");
    }
    
    public void flag_givenArg_shouldReturn(String text, String expectedResult) {
        final SafeContentService safeContentService = this.getSafeContentService();
        final String result = safeContentService.flag(text);
        assertEquals(expectedResult, result);
    }

    public SafeContentService getSafeContentService() {
        return this.returnFlagsForText(this.getRestTemplateForGet());
    }
    
    public SafeContentService returnFlagsForText(RestTemplateForGet restTemplate) {
        final String url = getTestConfig().testUrl().getContextUrl(getPort());
        return new SafeContentServiceImpl(restTemplate, url, this.getEnpoint(), 7000);
    }
    
    public String getEnpoint() {
        return "/dummyEndpoint";
    }

    public int getPort() {
        return 8080;
    }
    
    public TestConfig getTestConfig() {
        return new TestConfig();
    }
    
    public RestTemplateForGet getRestTemplateForGet() {
        return mock(RestTemplateForGet.class);
    }
}
