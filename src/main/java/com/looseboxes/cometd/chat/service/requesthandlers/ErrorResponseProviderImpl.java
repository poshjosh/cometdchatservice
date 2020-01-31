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

import java.util.Objects;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author USER
 */
public final class ErrorResponseProviderImpl implements ErrorResponseProvider{
    
    private final Supplier<ResponseImpl> responseSupplier;

    public ErrorResponseProviderImpl(Supplier<ResponseImpl> responseSupplier) {
        this.responseSupplier = Objects.requireNonNull(responseSupplier);
    }

    @Override
    public Response from(Exception e) {
        
        return from("Error", e);
    }
    
    @Override
    public Response from(String msg, Exception e) {
        
        final int code;
        if(e.getClass().isAnnotationPresent(ResponseStatus.class)) {
            final HttpStatus status = e.getClass().getAnnotation(ResponseStatus.class).code();
            if(status == null) {
                code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }else{
                code = status.value();
            }
        }else{
            code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        final String data = e.getLocalizedMessage() == null ? "" : e.getLocalizedMessage();

        final ResponseImpl response = this.responseSupplier.get();
        
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(msg);
        response.setData(data);
        
        return response;
    }
}
