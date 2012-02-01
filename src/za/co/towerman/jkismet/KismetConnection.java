/**
 * za.co.towerman.jkismet.KismetConnection
 * Copyright (C) 2012 Edwin Peer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package za.co.towerman.jkismet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import za.co.towerman.jkismet.message.KismetMessage;

/**
 * @author espeer
 */
public class KismetConnection {

    private Map<String, Set<String>> supported = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> subscribed = new HashMap<String, Set<String>>();
    
    private List<KismetListener> listeners = new LinkedList<KismetListener>();
    private BufferedReader in;
    private BufferedWriter out;
    private Socket socket;
    
    private boolean running = true;
    private boolean initialised = false;
    
    private String version = null;
    private String build = null;
    private Date startTime = null;
    private String serverName = null;
    
    public KismetConnection(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        out.write("!0 REMOVE TIME\r\n");
        out.flush();
        
        while (version == null || !initialised) {
            parse(in.readLine());
        }
        
        new Thread() {

            @Override
            public void run() {
                while(running) {
                    try {
                        parse(in.readLine());
                    }
                    catch (IOException ex) {
                        break;
                    }
                    catch (Exception ex) { }
                }
                
                try {
                    in.close();
                }
                catch (IOException ex) { }
                try {
                    out.close();
                }
                catch (IOException ex) { }
                try {
                    socket.close();
                }
                catch (IOException ex) { }
            }
            
        }.start();
    }

    public String getBuild() {
        return build;
    }

    public String getServerName() {
        return serverName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getVersion() {
        return version;
    }
    
    public void register(KismetListener listener) throws IOException {
        for (String protocol : listener.subscriptions.keySet()) {
            if (! supported.containsKey(protocol)) {
                throw new IllegalArgumentException("server does not support protocol: " + protocol);
            }
            
            for (String capability : listener.subscriptions.get(protocol)) {
                if (! supported.get(protocol).contains(capability)) {
                    throw new IllegalArgumentException("server does not support capability: \"" + capability + "\" for protocol: \"" + protocol + "\"");
                }
            }
        }
        
        listeners.add(listener);
        
        this.updateServerSubscriptions();
    }
    
    private void updateServerSubscriptions() throws IOException {
        for (String protocol : subscribed.keySet()) {
            out.write("!0 REMOVE " + protocol + "\r\n");
        }
        out.flush();
        
        subscribed.clear();
        
        for (KismetListener listener : listeners) {
            for (Entry<String, Set<String>> entry : listener.subscriptions.entrySet()) {
                Set<String> capabilities = subscribed.get(entry.getKey());
                if (capabilities == null) {
                    capabilities = new HashSet<String>();
                    subscribed.put(entry.getKey(), capabilities);
                }
                for (String capability : entry.getValue()) {
                    capabilities.add(capability);
                }
            }
        }
        
        for (Entry<String, Set<String>> entry : subscribed.entrySet()) {
            StringBuilder capabilities = new StringBuilder();
            for (String capability : entry.getValue()) {
                if (capabilities.length() > 0) {
                    capabilities.append(',');
                }
                capabilities.append(capability);
            }
            out.write("!0 ENABLE " + entry.getKey() + " " + capabilities.toString() + "\r\n");
        }
        out.flush();
    }
    
    private void parse(String line) throws IOException {
        if (line.startsWith("*KISMET: ") && line.length() > 9) {
            parseKismet(line.substring(9));
        }
        else if (line.startsWith("*ACK: ") && line.length() > 6) {
            parseAck(line.substring(6));
        }
        else if (line.startsWith("*ERROR: ") && line.length() > 8) {
            parseError(line.substring(8));
        }
        else if (line.startsWith("*PROTOCOLS: ") && line.length() > 12) {
            parseProtocols(line.substring(12));
        }
        else if (line.startsWith("*CAPABILITY: ") && line.length() > 13) {
            parseCapabilities(line.substring(13));
        }
        else if (line.startsWith("*TERMINATE: ") && line.length() > 12) {
            parseTerminate(line.substring(12));
        }
        else if (line.startsWith("*") && line.indexOf(':') > 2 && line.length() > line.indexOf(':') + 2) {
            parseProtocol(line.substring(1, line.indexOf(':')), line.substring(line.indexOf(':') + 2));
        }
    }

    private void parseKismet(String kismet) {
        List<String> values = this.split(kismet);
        version = values.get(0);
        startTime = new Date(Long.parseLong(values.get(1)) * 1000);
        serverName = values.get(2);
        build = values.get(3);
    }

    private void parseAck(String ack) {
        // ignored for now (not very robust)
    }

    private void parseError(String error) {
        // ignored for now (not very robust)
    }

    private void parseProtocols(String protocols) throws IOException {
        for (String protocol : protocols.split(",")) {
            supported.put(protocol, null);
            out.write("!0 CAPABILITY " + protocol + "\r\n");
        }
        out.flush();
    }

    private void parseCapabilities(String capabilities) {
        String protocol = capabilities.substring(0, capabilities.indexOf(' '));
        Set<String> set = new HashSet<String>();
        for (String capability : capabilities.substring(capabilities.indexOf(' ') + 1).split(",")) {
            set.add(capability);
        }
        supported.put(protocol, set);
        
        for (Set<String> tmp : supported.values()) {
            if (tmp == null) {
                return;
            }
        }
        
        initialised = true;
    }

    private void parseTerminate(String text) {
        running = false;
        for (KismetListener listener : listeners) {
            listener.onTerminated(text);
        }
    }

    private void parseProtocol(String protocol, String value) {
        KismetMessage message = createMessage(protocol);
        if (message == null) {
            return; // unsupported message
        }
        
        int idx = 0;
        List<String> values = this.split(value);
        
        for (String capability : subscribed.get(protocol)) {
            for (Method method : message.getClass().getMethods()) {
                Capability annotation = (Capability) method.getAnnotation(Capability.class);
                if (annotation != null && capability.equals(annotation.value()) && method.getParameterTypes().length == 1) {
                    try {
                        method.invoke(message, this.coerce(method.getParameterTypes()[0], values.get(idx)));
                        break;
                    } 
                    catch (IllegalAccessException ex) { } 
                    catch (IllegalArgumentException ex) { } 
                    catch (InvocationTargetException ex) { }
                }
            }
            ++idx;
        }
        
        for (KismetListener listener : listeners) {
            if (listener.subscriptions.containsKey(protocol)) {
                listener.onMessage(message);
            }
        }
    }
    
    private Object coerce(Class target, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        if (target.isAssignableFrom(boolean.class) || target.isAssignableFrom(Boolean.class)) {
            if ("1".equals(value) || "true".equalsIgnoreCase(value) || "t".equalsIgnoreCase(value)) {
                return Boolean.TRUE;
            }
            else {
                return Boolean.FALSE;
            }
        }
               
        if (target.isAssignableFrom(double.class) || target.isAssignableFrom(Double.class)) {
            return Double.parseDouble(value);
        }
        
        if (target.isAssignableFrom(byte.class) || target.isAssignableFrom(Byte.class)) {
            return value.getBytes()[0];
        }
        
        if (target.isAssignableFrom(char.class) || target.isAssignableFrom(Character.class)) {
            return value.charAt(0);
        }
        
        if (target.isAssignableFrom(short.class) || target.isAssignableFrom(Short.class)) {
            return Short.parseShort(value);
        }
        
        if (target.isAssignableFrom(int.class) || target.isAssignableFrom(Integer.class)) {
            return Integer.parseInt(value);
        }
        
        if (target.isAssignableFrom(long.class) || target.isAssignableFrom(Long.class)) {
            return Long.parseLong(value);
        }
        
        if (target.isAssignableFrom(float.class) || target.isAssignableFrom(Float.class)) {
            return Float.parseFloat(value);
        }
        
        if (target.isEnum()) {
            Object[] constants = target.getEnumConstants();
            for (int i = 0; i < constants.length; ++i) {
                try {
                    Method valueMethod = constants[i].getClass().getMethod("value");
                    Object tmp = coerce(valueMethod.getReturnType(), value);
                    if (tmp.equals(valueMethod.invoke(constants[i]))) {
                        return constants[i];
                    }
                } 
                catch (NoSuchMethodException ex) { } 
                catch (SecurityException ex) { }
                catch (IllegalAccessException ex) { }
                catch (InvocationTargetException ex) { }
                
                try {
                    if (value.equalsIgnoreCase((String) constants[i].getClass().getMethod("name").invoke(constants[i]))) {
                        return constants[i];
                    }
                } 
                catch (NoSuchMethodException ex) { } 
                catch (SecurityException ex) { }
                catch (IllegalAccessException ex) { }
                catch (InvocationTargetException ex) { }
            }
            
            return constants[Integer.parseInt(value)];
        }
        
        if (target.isAssignableFrom(Date.class)) {
            try {
                return new Date(Long.parseLong(value) * 1000);
            }
            catch (NumberFormatException ex) { }
            
            try {
                return DateFormat.getInstance().parse(value);
            }
            catch (ParseException ex) { }
        }
        
        if (target.isAssignableFrom(InetAddress.class)) {
            try {
                return InetAddress.getByName(value);
            }
            catch (UnknownHostException ex) { }
        }

        return value;
    }
    
    private KismetMessage createMessage(String protocol) { 
        StringBuilder messageClass = new StringBuilder();
        messageClass.append(KismetMessage.class.getPackage().getName());
        messageClass.append('.');
        messageClass.append(Character.toUpperCase(protocol.charAt(0)));
        messageClass.append(protocol.substring(1).toLowerCase());
        messageClass.append("Message");
        try {
            return (KismetMessage) Class.forName(messageClass.toString()).newInstance();
        } 
        catch (InstantiationException ex) { } 
        catch (IllegalAccessException ex) { }
        catch (ClassNotFoundException ex) { }
        catch (ClassCastException ex) { }

        messageClass = new StringBuilder();
        messageClass.append(KismetMessage.class.getPackage().getName());
        messageClass.append('.');
        messageClass.append(protocol);
        messageClass.append("Message");
        try {
            return (KismetMessage) Class.forName(messageClass.toString()).newInstance();
        } 
        catch (InstantiationException ex) { } 
        catch (IllegalAccessException ex) { }
        catch (ClassNotFoundException ex) { }
        catch (ClassCastException ex) { }

        return null;
    }
    
    private List<String> split(String str) {
        List<String> result = new ArrayList<String>();
        boolean delim = false;
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == 1) {
                if (delim) {
                    result.add(current.toString());
                    current.setLength(0);
                    delim = false;
                }
                else {
                    delim = true;
                }
            }
            else if (str.charAt(i) == ' ' && !delim) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
            }
            else {
                current.append(str.charAt(i));
            }
        }
        if (current.length() > 0) {
            result.add(current.toString());
        }
        return result;
    }

}
