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
package com.looseboxes.cometd.chat.service;

import java.util.Map;
import org.cometd.client.transport.ClientTransport;

/**
 * @author USER
 */
public interface ClientTransportProvider {
    
    public static final class ClientTransportInitializationException extends RuntimeException{

        public ClientTransportInitializationException() { }

        public ClientTransportInitializationException(String message) {
            super(message);
        }

        public ClientTransportInitializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClientTransportInitializationException(Throwable cause) {
            super(cause);
        }
    }
    
    ClientTransport createClientTransport(Map<String, Object> transportOptions) 
            throws ClientTransportInitializationException;
}
