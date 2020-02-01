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

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author USER
 */
@TestConfiguration
public class TestConfig {
    
    @Bean public TestUtil testUtil() {
        return new TestUtil();
    }
    
    @Bean public Mocker mocker() {
        return new Mocker();
    }
    
    @Bean public TestEndpointRequests testEndpoints() {
        return new TestEndpointRequests();
    }
    
    @Bean public TestUrls testUrl() {
        return new TestUrls();
    }
}
