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

import com.looseboxes.cometd.chatservice.controllers.MockContext.MethodCallVerifier;
import com.looseboxes.cometd.chatservice.services.ControllerService;
import com.looseboxes.cometd.chatservice.services.ControllerServiceContextProvider;
import com.looseboxes.cometd.chatservice.test.EndpointRequestBuilders;
import com.looseboxes.cometd.chatservice.test.EndpointRequestParams;
import com.looseboxes.cometd.chatservice.test.MyTestConfiguration;
import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
@ExtendWith(SpringExtension.class)
@Import(MyTestConfiguration.class)
public abstract class AbstractControllerTest {
    
    private final boolean debug = TestConfig.DEBUG;
    
    @Autowired private EndpointRequestParams endpointReqParams;

    @Autowired private EndpointRequestBuilders endpointReqBuilders;

    @Autowired private MockMvc mockMvc;
    
    protected abstract MockContext getMockContext();

    protected abstract ControllerService getControllerService();

    protected abstract ControllerServiceContextProvider getServiceContextProvider();
    
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
        
        requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                endpoint, code, error, params);
    }
    
    public void requestToEndpoint_whenParamsValid_shouldReturnMatchingResult(
            String endpoint, int code, boolean error) {
    
        requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
                endpoint, code, error, this.endpointReqParams.forEndpoint(endpoint));
    }
    
    public void requestToEndpoint_whenParamsGiven_shouldReturnMatchingResult(
            String endpoint, int code, boolean error, 
            Map<String, String> params) {

        try{
        final MethodCallVerifier svcCtxProviderVerifier = 
                getMockContext().whenMethodFromIsCalled(endpoint);

        final MethodCallVerifier controllerSvcVerifier =
                getMockContext().whenMethodProcessIsCalled(endpoint, code, error);
        try{

            final ResultActions actions = this.mockMvc.perform(
                    endpointReqBuilders.builder(endpoint, params))
                    .andDo(debug ? print() : (mvcResult) -> {})
                    .andExpect(status().is(code));
            
            if(!error) {
                actions.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.code", CoreMatchers.is(code)))
                        .andExpect(jsonPath("$.success", CoreMatchers.not(error)));
            }
        }catch(Exception e) {
            if(debug) {
                e.printStackTrace();
            }
            fail(e.toString());
        }
        
        svcCtxProviderVerifier.verify();
        
        controllerSvcVerifier.verify();
        }catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
