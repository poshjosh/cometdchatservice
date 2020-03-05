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
package com.looseboxes.cometd.chatservice.services.response;

import com.looseboxes.cometd.chatservice.chat.ChatSession;
import org.cometd.bayeux.Message;

/**
 * @author USER
 */
public final class ChatResponseBuilder<T> extends ResponseBuilderImpl<T>{

    public ChatResponseBuilder() { }

    public ChatResponseBuilder(ResponseCodeProvider responseCodeProvider) {
        super(responseCodeProvider);
    }

    @Override
    public Builder<T> newInstance() {
        return new ChatResponseBuilder(this.getResponseCodeProvider());
    }
    
    @Override
    public Response.Builder<T> message(Object message) {
        final String sval;
        if(message instanceof ChatSession) {
            sval = ((ChatSession)message).getState().toString();
        }else{
            sval = message.toString();
        }
        return this.message(sval);
    }

    @Override
    public Response.Builder<T> data(T data) {
        if(data instanceof Message) {
            final Message message = (Message)data;
            this.success(message.isSuccessful());
        }
        return super.data(data);
    }
}
