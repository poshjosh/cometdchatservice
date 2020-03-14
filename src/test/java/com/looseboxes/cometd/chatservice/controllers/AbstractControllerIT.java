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
import com.looseboxes.cometd.chatservice.services.ControllerService.ServiceContext;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextProvider;
import com.looseboxes.cometd.chatservice.services.response.Response;
import com.looseboxes.cometd.chatservice.test.ControllerServiceContextFromEndpointProvider;
import com.looseboxes.cometd.chatservice.test.EndpointRequestBuilders;
import com.looseboxes.cometd.chatservice.test.EndpointRequestParams;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import com.looseboxes.cometd.chatservice.test.TestData;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.hamcrest.CoreMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.fail;
import org.springframework.http.MediaType;

/**
 * Web MVC test which starts the spring application context without the web
 * server, thus narrowing the tests to only the web layer.
 * Uses @ExtendsWith(SpringExtention.class) which is JUnit5 construct for 
 * JUnit4 @RunWith(SpringRunner.class)
 * <p>
 * We provide mock objects to the spring application context by using the 
 * @MockBean annotation. @MockBean automatically replaces the bean of the same 
 * type in the application context with a Mockito mock.
 * </p>
 * @author USER
 */
// To start the web server use the commented out annotations, as opposed to the currently used.
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(MyTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@Import(MyTestConfiguration.class)
public abstract class AbstractControllerIT {
    
    private final boolean debug = TestConfig.DEBUG;
    
    @FunctionalInterface
    public static interface Verifier{ 
        void verify();
    }
    
    @Autowired private ControllerServiceContextFromEndpointProvider provideSvcCtxForEndpoint;
    
    @Autowired private TestData testData;
    
    @Autowired private EndpointRequestParams endpointReqParams;

    @Autowired private EndpointRequestBuilders endpointReqBuilders;

    @Autowired private MockMvc mockMvc;
    
    /** Required by the Controller being tested */
    @MockBean private ControllerServiceContextProvider serviceContextProvider;

    protected abstract ControllerService getControllerService();

    public void requestToEndpoint_whenParamsValid_shouldReturnSuccessfully(
            String endpoint) {
        
        this.requestToEndpoint_whenParamsValid_shouldReturnMatchingResult(endpoint, 200);
    }
    
    public void requestToEndpoint_whenParamsValid_shouldReturnMatchingResult(
            String endpoint, int code) {
        
        this.requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                endpoint, code, this.endpointReqParams.forEndpoint(endpoint));
    }
    
    public void requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
            String endpoint, int code, Map<String, String> params) {
        
        final boolean error = code >= 300;
        
        final String message = error ? "error" : "successful";
        
        requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                endpoint, code, error, message, 
                Collections.singletonMap(endpoint, message), params);
    }
    
    public void requestToEndpoint_whenParamsValid_shouldReturnMatchingResult(
            String endpoint, int code, boolean error, 
            String message, Object data) {
    
        requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                endpoint, code, error, message, data, 
                this.endpointReqParams.forEndpoint(endpoint));
    }
    
    public void requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
            String endpoint, int code, boolean error, 
            String message, Object data, Map<String, String> params) {
        
        final Verifier svcCtxProviderVerifier = this.whenMethodFromIsCalled(
                serviceContextProvider, endpoint);

        final Verifier controllerSvcVerifier = this.whenMethodProcessIsCalled(
                getControllerService(), code, error, message, data);
        try{
            
            this.mockMvc.perform(
                    endpointReqBuilders.builder(endpoint, params).contentType(MediaType.APPLICATION_JSON))
                    .andDo(debug ? print() : (mvcResult) -> {})
                    .andExpect(status().is(code))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code", CoreMatchers.is(code)))
                    .andExpect(jsonPath("$.message", CoreMatchers.is(message)))
                    .andExpect(jsonPath("$.data", CoreMatchers.is(data)))
                    .andExpect(jsonPath("$.success", CoreMatchers.not(error)));
            
        }catch(Exception e) {
            if(debug) {
                e.printStackTrace();
            }
            fail(e.toString());
        }
        
        svcCtxProviderVerifier.verify();
        
        controllerSvcVerifier.verify();
    }

    public Verifier whenMethodFromIsCalled(
            ControllerServiceContextProvider controllerSvcCtxProvider,
            String endpoint) {
        
        final ControllerService.ServiceContext serviceCtx = 
                provideSvcCtxForEndpoint.from(endpoint);
        
        when(controllerSvcCtxProvider.from(isA(HttpServletRequest.class)))
                .thenReturn(serviceCtx);
        
        final Verifier verifier = () -> verify(controllerSvcCtxProvider)
                .from(isA(HttpServletRequest.class));
        
        return verifier;
    }

    public Verifier whenMethodProcessIsCalled(ControllerService controllerSvc,
            int code, boolean error, String message, Object data) {
        
        final Response response = testData.createResponse(code, error, message, data);
        
        when(controllerSvc.process(isA(ServiceContext.class))).thenReturn(response);
        
        final Verifier verifier = () -> verify(controllerSvc)
                .process(isA(ServiceContext.class));
        
        return verifier;
    }
}
/**
 * 
    public Verifier whenMethodBuildResponseIsCalled(
            Response.Builder responseBuilder, int code,
            boolean error, String message, Object data) {
        
        final Response response = testData.createResponse(code, error, message, data);
        
        when(responseBuilder.build()).thenReturn(response);
        
        final Verifier verifier = () -> verify(responseBuilder).build();
        
        return verifier;
    }
 * 
 */