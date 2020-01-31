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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @see https://www.baeldung.com/configuration-properties-in-spring-boot
 * @author Chinomso Bassey Ikwuagwu on Jan 30, 2020 23:01:53 PM
 */
@Configuration
//@PropertySource("classpath:cometd.properties")
@ConfigurationProperties(prefix = PropertyNames.Prefixes.COMETD)
//@Profile("!test")
public class CometDProperties {
    
    private String servletName = "cometd";
    
    private String servletPath = "/cometd";
    
    private String defaultChannel = "/chat/privatechannel";
    
    private String defaultServletName = "chat";
    
    private String defaultServletPath = "/chat";

    private int handshakeTimeout = 3000;
    
    private int subscriptionTimeout = 2000;
    
    private int publishTimeout = 3000;
    
    public CometDProperties() { }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public String getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public String getDefaultServletName() {
        return defaultServletName;
    }

    public void setDefaultServletName(String defaultServletName) {
        this.defaultServletName = defaultServletName;
    }

    public String getDefaultServletPath() {
        return defaultServletPath;
    }

    public void setDefaultServletPath(String defaultServletPath) {
        this.defaultServletPath = defaultServletPath;
    }

    public int getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(int handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public int getSubscriptionTimeout() {
        return subscriptionTimeout;
    }

    public void setSubscriptionTimeout(int subscriptionTimeout) {
        this.subscriptionTimeout = subscriptionTimeout;
    }

    public int getPublishTimeout() {
        return publishTimeout;
    }

    public void setPublishTimeout(int publishTimeout) {
        this.publishTimeout = publishTimeout;
    }
}
