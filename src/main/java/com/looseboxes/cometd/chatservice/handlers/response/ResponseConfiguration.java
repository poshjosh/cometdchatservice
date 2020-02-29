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
package com.looseboxes.cometd.chatservice.handlers.response;

import java.util.Objects;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class ResponseConfiguration {
    
    public static interface ResponseSupplier extends Supplier<ResponseImpl>{}
    
    private static final class ResponseSupplierImpl implements ResponseSupplier{
        private final ResponseConfiguration config;
        public ResponseSupplierImpl(ResponseConfiguration config) {
            this.config = Objects.requireNonNull(config);
        }
        @Override
        public ResponseImpl get() {
            return (ResponseImpl)config.response();
        }
    }

    @Bean public ResponseBuilder responseBuilder() {
        return new ResponseBuilderImpl(this.responseSupplier(), this.responseCodeProvider());
    }
    
    @Bean public ResponseSupplier responseSupplier() {
        return new ResponseSupplierImpl(this);
    }
    
    @Bean public ResponseCodeProvider responseCodeProvider() {
        return new ResponseCodeFromSpringAnnotationProvider();
    }

    @Bean @Scope("prototype") public Response response() {
        return new ResponseImpl();
    }
}
