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
import com.looseboxes.cometd.chatservice.test.TestResponse;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Web MVC test which starts the spring application context without the web
 * server, thus narrowing the tests to only the web layer.
 * Uses @ExtendsWith(SpringExtention.class) which is JUnit5 construct for 
 * JUnit4 @RunWith(SpringRunner.class)
 * To start the web server use the commented out annotations, as opposed to the 
 * web layer only, use @SpringBootTest. Then use @AutoConfigureMockMvc
 * in place of @WebMvcTest(controllers = [CONTROLLER_CLASS])
 * <p>
 * We provide mock objects to the spring application context by using the 
 * @MockBean annotation. @MockBean automatically replaces the bean of the same 
 * type in the application context with a Mockito mock.
 * </p>
 * @author USER
 */
public abstract class AbstractControllerTest extends AbstractControllerTestBase{
    
    /**
     * Used to verify a method was called properly
     */
    @FunctionalInterface
    public static interface MethodCallVerifier{ 
        MethodCallVerifier NO_OP = () -> {};
        void verify();
    }
    
    @Autowired private TestResponse testResponse;
    @Autowired private ControllerServiceContextFromEndpointProvider 
            controllerServiceContextFromEndpointProvider;

    @MockBean private ControllerServiceContextProvider serviceContextProvider;
    
    /**
     * Should return a MockBean
     * @return A @MockBean 
     */
    protected abstract ControllerService getControllerService();
    
    @Override
    public void requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
            String endpoint, int code, boolean error, 
            Map<String, String> params) {

        final MethodCallVerifier svcCtxProviderVerifier = 
                whenMethodFromIsCalled(endpoint);

        final MethodCallVerifier controllerSvcVerifier =
                whenMethodProcessIsCalled(endpoint, code, error);

        super.requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                endpoint, code, error, params);
        
        svcCtxProviderVerifier.verify();
        
        controllerSvcVerifier.verify();
    }

    public MethodCallVerifier whenMethodFromIsCalled(String endpoint) {
        
        final ControllerService.ServiceContext serviceCtx = 
                this.getControllerServiceContextFromEndpointProvider().from(endpoint);
        
        final ControllerServiceContextProvider svcContextProvider = 
                getServiceContextProvider();
        
        when(svcContextProvider.from(isA(HttpServletRequest.class)))
                .thenReturn(serviceCtx);
        
        final MethodCallVerifier verifier = () -> verify(svcContextProvider)
                .from(isA(HttpServletRequest.class));
        
        return verifier;
    }

    public MethodCallVerifier whenMethodProcessIsCalled(
            String endpoint, int code, boolean error) {
        
        final String message = error ? "error" : "successful";
        final Object data = Collections.singletonMap(endpoint, message);
        final Response response = getTestResponse()
                .createResponse(code, error, message, data);
        
        final ControllerService controllerSvc = getControllerService();
        
        when(controllerSvc.process(isA(ControllerService.ServiceContext.class))).thenReturn(response);
        
        final MethodCallVerifier verifier = () -> verify(controllerSvc)
                .process(isA(ControllerService.ServiceContext.class));
        
        return verifier;
    }

    public ControllerServiceContextProvider getServiceContextProvider() {
        return serviceContextProvider;
    }
    
    public TestResponse getTestResponse() {
        return testResponse;
    }

    public ControllerServiceContextFromEndpointProvider 
        getControllerServiceContextFromEndpointProvider() {
        return controllerServiceContextFromEndpointProvider;
    }
}
