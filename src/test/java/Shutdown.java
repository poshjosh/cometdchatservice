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


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 * @author USER
 */
public class Shutdown {
    public static void main(String... args) {
        try{
            final Properties props = new Properties();
            try(InputStream propStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("application.properties")) {
                props.load(propStream);
            }
            final String portStr = props.getProperty("server.port", "8080");
            System.out.println(Shutdown.class.getSimpleName()+". port: " + portStr);
            if(portStr.isEmpty()) {
                throw new IllegalArgumentException();
            }

            final URL shutdownUrl = new URL("http://localhost:"+portStr+"/actuator/shutdown");
            System.out.println(Shutdown.class.getSimpleName()+". URL: " + shutdownUrl);

            final HttpURLConnection conn = (HttpURLConnection)shutdownUrl.openConnection();
            conn.setRequestMethod("POST");
            
            try(InputStream in = conn.getInputStream()) {
                final byte [] buff = new byte[1024];
                final StringBuilder builder = new StringBuilder();
                while(in.read(buff) != -1) {
                    builder.append(new String(buff));
                }
                System.out.println(Shutdown.class.getSimpleName()+". Shutdown response:\n" + builder);
            }
        }catch(java.net.ConnectException e) {
            System.out.println("Web application probably not online");
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }    
    }
}
