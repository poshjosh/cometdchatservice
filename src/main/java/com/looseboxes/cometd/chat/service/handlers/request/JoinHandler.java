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
package com.looseboxes.cometd.chat.service.handlers.request;

import com.looseboxes.cometd.chat.service.AttributeNames;
import com.looseboxes.cometd.chat.service.ClientProvider;
import com.looseboxes.cometd.chat.service.ClientSessionChannelSubscription;
import com.looseboxes.cometd.chat.service.CometDProperties;
import com.looseboxes.cometd.chat.service.handlers.response.Response;
import com.looseboxes.cometd.chat.service.handlers.ServletUtil;
import com.looseboxes.cometd.chat.service.handlers.exceptions.ProcessingRequestTimeoutException;
import com.looseboxes.cometd.chat.service.handlers.response.ResponseBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.client.BayeuxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author USER
 */
public class JoinHandler extends AbstractRequestHandler{

    private static final Logger LOG = LoggerFactory.getLogger(JoinHandler.class);

    public JoinHandler() { }
    
    public boolean isJoinedToChat(HttpServletRequest req, HttpServletResponse res) {
        
        final ClientSession client = (ClientSession)req.getSession().getAttribute(
                AttributeNames.Session.COMETD_CLIENT_SESSION);
        
        return client == null ? false : client.isHandshook();
    }
    
    @Override
    public Response doProcess(HttpServletRequest req, HttpServletResponse res) {
        
        final WebApplicationContext webAppCtx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(req.getServletContext());
        
        final boolean alreadyJoined = this.isJoinedToChat(req, res);
        
        if(alreadyJoined) {
        
            final ResponseBuilder resBuilder = webAppCtx.getBean(ResponseBuilder.class);
            
            return resBuilder.buildResponse("Already joined chat", null, false);
        }

// This may have an asterix e.g /cometd/*  therefore we use the literal /cometd
//        final String cometdPath = webAppCtx.getBean(CometDProperties.class).getServletPath();

        final String url = Objects.requireNonNull(
                webAppCtx.getBean(ServletUtil.class).getContextUrl(req)) + "/cometd";

        LOG.debug("URL: {}", url);

        final ClientSession client;
        
        final Object attr = req.getSession().getAttribute(
                AttributeNames.Session.COMETD_CLIENT_SESSION);
        
        if(attr != null) {
            
            client = (ClientSession)attr;
            
        }else{
        
            final Map<String, Object> transportOptions = new HashMap<>();

            final ClientProvider clientProvider = webAppCtx.getBean(ClientProvider.class);

            // Create the BayeuxClient.
            client = clientProvider.createClient(url, transportOptions);

            req.getSession().setAttribute(AttributeNames.Session.COMETD_CLIENT_SESSION, client);
        }
        
        // Here set up the BayeuxClient.
//            client.getChannel(Channel.META_CONNECT).addListener(
//                    (ClientSessionChannel.MessageListener)(ClientSessionChannel sessChannel, Message msg) -> {
//            });

        final Map handshakeConfig = new HashMap<>();

        final long handshakeStart = System.currentTimeMillis();
        
        client.handshake(handshakeConfig);
        
        final CometDProperties cometdProps = webAppCtx.getBean(CometDProperties.class);

        final boolean handshaken = ((BayeuxClient)client).waitFor(cometdProps.getHandshakeTimeout(), BayeuxClient.State.HANDSHAKEN);
        
        if( ! handshaken && (System.currentTimeMillis() - handshakeStart) >= cometdProps.getHandshakeTimeout()) {
            throw new ProcessingRequestTimeoutException("Time out: " + cometdProps.getHandshakeTimeout() + " millis");
        }
        
        final String chatchannel = cometdProps.getDefaultChannel();

        return webAppCtx.getBean(ClientSessionChannelSubscription.class).subscribe(
                client, chatchannel, cometdProps.getSubscriptionTimeout());
    }
}
