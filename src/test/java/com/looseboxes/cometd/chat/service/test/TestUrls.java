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

import com.looseboxes.cometd.chat.service.CometDProperties;
import com.looseboxes.cometd.chat.service.ParamNames;
import com.looseboxes.cometd.chat.service.controllers.Endpoints;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author USER
 */
public class TestUrls{
    
    @Value("${server.servlet.context-path}") private String contextPath;
    
    @Autowired private CometDProperties cometdProps;
    
    public String getShutdownUrl(int port) {
        return getContextUrl(port) + Endpoints.SHUTDOWN + '?' + ParamNames.DELAY + '=' + 5000;
    }
    
    public String getReqUrl(int port, String req) {
        return getDefaultServerUrl(port) + "?req="+Objects.requireNonNull(req); 
    }
    
    public String getDefaultServerUrl(int port) {
        return getContextUrl(port) + cometdProps.getDefaultServletPath();
    }

    public String getContextUrl(int port) {
        return getBaseUrl(port) + Objects.requireNonNull(contextPath);
    }
    
    public String getBaseUrl(int port) {
        return "http://localhost:" + port;
    }
}
