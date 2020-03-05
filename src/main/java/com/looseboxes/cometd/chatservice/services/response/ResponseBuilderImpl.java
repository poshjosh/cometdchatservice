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

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 4:04:45 PM
 */
public class ResponseBuilderImpl<T> implements Response<T>, Response.Builder<T>{
    
    private int code = -1;
    private T data;
    private String message;
    /**
     * 1 for success, 0 for not-success, and -1 for not set
     */
    private int success = -1;
    private long timestamp;
    private final AtomicBoolean buildAttempted = new AtomicBoolean(false);
    
    private final ResponseCodeProvider responseCodeProvider;

    public ResponseBuilderImpl() {
        this((candidate, resultIfNone) -> resultIfNone);
    }
    
    public ResponseBuilderImpl(ResponseCodeProvider responseCodeProvider) {
        this.responseCodeProvider = Objects.requireNonNull(responseCodeProvider);
    }

    @Override
    public Response<T> build() {
        
        if(this.isBuildAttempted()) {
            throw new IllegalStateException("Method build() may only be called once");
        }
        buildAttempted.compareAndSet(false, true);
        
        if(code == -1) {
            this.code(this.guessCode());
        }

        if(success == -1) {
            success(code >= 200 && code < 300);
        }
        
        this.timestamp = System.currentTimeMillis();
        
        return this;
    }
    
    private int guessCode() {
        final int _c = isSuccess() ? HttpServletResponse.SC_OK :
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        return data == null ? _c : responseCodeProvider.from(data, _c);
    }

    @Override
    public Builder<T> newInstance() {
        return new ResponseBuilderImpl(this.responseCodeProvider);
    }

    @Override
    public boolean isBuildAttempted() {
        return this.buildAttempted.get();
    }

    @Override
    public Response.Builder<T> error(boolean error) {
        return this.success( ! error);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public Response.Builder code(int code) {
        this.code = code;
        return this;
    }

    @Override
    public T getData() {
        return data;
    }

    @Override
    public Response.Builder data(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Response.Builder message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean isSuccess() {
        return success == 1;
    }

    @Override
    public Response.Builder success(boolean success) {
        this.success = success ? 1 : 0;
        return this;
    }

    public ResponseCodeProvider getResponseCodeProvider() {
        return responseCodeProvider;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + this.code;
        hash = 47 * hash + Objects.hashCode(this.data);
        hash = 47 * hash + Objects.hashCode(this.message);
        hash = 47 * hash + this.success;
        hash = 47 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResponseBuilderImpl<?> other = (ResponseBuilderImpl<?>) obj;
        if (this.code != other.code) {
            return false;
        }
        if (this.success != other.success) {
            return false;
        }
        if (this.timestamp != other.timestamp) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "code=" + code + 
                ", data=" + data + ", message=" + message + 
                ", success=" + this.isSuccess() + ", timestamp=" + timestamp + '}';
    }
}
