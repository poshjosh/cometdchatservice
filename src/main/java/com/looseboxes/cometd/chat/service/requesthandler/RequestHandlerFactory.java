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

import com.looseboxes.cometd.chat.service.requesthandler.exceptions.ProcessingRequestException;
import com.looseboxes.cometd.chat.service.requesthandler.exceptions.RequestHandlerNotFoundException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author USER
 */
public interface RequestHandlerFactory {

    /**
     * @param req The ServletRequest instance for which to return a {@link com.looseboxes.cometd.chat.service.requesthandler.RequestHandler RequestHandler}
     * @param res The ServletResponse instance for which to return a {@link com.looseboxes.cometd.chat.service.requesthandler.RequestHandler RequestHandler}
     * @return A {@link com.looseboxes.cometd.chat.service.requesthandler.RequestHandler RequestHandler}
     * @throws RequestHandlerNotFoundException If a matching request handler is not found
     * @throws ProcessingRequestException if there are errors initialising the request handler
     */
    RequestHandler getRequestHandler(ServletRequest req, ServletResponse res)
            throws RequestHandlerNotFoundException, ProcessingRequestException;
}
