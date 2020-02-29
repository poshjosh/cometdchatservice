/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
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

package com.looseboxes.cometd.chatservice.handlers.response;

import java.util.Objects;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 4:04:45 PM
 */
public class ResponseBuilderImpl implements ResponseBuilder {

    private final Supplier<ResponseImpl> responseSupplier;
    private final ResponseCodeProvider responseCodeProvider;

    public ResponseBuilderImpl(
            Supplier<ResponseImpl> responseSupplier,
            ResponseCodeProvider responseCodeProvider) {
        this.responseSupplier = Objects.requireNonNull(responseSupplier);
        this.responseCodeProvider = Objects.requireNonNull(responseCodeProvider);
    }
    
    @Override
    public <T> Response<T> buildResponse(Object msg, T value, boolean error) {
        final ResponseImpl data = this.responseSupplier.get();
        final int _c = error ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : HttpServletResponse.SC_OK;
        final int code = value == null ? _c : responseCodeProvider.from(value, _c);
        data.setCode(code);
        if(msg != null) {
            data.setMessage(msg.toString());
        }
        data.setData(value);
        data.setSuccess(!error);
        return data;
    }
}
