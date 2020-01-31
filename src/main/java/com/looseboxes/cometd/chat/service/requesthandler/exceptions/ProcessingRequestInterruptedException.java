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
package com.looseboxes.cometd.chat.service.requesthandler.exceptions;

/**
 * @author USER
 */
public class ProcessingRequestInterruptedException extends ProcessingRequestException{

    public ProcessingRequestInterruptedException() {
    }

    public ProcessingRequestInterruptedException(String message) {
        super(message);
    }

    public ProcessingRequestInterruptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessingRequestInterruptedException(Throwable cause) {
        super(cause);
    }

    public ProcessingRequestInterruptedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
