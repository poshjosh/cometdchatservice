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

import com.looseboxes.cometd.chatservice.test.TestConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ChatListenerManagerTest {
    
    private final boolean logStackTrace = TestConfig.LOG_STACKTRACE;
    
    @Test
    public void containsListener_whenListenerRemoved_doesNotContainRemovedListener() {
        System.out.println("containsListener_whenListenerRemoved_doesNotContainRemovedListener");
        
        final ChatListener listener = getChatListener();
        
        final ChatListenerManager listenerManager = getChatListenerManager();
        
        // Add first, to be able to remove added
        listenerManager.addListener(listener);

        listenerManager.removeListener(listener);

        this.assertThatListenerManagerDoesNotContainListener(listenerManager, listener);
    }

    @Test
    public void containsListener_whenListenerAdded_containsAddedListener() {
        System.out.println("containsListener_whenListenerAdded_containsAddedListener");
        
        final ChatListener listener = getChatListener();
        
        final ChatListenerManager listenerManager = getChatListenerManager();
        
        listenerManager.addListener(listener);
        
        this.assertThatListenerManagerContainsListener(listenerManager, listener);
    }
    
    private void assertThatListenerManagerContainsListener(
            ChatListenerManager listenerManager, ChatListener listener) {
        assertTrue(listenerManager.containsListener(listener));
    }

    private void assertThatListenerManagerDoesNotContainListener(
            ChatListenerManager listenerManager, ChatListener listener) {
        assertFalse(listenerManager.containsListener(listener));
    }

    @Test
    public void containsListener_whenNullListener_throwException() {
        this.whenNullChatListener_throwException("containsListener");
    }

    @Test
    public void removeListener_whenCalledMultipleTimes_decreasesSizeBySameTimes() {
        System.out.println("removeListener_whenCalledMultipleTimes_decreasesSizeBySameTimes");

        final ChatListenerManager listenerManager = getChatListenerManager();
        
        final List<ChatListener> list = newList(getChatListener(), getChatListener());
        for(ChatListener e : list) {
            listenerManager.addListener(e);
        }

        final int sizeB4 = listenerManager.size();
        int numberOfItemsRemoved = 0;
        for(ChatListener e : list) {
            listenerManager.removeListener(e);
            ++numberOfItemsRemoved;
        }
        
        final int currentSize = listenerManager.size();

        assertThat(sizeB4 - currentSize, is(numberOfItemsRemoved));
    }

    @Test
    public void addListener_whenCalledMultipleTimes_increasesSizeBySameTimes() {
        System.out.println("addListener_whenCalledMultipleTimes_increasesSizeBySameTimes");

        final ChatListenerManager listenerManager = getChatListenerManager();
        
        final int sizeB4 = listenerManager.size();
        
        final List<ChatListener> listeners = newList(getChatListener(), getChatListener());
        for(ChatListener e : listeners) {
            listenerManager.addListener(e);
        }

        assertThat(listenerManager.size() - sizeB4, is(listeners.size()));
    }

    @Test
    public void removeListener_whenValidChatListener_returnFalse() {
        this.onMethodCall_whenValidChatListener_return(
                "removeListener", this.getChatListener(), false);
    }

    @Test
    public void removeListener_whenListenerPreviouslyAdded_returnTrue() {
        
        final ChatListener listener = this.getChatListener();
        
        final ChatListenerManager listenerManager = this.getChatListenerManager();
        
        // We have to add first for remove to return true
        this.onMethodCall_whenValidChatListener_return(
                        "addListener", listenerManager, listener, true);
        
        this.onMethodCall_whenValidChatListener_return(
                "removeListener", listenerManager, listener, true);
    }

    @Test
    public void addListener_whenValidChatListener_returnTrue() {
        this.onMethodCall_whenValidChatListener_return(
                "addListener", this.getChatListener(), true);
    }

    public void onMethodCall_whenValidChatListener_return(
            String methodName, ChatListener listener, boolean returnValue) {
        
        final ChatListenerManager listenerManager = this.getChatListenerManager();
        
        this.onMethodCall_whenValidChatListener_return(
                methodName, listenerManager, listener, returnValue);
    }

    public void onMethodCall_whenValidChatListener_return(
            String methodName, ChatListenerManager listenerManager, 
            ChatListener listener, boolean expectedResult) {
        System.out.println(methodName + "_whenValidChatListener_return" + (expectedResult ? "True" : "False"));
        final boolean result = this.invokeListenerManagerMethod(
                listenerManager, methodName, listener);
        assertEquals(expectedResult, result);
    }

    @Test
    public void removeListener_whenNullChatListener_throwException() {
        this.whenNullChatListener_throwException("removeListener");
    }

    @Test
    public void addListener_whenNullChatListener_throwException() {
        this.whenNullChatListener_throwException("addListener");
    }

    
    public void whenNullChatListener_throwException(String methodName) {
        System.out.println(methodName + "_whenNullChatListener_throwException");
        final ChatListener listener = null;
        final ChatListenerManager listenerManager = getChatListenerManager();
        
        final RuntimeException thrown = assertThrows(
                RuntimeException.class, 
                () -> invokeListenerManagerMethod(listenerManager, methodName, listener),
                "Should throw exception, but execution completed");
        
        if(this.logStackTrace) {
            thrown.printStackTrace();
        }
    }
    
    public boolean invokeListenerManagerMethod(ChatListenerManager listenerManager, 
            String methodName, ChatListener listener) {
        
        final boolean ret;
        switch(methodName) {
            case "addListener": 
                ret = listenerManager.addListener(listener); 
                break;
            case "removeListener": 
                ret = listenerManager.removeListener(listener); 
                break; 
            case "containsListener": 
                ret = listenerManager.containsListener(listener); 
                break; 
            default: throw new IllegalArgumentException(
                    "Unsupported method name: " + methodName);
        }        
        return ret;
    }

    @Test
    public void fireEvent_whenNullEventHandler_throwException() {
        System.out.println("fireEvent_whenNullEventHandler_throwException");
        final ChatListener.Event event = getChatListenerEvent();
        final ChatListenerManager.EventHandler eventHandler = null;

        final RuntimeException thrown = assertThrows(
                RuntimeException.class, 
                () -> fireEvent(event, eventHandler),
                "Should throw exception, but execution completed");
        
        if(this.logStackTrace) {
            thrown.printStackTrace();
        }
    }

    @Test
    public void fireEvent_whenNullEvent_throwException() {
        System.out.println("fireEvent_whenNullEvent_throwException");
        final ChatListener.Event event = null;
        final ChatListenerManager.EventHandler eventHandler = 
                getChatListenerEventHandler();

        final RuntimeException thrown = assertThrows(
                RuntimeException.class, 
                () -> fireEvent(event, eventHandler),
                "Should throw exception, but execution completed");
        
        if(this.logStackTrace) {
            thrown.printStackTrace();
        }
    }

    @Test
    public void fireEvent_whenValidArgs_EventHandlerAndListenerAreInvoked() {
        System.out.println(
                "fireEvent_whenValidArgs_EventHandlerAndListenerAreInvoked");

        final ChatListener.Event event = getChatListenerEvent();
        final ChatListenerManager.EventHandler eventHandler = 
                getChatListenerEventHandler();
        
        final List<ChatListener> listeners = this.fireEvent(event, eventHandler);
        
        for(ChatListener listener : listeners) {

            this.verifyInvocationCount(eventHandler, listener, event);
        }
    }
    
    public List<ChatListener> fireEvent(
            ChatListener.Event event, ChatListenerManager.EventHandler eventHandler) {

        final List<ChatListener> listeners = newList(getChatListener(), getChatListener());

        final ChatListenerManager listenerManager = getChatListenerManager(listeners);

        listenerManager.fireEvent(event, eventHandler);
        
        return listeners;
    }

    public void verifyInvocationCount(
            ChatListenerManager.EventHandler eventHandler,
            ChatListener listener, ChatListener.Event event) {
        verify(eventHandler, times(1)).accept(listener, event);
        verify(listener, atMost(1)).onChatReceived(event);
        verify(listener, atMost(1)).onConnect(event);
        verify(listener, atMost(1)).onConnectionBroken(event);
        verify(listener, atMost(1)).onConnectionClosed(event);
        verify(listener, atMost(1)).onConnectionEstablished(event);
        verify(listener, atMost(1)).onDisconnect(event);
        verify(listener, atMost(1)).onHandshake(event);
        verify(listener, atMost(1)).onSubscribe(event);
        verify(listener, atMost(1)).onUnsubscribe(event);
    }

    @Test
    public void size_whenInstanceNewlyCreated_returnsZero() {
        System.out.println("size_whenInstanceNewlyCreated_returnsZero");
        final ChatListenerManager listenerManager = getChatListenerManager();
        this.assertEmpty(listenerManager);
    }
    
    private void assertEmpty(ChatListenerManager listenerManager) {
        assertThat(listenerManager.size(), is(0));
    }

    public ChatListenerManager getChatListenerManager(ChatListener... listeners) {
        return this.getChatListenerManager(this.newList(listeners));
    }

    public ChatListenerManager getChatListenerManager(List<ChatListener> listeners) {
        final ChatListenerManager listenerManager = this.getChatListenerManager();
        for(ChatListener listener : listeners) {
            synchronized(listeners) {
                listenerManager.addListener(listener);
            }
        }
        return listenerManager;
    }
    
    public List<ChatListener> newList(ChatListener... listeners) {
        return listeners == null ? Collections.EMPTY_LIST : Arrays.asList(listeners);
    }

    public ChatListenerManager getChatListenerManager() {
        return new ChatListenerManagerImpl();
    }
    
    public ChatListener getChatListener(){
        return mock(ChatListener.class);
    }

    public ChatListener.Event getChatListenerEvent(){
        return mock(ChatListener.Event.class);
    }

    public ChatListenerManager.EventHandler getChatListenerEventHandler() {
        return mock(ChatListenerManager.EventHandler.class);
    }
}
