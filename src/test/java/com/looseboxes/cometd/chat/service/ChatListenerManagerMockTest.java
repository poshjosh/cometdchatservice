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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.jupiter.api.Disabled;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.*;

/**
 * @author USER
 */
// @RunWith(MockitoJUnitRunner.class)   JUnit4 construct
@ExtendWith(MockitoExtension.class)
public class ChatListenerManagerMockTest {
    
    public ChatListenerManagerMockTest() { }

    @Test
    public void containsListener_whenListenerRemoved_doesNotContainRemovedListener() {
        System.out.println("containsListener_whenListenerRemoved_doesNotContainRemovedListener");
        final ChatListener listener = getChatListener();
        final ChatListenerManager listenerManager = getChatListenerManager();
        
        final List<ChatListener> listeners = mockAddListener(listenerManager);
        mockRemoveListener(listenerManager, listeners);
        mockContainsListener(listenerManager, listeners);
        
        listenerManager.addListener(listener);
        listenerManager.removeListener(listener);
        assertFalse(listenerManager.containsListener(listener));
    }

    @Test
    public void containsListener_whenListenerAdded_containsAddedListener() {
        System.out.println("containsListener_whenListenerAdded_containsAddedListener");
        final ChatListener listener = getChatListener();
        final ChatListenerManager listenerManager = getChatListenerManager();

        final List<ChatListener> listeners = mockAddListener(listenerManager);
        mockContainsListener(listenerManager, listeners);
        
        listenerManager.addListener(listener);
        assertTrue(listenerManager.containsListener(listener));
    }

    @Test
    public void containsListener_whenNullListener_throwException() {
        this.whenNullChatListener_throwException("containsListener");
    }

    @Test
    public void removeListener_whenCalledMultipleTimes_decreasesSizeBySameTimes() {
        System.out.println("removeListener_whenCalledMultipleTimes_decreasesSizeBySameTimes");
        final List<ChatListener> list = Arrays
                .asList(getChatListener(), getChatListener());
        final ChatListenerManager listenerManager = getChatListenerManager();
        
        final List<ChatListener> listeners = mockAddListener(listenerManager);
        mockRemoveListener(listenerManager, listeners);
        mockSize(listenerManager, listeners);
        
        for(ChatListener e : list) {
            listenerManager.addListener(e);
        }
        final int sizeB4 = listenerManager.size();
        for(ChatListener e : list) {
            listenerManager.removeListener(e);
        }
        assertThat(listenerManager.size() - sizeB4, is(0));
    }

    @Test
    @Disabled("@TODO - Why java.util.ConcurrentModificationException")
    public void addListener_whenCalledMultipleTimes_increasesSizeBySameTimes() {
        System.out.println("addListener_whenCalledMultipleTimes_increasesSizeBySameTimes");

        final ChatListenerManager listenerManager = getChatListenerManager();
        
        final List<ChatListener> listeners = mockAddListener(listenerManager);
        synchronized(listeners) {
            listeners.add(getChatListener());
            listeners.add(getChatListener());
        }

        mockSize(listenerManager, listeners);
        
        final int sizeB4 = listenerManager.size();
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
        
        final List<ChatListener> listeners = 
                this.onMethodCall_whenValidChatListener_return(
                        "addListener", listenerManager, listener, true);
        
        this.onMethodCall_whenValidChatListener_return(
                "removeListener", listenerManager, listener, listeners, true);
    }

    @Test
    public void addListener_whenValidChatListener_returnTrue() {
        this.onMethodCall_whenValidChatListener_return(
                "addListener", this.getChatListener(), true);
    }

    public List<ChatListener> onMethodCall_whenValidChatListener_return(
            String methodName, ChatListener listener, boolean returnValue) {

        final List<ChatListener> listeners = newList();
        
        this.onMethodCall_whenValidChatListener_return(
                methodName, listener, 
                listeners, returnValue);
        
        return listeners;
    }
    
    public void onMethodCall_whenValidChatListener_return(
            String methodName, ChatListener listener,
            List<ChatListener> listeners, boolean returnValue) {
        
        final ChatListenerManager listenerManager = this.getChatListenerManager();
        
        this.onMethodCall_whenValidChatListener_return(
                methodName, listenerManager, listener, listeners, returnValue);
    }

    public List<ChatListener> onMethodCall_whenValidChatListener_return(String methodName, 
            ChatListenerManager listenerManager, ChatListener listener, boolean returnValue) {
        
        final List<ChatListener> listeners = newList();
        
        this.onMethodCall_whenValidChatListener_return(methodName, 
                listenerManager, listener, listeners, returnValue);
        
        return listeners;
    }
    
    public void onMethodCall_whenValidChatListener_return(String methodName, 
            ChatListenerManager listenerManager, ChatListener listener,
            List<ChatListener> listeners, boolean expectedResult) {
        System.out.println(methodName + "_whenValidChatListener_return" + (expectedResult ? "True" : "False"));
        final boolean result = this.invokeListenerManagerMethod(
                listenerManager, methodName, listener, listeners);
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
        this.whenNullChatListener_throwException(methodName, newList());
    }
    
    public void whenNullChatListener_throwException(
            String methodName, List<ChatListener> listeners) {
        System.out.println(methodName + "_whenNullChatListener_throwException");
        final ChatListener listener = null;
        final ChatListenerManager listenerManager = getChatListenerManager();
        try{
            invokeListenerManagerMethod(listenerManager, methodName, listener, listeners);
            fail("Should throw exception, but execution completed");
        }catch(Exception expected){}
    }
    
    public boolean invokeListenerManagerMethod(ChatListenerManager listenerManager, 
            String methodName, ChatListener listener, List<ChatListener> listeners) {
        
        final boolean ret;
        switch(methodName) {
            case "addListener": 
                if(listener == null) {
                    this.mockAddListener_nullArg(listenerManager);
                }else{
                    this.mockAddListener(listenerManager, listeners);
                }
                ret = listenerManager.addListener(listener); 
                break;
            case "removeListener": 
                if(listener == null) {
                    this.mockRemoveListener_nullArg(listenerManager);
                }else{
                    this.mockRemoveListener(listenerManager, listeners);
                }
                ret = listenerManager.removeListener(listener); 
                break; 
            case "containsListener": 
                if(listener == null) {
                    this.mockContainsListener_nullArg(listenerManager);
                }else{
                    this.mockContainsListener(listenerManager, listeners);
                }
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
        try{
            this.fireEvent(event, eventHandler);
            fail("Should throw exception, but execution completed");
        }catch(Exception expected){}
    }

    @Test
    public void fireEvent_whenNullEvent_throwException() {
        System.out.println("fireEvent_whenNullEvent_throwException");
        final ChatListener.Event event = null;
        final ChatListenerManager.EventHandler eventHandler = 
                getChatListenerEventHandler();
        try{
            this.fireEvent(event, eventHandler);
            fail("Should throw exception, but execution completed");
        }catch(Exception expected){}
    }

    @Test
    @Disabled("@TODO - Why java.util.ConcurrentModificationException")
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

        if(event == null) {
            this.mockFireEvent_nullEvent(listenerManager, listeners);
        }else if(eventHandler == null) {
            this.mockFireEvent_nullEventHandler(listenerManager, listeners);
        }
        
        listenerManager.fireEvent(event, eventHandler);
        
        return listeners;
    }

    public void verifyInvocationCount(
            ChatListenerManager.EventHandler eventHandler,
            ChatListener listener, ChatListener.Event event) {
//        verify(eventHandler, times(1)).accept(listener, event);
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
        this.mockSize(listenerManager);
        assertThat(listenerManager.size(), is(0));
    }

    public ChatListener.Event getChatListenerEvent(){
        return mock(ChatListener.Event.class);
    }

    public ChatListenerManager.EventHandler getChatListenerEventHandler() {
        return mock(ChatListenerManager.EventHandler.class);
    }

    public ChatListenerManager getChatListenerManager(ChatListener... listeners) {
        return this.getChatListenerManager(listeners == null ? 
                Collections.EMPTY_LIST : Arrays.asList(listeners));
    }

    public ChatListenerManager getChatListenerManager(List<ChatListener> listeners) {
        final ChatListenerManager listenerManager = mock(ChatListenerManager.class);
        this.mockAddListener(listenerManager, listeners);
        for(ChatListener listener : listeners) {
            synchronized(listeners) {
                listenerManager.addListener(listener);
            }
        }
        return listenerManager;
    }

    public ChatListenerManager getChatListenerManager() {
        return mock(ChatListenerManager.class);
    }
    
    public List<ChatListener> mockAddListener(ChatListenerManager listenerMgr) {
        final List<ChatListener> listeners = newList();
        this.mockAddListener(listenerMgr, listeners);
        return listeners;
    }
    
    public ChatListenerManager mockAddListener(
            ChatListenerManager listenerMgr, List<ChatListener> listeners) {
        when(listenerMgr.addListener(ArgumentMatchers.isA(ChatListener.class)))
            .thenAnswer((invoc) -> {
                final ChatListener arg = (ChatListener)invoc.getArgument(0);
                synchronized(listeners) {
                    return listeners.add(arg);
                }
        });
        return listenerMgr;
    }

    public ChatListenerManager mockAddListener_nullArg(ChatListenerManager listenerMgr) {
        when(listenerMgr.addListener(isNull())).thenThrow(NullPointerException.class);
        return listenerMgr;
    }

    public ChatListenerManager mockFireEvent_nullEvent(
            ChatListenerManager listenerMgr, List<ChatListener> listeners) {
        
        doThrow(NullPointerException.class).when(listenerMgr).fireEvent(
                isNull(), 
                ArgumentMatchers.isA(ChatListenerManager.EventHandler.class));
        
        return listenerMgr;
    }
    
    public ChatListenerManager mockFireEvent_nullEventHandler(
            ChatListenerManager listenerMgr, List<ChatListener> listeners) {
        
        doThrow(NullPointerException.class).when(listenerMgr).fireEvent(
                ArgumentMatchers.isA(ChatListener.Event.class), 
                isNull());
        
        return listenerMgr;
    }

    public ChatListenerManager mockRemoveListener(
            ChatListenerManager listenerMgr,
            List<ChatListener> listeners) {

        when(listenerMgr.removeListener(ArgumentMatchers.isA(ChatListener.class)))
            .thenAnswer((invoc) -> {
                final ChatListener listener = (ChatListener)invoc.getArgument(0);
                synchronized(listeners) {
                    return listeners.remove(listener);
                }
        });
        
        return listenerMgr;
    }

    public ChatListenerManager mockRemoveListener_nullArg(
            ChatListenerManager listenerMgr) {
        
        when(listenerMgr.removeListener(isNull())).thenThrow(NullPointerException.class);
        
        return listenerMgr;
    }

    public ChatListenerManager mockContainsListener(
            ChatListenerManager listenerMgr,
            List<ChatListener> listeners) {

        when(listenerMgr.containsListener(ArgumentMatchers.isA(ChatListener.class)))
            .thenAnswer((invoc) -> {
                final ChatListener listener = (ChatListener)invoc.getArgument(0);
                synchronized(listeners) {
                    return listeners.contains(listener);
                }
        });
        
        return listenerMgr;
    }

    public ChatListenerManager mockContainsListener_nullArg(
            ChatListenerManager listenerMgr) {
        
        when(listenerMgr.containsListener(isNull())).thenThrow(NullPointerException.class);
        
        return listenerMgr;
    }

    public ChatListenerManager mockSize(ChatListenerManager listenerMgr) {
        
        return this.mockSize(listenerMgr, newList());
    }
    
    public ChatListenerManager mockSize(
            ChatListenerManager listenerMgr,
            List<ChatListener> listeners) {
        
        when(listenerMgr.size()).thenReturn(listeners.size());
        
        return listenerMgr;
    }
    
    public List<ChatListener> newList() {
        return Collections.synchronizedList(new ArrayList());
    }
    
    public List<ChatListener> newList(ChatListener... arr) {
        return Collections.synchronizedList(new ArrayList<>(Arrays.asList(arr)));
    }
    
    public ChatListener getChatListener(){
        return mock(ChatListener.class);
    }
}
