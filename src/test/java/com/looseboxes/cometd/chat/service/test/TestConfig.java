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

import com.looseboxes.cometd.chat.service.AppConfiguration;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
@TestConfiguration
public class TestConfig {
    
    private static final ResponseConfiguration resConfig = new ResponseConfiguration();
    private static final AppConfiguration appConfig = new AppConfiguration(resConfig);
    
    @Bean public TestData testData() {
        return new TestData();
    }
    
    @Bean public TestUtil testUtil() {
        return new TestUtil();
    }
    
    @Bean public Mocker mocker() {
        return new Mocker(this);
    }
    
    @Bean public EndpointRequestBuilders endpointRequestBuilders() {
        return new EndpointRequestBuilders();
    }
    
    @Bean public TestUrls testUrl() {
        return new TestUrls();
    }
    
    public AppConfiguration appConfig() {
        return appConfig;
    }
}
