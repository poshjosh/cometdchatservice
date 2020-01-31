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
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;

/**
 * @author USER
 */
public final class ClientTransportProviderImpl implements ClientTransportProvider{
    
    @Override
    public ClientTransport createClientTransport(Map<String, Object> transportOptions) 
            throws ClientTransportProvider.ClientTransportInitializationException{
        
        try{
            
            // Create (and eventually set up) Jetty's HttpClient.
            final HttpClient httpClient = new HttpClient();

            // Here set up Jetty's HttpClient.
            // httpClient.setMaxConnectionsPerDestination(2);
            httpClient.start();

            return new LongPollingTransport(transportOptions, httpClient);
            
        }catch(Exception e) {
            
            throw new ClientTransportProvider.ClientTransportInitializationException(e);
        }
    }
}
