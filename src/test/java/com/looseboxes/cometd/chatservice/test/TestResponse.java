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
package com.looseboxes.cometd.chatservice.test;

import com.looseboxes.cometd.chatservice.services.response.Response;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author USER
 */
public class TestResponse {
    
    private final int successCode = HttpServletResponse.SC_OK;

    public void validateSuccessResponse(Response response) { 
        final int expectedCode = successCode;
        assertThat("code != " + expectedCode, response.getCode(), equalTo(expectedCode));
        assertThat("success != true", response.isSuccess(), equalTo(true));
    }
    
    public Response createSuccessResponse() {
        return this.createResponse(successCode, false, "success", null);
    }

    public <T> Response<T> createResponse(int code,
            boolean error, String message, T data) {
        final ResponseImpl res = new ResponseImpl();
        res.setCode(code);
        res.setMessage(message);
        res.setData(data);
        res.setSuccess( ! error);
        return res;
    }
}
