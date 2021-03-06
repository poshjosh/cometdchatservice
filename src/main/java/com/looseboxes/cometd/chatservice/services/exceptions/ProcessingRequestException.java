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
package com.looseboxes.cometd.chatservice.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author USER
 */
@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR, value=HttpStatus.INTERNAL_SERVER_ERROR)
public class ProcessingRequestException extends RuntimeException{

    public ProcessingRequestException() {
    }

    public ProcessingRequestException(String message) {
        super(message);
    }

    public ProcessingRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingRequestException(Throwable cause) {
        super(cause);
    }

    public ProcessingRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
