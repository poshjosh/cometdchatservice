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
package com.looseboxes.cometd.chatservice.chat;

import org.eclipse.jetty.client.HttpClient;

/**
 * @author USER
 */
public interface HttpClientInitializer {

    public static final class ClientInitializationException extends RuntimeException{

        public ClientInitializationException() { }

        public ClientInitializationException(String message) {
            super(message);
        }

        public ClientInitializationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ClientInitializationException(Throwable cause) {
            super(cause);
        }
    }

    HttpClient init(HttpClient httpClient) throws ClientInitializationException;
}
