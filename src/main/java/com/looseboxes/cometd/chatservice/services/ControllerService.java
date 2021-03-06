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
package com.looseboxes.cometd.chatservice.services;

import com.looseboxes.cometd.chatservice.chat.ChatSession;
import com.looseboxes.cometd.chatservice.services.exceptions.InvalidRequestParameterException;
import com.looseboxes.cometd.chatservice.services.response.Response;
import java.util.Map;
import org.cometd.bayeux.server.BayeuxServer;

/**
 * @author USER
 */
public interface ControllerService {
    
    interface ServiceContext {
    
        BayeuxServer getBayeuxServer(); 
        
        default boolean isJoinedToChat() {
            final ChatSession chatSession = getChatSession();
            return chatSession == null ? false : 
                    chatSession.getState().isConnecting() ||
                    chatSession.getState().isConnected();
        }

        ChatSession getChatSession();
    
        default Object requireNonNullOrEmptyParameter(String paramName) {
            final Map<String, Object> params = getParameters();
            if(this.isNullOrEmptyParameter(paramName)) {
                throw new InvalidRequestParameterException(
                        "Invalid value for parameter: " + paramName);
            }
            return params.get(paramName);
        }

        default boolean isNullOrEmptyParameter(String paramName) {
            final Map<String, Object> params = getParameters();
            final Object paramValue = params.get(paramName);
            if(paramValue == null) {
                return true;
            }else if(paramValue instanceof CharSequence) {
                if(((CharSequence)paramValue).length() == 0) {
                    return true;
                }
            }else if(paramValue instanceof Object[]) {
                final Object [] arr = ((Object[])paramValue);
                if(arr.length == 0) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * @return A map of parameters primarily extracted from a 
         * {@link javax.servlet.http.HttpServletRequest HttpServletRequest} and 
         * whose values are either <code>String</code> (i.e text) or
         * <code>String[]</code> (i.e array of text values)
         */
        Map<String, Object> getParameters();
    }
    
    /**
     * Best effort should be made to always return a response, regardless of 
     * what exception is thrown during processing. However if the method argument
     * is <code>null</code> then the method should throw NullPointerException.
     * @param serviceContext The context holding objects required for processing
     * @return A response
     */
    Response process(ServiceContext serviceContext);
}
