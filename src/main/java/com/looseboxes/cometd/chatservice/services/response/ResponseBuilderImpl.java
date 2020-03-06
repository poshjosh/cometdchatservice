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

package com.looseboxes.cometd.chatservice.services.response;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 4:04:45 PM
 */
public class ResponseBuilderImpl<T> implements Response.Builder<T>{

    private static final Logger LOG = LoggerFactory.getLogger(ResponseBuilderImpl.class);
    
    private final ResponseBean<T> bean = new ResponseBean<>();
    
    private final AtomicBoolean buildAttempted = new AtomicBoolean(false);
    
    private final ResponseCodeProvider responseCodeProvider;
    
    private boolean successSet;

    public ResponseBuilderImpl() {
        this((candidate, resultIfNone) -> resultIfNone);
    }
    
    public ResponseBuilderImpl(ResponseCodeProvider responseCodeProvider) {
        this.responseCodeProvider = Objects.requireNonNull(responseCodeProvider);
    }

    @Override
    public Response<T> build() {
        if(LOG.isTraceEnabled()) {
            LOG.trace("build() called by: {}", this);
        }
        
        if(this.isBuildAttempted()) {
            throw new IllegalStateException("Method build() may only be called once");
        }
        buildAttempted.compareAndSet(false, true);
        
        if(bean.getCode() < 1) {
            this.code(this.guessCode(bean.getData()));
        }

        if( ! successSet) {
            final int code = bean.getCode();
            this.success(code >= 200 && code < 300);
        }
        
        bean.setTimestamp(System.currentTimeMillis());
        
        return bean;
    }
    
    private int guessCode(Object obj) {
        final int _c = successSet && bean.isSuccess() ? HttpServletResponse.SC_OK :
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        return obj == null ? _c : responseCodeProvider.from(obj, _c);
    }

    @Override
    public Response.Builder<T> newInstance() {
        return new ResponseBuilderImpl(this.responseCodeProvider);
    }

    @Override
    public boolean isBuildAttempted() {
        return this.buildAttempted.get();
    }

    @Override
    public Response.Builder<T> error(boolean error) {
        this.bean.setSuccess( ! error);
        return this;
    }

    @Override
    public Response.Builder code(int code) {
        this.bean.setCode(code);
        return this;
    }

    @Override
    public Response.Builder data(T data) {
        this.bean.setData(data);
        return this;
    }

    @Override
    public Response.Builder message(String message) {
        this.bean.setMessage(message);
        return this;
    }

    @Override
    public Response.Builder success(boolean success) {
        this.successSet = true;
        this.bean.setSuccess(success);
        return this;
    }

    public ResponseCodeProvider getResponseCodeProvider() {
        return responseCodeProvider;
    }
}
