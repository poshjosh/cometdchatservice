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
package com.looseboxes.cometd.chatservice.controllers;

import com.looseboxes.cometd.chatservice.services.ControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextProvider;
import com.looseboxes.cometd.chatservice.services.response.Response;
import com.looseboxes.cometd.chatservice.test.ControllerServiceContextFromEndpointProvider;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import com.looseboxes.cometd.chatservice.test.TestData;
import java.util.Collections;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * @author USER
 */
public class MockContextImpl implements MockContext{

    private final AbstractControllerTest test;

    public MockContextImpl(AbstractControllerTest test) {
        this.test = Objects.requireNonNull(test);
    }
    
    @Override
    public MethodCallVerifier whenMethodFromIsCalled(String endpoint) {
        
        final ControllerService.ServiceContext serviceCtx = 
                this.getProvideServiceContextForEndpoint().from(endpoint);
        
        final ControllerServiceContextProvider svcContextProvider = 
                test.getServiceContextProvider();
        
        when(svcContextProvider.from(isA(HttpServletRequest.class)))
                .thenReturn(serviceCtx);
        
        final MethodCallVerifier verifier = () -> verify(svcContextProvider)
                .from(isA(HttpServletRequest.class));
        
        return verifier;
    }

    @Override
    public MethodCallVerifier whenMethodProcessIsCalled(
            String endpoint, int code, boolean error) {
        
        final String message = error ? "error" : "successful";
        final Object data = Collections.singletonMap(endpoint, message);
        final Response response = getTestData().createResponse(code, error, message, data);
        
        final ControllerService controllerSvc = test.getControllerService();
        
        when(controllerSvc.process(isA(ControllerService.ServiceContext.class))).thenReturn(response);
        
        final MethodCallVerifier verifier = () -> verify(controllerSvc)
                .process(isA(ControllerService.ServiceContext.class));
        
        return verifier;
    }
    
    public TestData getTestData() {
        return this.getTestConfig().testData();
    }

    public ControllerServiceContextFromEndpointProvider getProvideServiceContextForEndpoint() {
        return this.getTestConfig().controllerServiceContextFromEndpointProvider();
    }
    
    public TestConfig getTestConfig() {
        return new TestConfig();
    }
}
