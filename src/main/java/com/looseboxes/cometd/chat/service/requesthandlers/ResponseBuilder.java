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

package com.looseboxes.cometd.chat.service.requesthandlers;

import java.util.Collections;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 26, 2019 4:09:29 PM
 */
public interface ResponseBuilder {

    default Response<?> buildErrorResponse(Object msg) {
        return this.buildResponse(msg, msg, true);
    }

    default Response<Throwable> buildErrorResponse(Throwable data) {
        return buildErrorResponse("Error", data);
    }

    default Response<Throwable> buildErrorResponse(Object msg, Throwable data) {
        return buildResponse(msg, data, true);
    }

    default <T> Response<Map<String, T>> buildResponse(Object msg, String name, T value, boolean error) {
        return buildResponse(msg, Collections.singletonMap(name, value), error);
    }

    default Response<String> buildSuccessResponse() {
        return this.buildResponse("Success", "success", false);
    }

    <T> Response<T> buildResponse(Object msg, T data, boolean error);
}
