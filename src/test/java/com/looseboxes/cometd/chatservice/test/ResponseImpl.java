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
import java.util.Objects;

/**
 * @author USER
 */
public class ResponseImpl<T> implements Response<T>{
    
    private int code;
    private String message;
    private T data;
    private boolean success;
    private long timestamp = System.currentTimeMillis();

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
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.code;
        hash = 83 * hash + Objects.hashCode(this.message);
        hash = 83 * hash + Objects.hashCode(this.data);
        hash = 83 * hash + (this.success ? 1 : 0);
        hash = 83 * hash + (int) (this.timestamp ^ (this.timestamp >>> 32));
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
        final ResponseImpl<?> other = (ResponseImpl<?>) obj;
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
        return "ResponseImpl{" + "code=" + code + ", message=" + message + 
                ", data=" + data + ", success=" + success + ", timestamp=" + timestamp + '}';
    }
}
