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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author USER
 */
public class ResponseCodeFromSpringAnnotationProvider implements ResponseCodeProvider{

    @Override
    public int from(Object o, int resultIfNone) {
        
        final int code;
        
        if(o.getClass().isAnnotationPresent(ResponseStatus.class)) {
            final HttpStatus status = o.getClass().getAnnotation(ResponseStatus.class).code();
            if(status == null) {
                code = resultIfNone;
            }else{
                code = status.value();
            }
        }else{
            code = resultIfNone;
        }
        
        return code;
    }
}    

