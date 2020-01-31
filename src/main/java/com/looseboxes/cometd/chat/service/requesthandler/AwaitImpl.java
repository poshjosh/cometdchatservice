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
package com.looseboxes.cometd.chat.service.requesthandler;

import com.looseboxes.cometd.chat.service.requesthandler.exceptions.ProcessingRequestInterruptedException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author USER
 */
public final class AwaitImpl implements Await{

    public AwaitImpl() { }

    @Override
    public void tillTrue(AtomicBoolean flag, Object lock, long timeout) {
        
        synchronized(lock){
        
            final long n = timeout / 10;
            final long threshold = 500;
            final long step = timeout < threshold || n > timeout ? timeout : n < threshold ? threshold : n;
            long spent = 0;
            
            while(!flag.get()) {
                try{

//                LOG.info("Time. spent / total = " + spent + " / " + timeout);

                    final long left = (timeout - spent);
                    
                    final long waitTimeout = left < step ? left : step;

                    lock.wait(waitTimeout);

                    spent += waitTimeout;

                    if(spent>=timeout) {
                        break;
                    }
                }catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ProcessingRequestInterruptedException("Time. spent / total = " + spent + " / " + timeout, e);
                }finally{
                    lock.notifyAll();
                }
            }
        }
    }
}