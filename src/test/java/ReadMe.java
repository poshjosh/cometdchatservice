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


import com.looseboxes.cometd.chatservice.chat.ChatConfig;
import com.looseboxes.cometd.chatservice.chat.ChatSessionImpl;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.cometd.bayeux.Message;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.HttpClient;
import com.looseboxes.cometd.chatservice.chat.Chat;
import com.looseboxes.cometd.chatservice.chat.ChatListener;
import com.looseboxes.cometd.chatservice.chat.ChatSession;

/**
 * To run this successfully, make sure the jetty server is started on the 
 * port you specified in the URL argument.
 * @author USER
 */
public class ReadMe {
    public static void main(String... args) {
        try{
            
            // To run this successfully, make sure the jetty server is started.

            // Variables
            final String JOHN = "John";
            final String MARY = "Mary";
            final long timeout = 7_000;

            // Create (and eventually set up) Jetty's HttpClient.
            final HttpClient httpClient = new HttpClient();

            // Here set up Jetty's HttpClient.
            // httpClient.setMaxConnectionsPerDestination(2);
            httpClient.start();

            final LongPollingTransport tpt = new LongPollingTransport(new HashMap(), httpClient);
            
            final String url = "http://localhost:8092/cometd";
            
            final BayeuxClient johnClient = new BayeuxClient(url, tpt);
            
            final ChatConfig johnConfig = new ChatConfig("/service/privatechat", "/chat/demo", JOHN);
            johnConfig.setLogLevel(Chat.LOG_LEVEL_VALUES.DEBUG);
            
            final ChatSession johnSession = new ChatSessionImpl(johnClient, johnConfig);
            
            final Future<Message> johnJoin = johnSession.join();

            final Message johnConnMsg = johnJoin.get(timeout, TimeUnit.MILLISECONDS);
            System.out.println("ReadMe:: Response to " + JOHN + "'s join: "+johnConnMsg);

            // Client must be unique to each user. A different instance for each user
            final BayeuxClient maryClient = new BayeuxClient(url, tpt);

            final ChatConfig maryConfig = johnConfig.forUser(MARY);

            final ChatSession marySession = new ChatSessionImpl(maryClient, maryConfig);
            
            final Future<Message> maryJoin = marySession.join();

            final Message maryConnMsg = maryJoin.get(timeout, TimeUnit.MILLISECONDS);
            System.out.println("ReadMe:: Response to " + MARY + "'s join: "+maryConnMsg);
            
            johnSession.send("Hi", MARY, (Message msg) -> {
                System.out.println("ReadMe:: Response to "+JOHN+"'s message sending: " + msg);
            });

            johnSession.send("Hi " + JOHN, JOHN, (Message msg) -> {
                System.out.println("ReadMe:: Response to "+MARY+"'s message sending: " + msg);
            });

            final Future<Message> johnLeave = johnSession.leave();

            final Future<Message> maryLeave = marySession.leave();

            johnLeave.get(timeout, TimeUnit.MILLISECONDS);

            maryLeave.get(timeout, TimeUnit.MILLISECONDS);
            
            // Add listeners if need
            johnSession.addListener(new ChatListener(){
                @Override
                public void onUnsubscribe(ChatListener.Event event) { }
                @Override
                public void onSubscribe(ChatListener.Event event) { }
                @Override
                public void onDisconnect(ChatListener.Event event) { }
                @Override
                public void onConnect(ChatListener.Event event) { }
            });
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
