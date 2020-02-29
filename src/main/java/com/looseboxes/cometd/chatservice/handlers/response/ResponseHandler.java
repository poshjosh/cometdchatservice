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

import com.looseboxes.cometd.chatservice.handlers.exceptions.ProcessingRequestException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author USER
 */
public interface ResponseHandler<T> {
    void onSuccess(ServletRequest req, ServletResponse res, T responseData) throws ProcessingRequestException;
    void onFailure(ServletRequest req, ServletResponse res, T responseData) throws ProcessingRequestException;
    void onAlways(ServletRequest req, ServletResponse res, T responseData) throws ProcessingRequestException;
}
