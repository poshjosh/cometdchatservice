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
package com.looseboxes.cometd.chatservice.services.response;

/**
 * @author USER
 */
public interface Response<T> {
    
    public static Response.Builder<?> builder() {
        return new ResponseBuilderImpl<>();
    }

    int getCode();

    T getData();

    String getMessage();

    boolean isSuccess();
    
    long getTimestamp();

    public interface Builder<T> {
        
        Builder<T> newInstance();

        Response<T> build();

        Response.Builder<T> code(int code);

        Response.Builder<T> data(T data);

        Response.Builder<T> error(boolean error);

        boolean isBuildAttempted();

        default Response.Builder<T> message(Object message){
            return this.message(message.toString());
        }
        
        Response.Builder<T> message(String message);

        Response.Builder<T> success(boolean success);
    }
}
