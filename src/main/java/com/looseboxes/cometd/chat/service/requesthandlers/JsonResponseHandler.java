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
package com.looseboxes.cometd.chat.service.requesthandlers;

import com.looseboxes.cometd.chat.service.requesthandlers.exceptions.ProcessingRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public final class JsonResponseHandler implements ResponseHandler<Response>{
    
    public JsonResponseHandler() { }

    @Override
    public void onSuccess(ServletRequest req, ServletResponse res, Response responseData) 
            throws ProcessingRequestException {
        this.process(req, res, responseData);
    }

    @Override
    public void onFailure(ServletRequest req, ServletResponse res, Response responseData) 
            throws ProcessingRequestException {
        this.process(req, res, responseData);
    }

    @Override
    public void onAlways(ServletRequest req, ServletResponse res, Response responseData) { }

    public void process(ServletRequest req, ServletResponse res, Response responseData) {
        
        try{

            res.setContentType("application/json");

            final ObjectMapper objectMapper = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(req.getServletContext()).getBean(ObjectMapper.class);

            objectMapper.writeValue(res.getOutputStream(), responseData);

        }catch(IOException e) {

            throw new UncheckedIOException(e);
        }
    }
}
