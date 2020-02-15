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
import java.util.Scanner;

/**
 * @author USER
 */
public class ShutdownViaSpringBootActuator {
    
    public static void main(String... args) {
        new ShutdownViaSpringBootActuator().run();
    }
    
    public void run() {
        run(-1);
    }
    
    public void run(int port) {
        try{
            
            port = getPort(port);

            final URL shutdownUrl = new URL("http://localhost:"+port+"/actuator/shutdown");
            System.out.println(ShutdownViaSpringBootActuator.class.getSimpleName()+". URL: " + shutdownUrl);

            final HttpURLConnection conn = (HttpURLConnection)shutdownUrl.openConnection();
            conn.setRequestMethod("POST");
            
            try(InputStream in = conn.getInputStream()) {
                final Scanner scan = new Scanner(in);
                System.out.println(ShutdownViaSpringBootActuator.class.getSimpleName()+". Shutdown response:\n");
                while(scan.hasNextLine()) {
                    System.out.println(scan.nextLine());
                }
            }
        }catch(java.net.ConnectException e) {
            System.out.println("Web application probably not online");
            e.printStackTrace();
        }catch(IOException e) {
            e.printStackTrace();
        }    
    }
    
    private int getPort(int port) throws IOException{
        if(port < 1) {
            final Properties props = new Properties();
            try(InputStream propStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("application.properties")) {
                props.load(propStream);
            }
            final String portStr = props.getProperty("server.port", "8080");
            System.out.println(ShutdownViaSpringBootActuator.class.getSimpleName()+". port: " + portStr);
            if(portStr.isEmpty()) {
                throw new IllegalArgumentException();
            }
            port = Integer.parseInt(portStr);
        }
        return port;
    }
}
