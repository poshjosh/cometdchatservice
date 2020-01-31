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

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.cometd.annotation.AnnotationCometDServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.web.context.support.WebApplicationContextUtils;

@ServletComponentScan
@SpringBootApplication
public class CometDApplication implements ServletContextInitializer {
    
    public static void main(String[] args) {
        SpringApplication.run(CometDApplication.class, args);
    }
    
    @Override
    public void onStartup(ServletContext servletContext) {
        
        final CometDProperties cometdProps = WebApplicationContextUtils
                .getWebApplicationContext(servletContext).getBean(CometDProperties.class);
        
        final ServletRegistration.Dynamic cometdServlet = servletContext
                .addServlet(cometdProps.getServletName(), AnnotationCometDServlet.class);
        
        final String mapping = cometdProps.getServletPath();
        cometdServlet.addMapping(mapping);
        cometdServlet.setAsyncSupported(true);
        cometdServlet.setLoadOnStartup(1);
        cometdServlet.setInitParameter("services", ChatService.class.getName());
        cometdServlet.setInitParameter("ws.cometdURLMapping", mapping);

        ServletRegistration.Dynamic demoServlet = servletContext.addServlet(
                cometdProps.getDefaultServletName(), CometDServlet.class);
        demoServlet.addMapping(cometdProps.getDefaultServletPath());
        demoServlet.setAsyncSupported(true);
        demoServlet.setLoadOnStartup(2);
    }
}