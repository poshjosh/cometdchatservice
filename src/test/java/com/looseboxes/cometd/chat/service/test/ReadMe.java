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
package com.looseboxes.cometd.chat.service.test;

import com.looseboxes.cometd.chat.service.ChatConfig;
import com.looseboxes.cometd.chat.service.ChatSessionImpl;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.cometd.bayeux.Message;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import com.looseboxes.cometd.chat.service.Chat;
import com.looseboxes.cometd.chat.service.ChatSession;

/**
 * To run this successfully, make sure the jetty server is started
 * @author USER
 */
public class ReadMe {
    public static void main(String... args) {
        try{
            
            // To run this successfully, make sure the jetty server is started.
            
            // Create (and eventually set up) Jetty's HttpClient.
            final HttpClient httpClient = new HttpClient();

            // Here set up Jetty's HttpClient.
            // httpClient.setMaxConnectionsPerDestination(2);
            httpClient.start();

            final LongPollingTransport tpt = new LongPollingTransport(new HashMap(), httpClient);
            
            final String url = "http://localhost:8080/chatservice/cometd";
            
            final BayeuxClient johnClient = new BayeuxClient(url, tpt);
            
            final String FROM = "Tetula";
            final String TO = "Titi";
            
            final ChatConfig johnConfig = new ChatConfig("/service/privatechat", "/chat/demo", FROM);
            johnConfig.setLogLevel(Chat.LOG_LEVEL_VALUES.DEBUG);
            
            final ChatSession john = new ChatSessionImpl(johnClient, johnConfig);
            
            final Future<Message> johnJoin = john.join();

            final long timeout = 7_000;
            final Message johnConnMsg = johnJoin.get(timeout, TimeUnit.MILLISECONDS);
            System.out.println("ReadMe:: Response to " + FROM + " join: "+johnConnMsg);

            // Client must be unique to each user. A different instance for each user
            final BayeuxClient maryClient = new BayeuxClient(url, tpt);

            final ChatConfig maryConfig = johnConfig.forUser(TO);

            final ChatSession mary = new ChatSessionImpl(maryClient, maryConfig);
            
            final Future<Message> maryJoin = mary.join();

            final Message maryConnMsg = maryJoin.get(timeout, TimeUnit.MILLISECONDS);
            System.out.println("ReadMe:: Response to " + TO + " join: "+maryConnMsg);
            
            john.send("Hi", TO, (Message msg) -> {
                System.out.println("ReadMe:: Response to message sending: " + msg);
            });

            final Future<Message> johnLeave = john.leave();

            final Future<Message> maryLeave = mary.leave();

            johnLeave.get(timeout, TimeUnit.MILLISECONDS);

            maryLeave.get(timeout, TimeUnit.MILLISECONDS);
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
