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

import java.util.concurrent.Future;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author USER
 */
public class ChatSessionTest {
    
    public ChatSessionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addListener method, of class ChatSession.
     */
    @Test
    public void testAddListener() {
        System.out.println("addListener");
        ChatListener listener = null;
        ChatSession instance = new ChatSessionImpl();
        boolean expResult = false;
        boolean result = instance.addListener(listener);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeListener method, of class ChatSession.
     */
    @Test
    public void testRemoveListener() {
        System.out.println("removeListener");
        ChatListener listener = null;
        ChatSession instance = new ChatSessionImpl();
        boolean expResult = false;
        boolean result = instance.removeListener(listener);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of connect method, of class ChatSession.
     */
    @Test
    public void testConnect() {
        System.out.println("connect");
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.connect();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of disconnect method, of class ChatSession.
     */
    @Test
    public void testDisconnect() {
        System.out.println("disconnect");
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.disconnect();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStatus method, of class ChatSession.
     */
    @Test
    public void testGetStatus() {
        System.out.println("getStatus");
        ChatSession instance = new ChatSessionImpl();
        ChatSession.Status expResult = null;
        ChatSession.Status result = instance.getStatus();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of join method, of class ChatSession.
     */
    @Test
    public void testJoin() {
        System.out.println("join");
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.join();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of leave method, of class ChatSession.
     */
    @Test
    public void testLeave() {
        System.out.println("leave");
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.leave();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of send method, of class ChatSession.
     */
    @Test
    public void testSend_String_String() {
        System.out.println("send");
        String textMessage = "";
        String toUser = "";
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.send(textMessage, toUser);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of send method, of class ChatSession.
     */
    @Test
    public void testSend_3args() {
        System.out.println("send");
        String textMessage = "";
        String toUser = "";
        ClientSession.MessageListener messageListener = null;
        ChatSession instance = new ChatSessionImpl();
        instance.send(textMessage, toUser, messageListener);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of subscribe method, of class ChatSession.
     */
    @Test
    public void testSubscribe() {
        System.out.println("subscribe");
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.subscribe();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of unsubscribe method, of class ChatSession.
     */
    @Test
    public void testUnsubscribe() {
        System.out.println("unsubscribe");
        ChatSession instance = new ChatSessionImpl();
        Future<Message> expResult = null;
        Future<Message> result = instance.unsubscribe();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class ChatSessionImpl implements ChatSession {

        public boolean addListener(ChatListener listener) {
            return false;
        }

        public boolean removeListener(ChatListener listener) {
            return false;
        }

        public Future<Message> connect() {
            return null;
        }

        public Future<Message> disconnect() {
            return null;
        }

        public Status getStatus() {
            return null;
        }

        public Future<Message> join() {
            return null;
        }

        public Future<Message> leave() {
            return null;
        }

        public Future<Message> send(String textMessage, String toUser) {
            return null;
        }

        public void send(String textMessage, String toUser, ClientSession.MessageListener messageListener) {
        }

        public Future<Message> subscribe() {
            return null;
        }

        public Future<Message> unsubscribe() {
            return null;
        }
    }
    
}
