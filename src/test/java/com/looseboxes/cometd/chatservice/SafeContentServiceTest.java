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

import com.looseboxes.cometd.chatservice.test.CacheEvicter;
import com.looseboxes.cometd.chatservice.test.TestConfiguratonForInMemoryCache;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import com.looseboxes.cometd.chatservice.test.TestUrls;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author USER
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    MyTestConfiguration.class, TestConfiguratonForInMemoryCache.class})
public class SafeContentServiceTest {
    
    @Autowired private TestUrls testUrls;
    
    @Autowired private CacheEvicter cacheEvicter;
    
    public SafeContentServiceTest() { }
    
    @BeforeEach
    public void evictAllCaches() {
        cacheEvicter.evictAllCaches();
    }
    
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
        
        verify(whenHttpGetIsCalled).get(
                ArgumentMatchers.isA(String.class), 
                ArgumentMatchers.isA(HttpHeaders.class), 
                ArgumentMatchers.isA(Map.class), 
                ArgumentMatchers.isA(Class.class));
        
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
                ArgumentMatchers.isA(String.class), 
                ArgumentMatchers.isA(HttpHeaders.class), 
                ArgumentMatchers.isA(Map.class), 
                ArgumentMatchers.isA(Class.class))).thenReturn(response);
        
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
        final String url = this.testUrls.getContextUrl(getPort());
        return new SafeContentServiceImpl(restTemplate, url, this.getEnpoint(), 7000);
    }
    
    public String getEnpoint() {
        return "/dummyEndpoint";
    }

    public int getPort() {
        return 8080;
    }
    
    public RestTemplateForGet getRestTemplateForGet() {
        return mock(RestTemplateForGet.class);
    }
}
