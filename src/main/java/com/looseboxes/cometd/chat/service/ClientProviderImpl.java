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
import java.util.Objects;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author USER
 */
public class ClientProviderImpl implements ClientProvider{
    
    private final ClientTransportProvider transportProvider;

    public ClientProviderImpl(@Autowired ClientTransportProvider transportProvider) {
        this.transportProvider = Objects.requireNonNull(transportProvider);
    }
    
    @Override
    public ClientSession createClient(String url, Map<String, Object> transportOptions) 
            throws ClientProvider.ClientInitializationException{
        
        try{
            
            final ClientTransport transport = this.transportProvider.createClientTransport(transportOptions);

            // Create the BayeuxClient.
            return new BayeuxClient(url, transport);
            
        }catch(Exception e) {
            
            throw new ClientProvider.ClientInitializationException(e);
        }
    }
}
