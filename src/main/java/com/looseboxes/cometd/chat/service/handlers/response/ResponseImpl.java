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
package com.looseboxes.cometd.chat.service.handlers.response;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author USER
 */
public final class ResponseImpl<T> implements Serializable, Response<T>{

    private boolean success;
    
    private int code = 500; // Internal Server Error
    
    private String message = "";
    
    private T data;

    public ResponseImpl() { }

    public Response withDefaults() {
        return this.with(this.code);
    }
    
    public Response with(int code) {
        return this.with(code, this.message);
    }

    public Response with(int code, String message) {
        final ResponseImpl res = new ResponseImpl();
        res.setSuccess(success);
        res.setCode(code);
        res.setMessage(message);
        res.setData(data);
        return res;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.success ? 1 : 0);
        hash = 67 * hash + this.code;
        hash = 67 * hash + Objects.hashCode(this.message);
        hash = 67 * hash + Objects.hashCode(this.data);
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
        final ResponseImpl other = (ResponseImpl) obj;
        if (this.success != other.success) {
            return false;
        }
        if (this.code != other.code) {
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
        return "ResponseObject{" + "success=" + success + ", code=" + code + ", message=" + message + ", data=" + data + '}';
    }
}
