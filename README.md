## cometdchatservice
#### A Java microservice exposing cometd chat services

### Example Usage

#### Imports
```java
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
import com.looseboxes.cometd.chat.service.ChatListener;
import com.looseboxes.cometd.chat.service.ChatSession;
```

Declare some variables.
```java
final String JOHN = "John";
final String MARY = "Mary";
final long timeout = 7_000;
```

#### To run this successfully, make sure the jetty server is started.

#### Create and set up Jetty's HttpClient (One for each user)
```java
final HttpClient httpClient = new HttpClient();

// Here set up Jetty's HttpClient.
// httpClient.setMaxConnectionsPerDestination(2);
httpClient.start();

final LongPollingTransport tpt = new LongPollingTransport(new HashMap(), httpClient);

final String url = "http://localhost:8092/cometd";

final BayeuxClient johnClient = new BayeuxClient(url, tpt);
```

#### Use each client to create a chat session for each user

Use john's client to create johns chat session
```java
final ChatConfig johnConfig = ChatConfig.builder()
        .logLevel(Chat.LOG_LEVEL_VALUES.DEBUG)
        .channel("/service/privatechat")
        .room("/chat/demo")
        .user(JOHN).build();

final ChatSession johnSession = new ChatSessionImpl(johnClient, johnConfig);

final Future<Message> johnJoin = johnSession.join();

final Message johnConnMsg = johnJoin.get(timeout, TimeUnit.MILLISECONDS);
System.out.println("ReadMe:: Response to " + JOHN + "'s join: "+johnConnMsg);
```

Use mary's client to create johns chat session
```java
final ChatConfig maryConfig = johnConfig.forUser(MARY);

final ChatSession marySession = new ChatSessionImpl(maryClient, maryConfig);

final Future<Message> maryJoin = marySession.join();

final Message maryConnMsg = maryJoin.get(timeout, TimeUnit.MILLISECONDS);
System.out.println("ReadMe:: Response to " + MARY + "'s join: "+maryConnMsg);
```

Send messages back and forth
```java
johnSession.send("Hi", MARY, (Message msg) -> {
    System.out.println("ReadMe:: Response to "+JOHN+"'s message sending: " + msg);
});

johnSession.send("Hi " + JOHN, JOHN, (Message msg) -> {
    System.out.println("ReadMe:: Response to "+MARY+"'s message sending: " + msg);
});
```

Make sure to leave when done
```java
final Future<Message> johnLeave = johnSession.leave();

final Future<Message> maryLeave = marySession.leave();

johnLeave.get(timeout, TimeUnit.MILLISECONDS);

maryLeave.get(timeout, TimeUnit.MILLISECONDS);
```

### Use of listener

// Add listeners to a chat session
```java
johnSession.listeners().addListener(new ChatListener(){
    @Override
    public void onUnsubscribe(ChatListener.Event event) { }
    @Override
    public void onSubscribe(ChatListener.Event event) { }
    @Override
    public void onDisconnect(ChatListener.Event event) { }
    @Override
    public void onConnect(ChatListener.Event event) { }
});
```

### Naming Conventions for Channels and Rooms
- Chat rooms must start with ```/chat``` e.g ```/chat/soccer``` or ```/chat/customercare```

- Service rooms start with ```/service```

- For each chat room there is a corresponding service channel. 
E.g. For ```/chat/soccer``` the service would be ```/service/soccer```

- For each chat room there is a members channel on the server local session i.e 
```java
ServerSession.getLocalSession().getChannel(..) 
```
E.g. For ```/chat/soccer``` the members channel would be ```/members/soccer``` 

- Private chat configured at room ```/chat/privatechat```
