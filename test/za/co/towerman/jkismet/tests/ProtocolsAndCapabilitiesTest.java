/**
 * za.co.towerman.jkismet.Protocol
 * Copyright (C) 2012 Edwin Peer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
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
package za.co.towerman.jkismet.tests;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import org.junit.Ignore;
import za.co.towerman.jkismet.message.KismetMessage;
import za.co.towerman.jkismet.KismetListener;
import za.co.towerman.jkismet.Protocol;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import junit.framework.Assert;
import za.co.towerman.jkismet.KismetConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import za.co.towerman.jkismet.Capability;

/**
 *
 * @author simeon
 */
public class ProtocolsAndCapabilitiesTest {
    
    private KismetConnection conn;
    private KismetListener listener;
    private Map<String, Class<KismetMessage>> pmap;
    private static final Set<String> internalProtocols = new HashSet<String>(Arrays.asList(        
         new String[] {"KISMET","ACK","ERROR","PROTOCOLS","CAPABILITY","TERMINATE","REMOVE"}
    ));
    
    public ProtocolsAndCapabilitiesTest() {
    }

//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
    
    @Before
    public void setUp() throws IOException, ClassNotFoundException {
        conn = new KismetConnection("localhost",2501);
        pmap = new HashMap<String, Class<KismetMessage>>();
        
        // Build a map of protocol names to KismetMessage classes
        for (Class<KismetMessage> pClass: getClasses("za.co.towerman.jkismet.message")) {
            if (pClass.isAnnotationPresent(Protocol.class))  {
                String protocolName = pClass.getAnnotation(Protocol.class).value();
                pmap.put(protocolName, pClass);
                System.out.println(protocolName+" -> "+pClass.getName());
            }
        }
     
        
        listener = new KismetListener() {
        
            @Override
            public void onMessage(KismetMessage message) {
                System.out.println(message);
            }

            @Override
            public void onTerminated(String reason) {
                System.out.println("Connection terminated by server: " + reason);
            }
        };
        
    }
    
    @After
    public void tearDown() {
    }
    
    // Lifted from http://dzone.com/snippets/get-all-classes-within-package
    @Ignore
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    // Lifted from http://dzone.com/snippets/get-all-classes-within-package
    @Ignore
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
    
    // Lifted from http://snippets.dzone.com/posts/show/91
    @Ignore
    static String join(Collection<?> s, String delimiter) {
        StringBuilder builder = new StringBuilder();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append(delimiter);
        }
        return builder.toString();
    }
    
    @Ignore
    static Set<String> getMessageClassCapabilities(Class<KismetMessage> messageClass) {
        Set<String> set = new HashSet<String>();
        for (Method method : messageClass.getMethods()) {
            String mName = method.getName();
            Capability capability = method.getAnnotation(Capability.class);
            if (capability != null && mName.startsWith("set")) {
                set.add(mName.substring(3));
            }
        }
        return set;
    }

    /**
     * This checks for each protocol Kismet claims to support, whether there exists a
     * matching built-in message classes in the za.co.towerman.jkismet.message package.
     * 
     * @throws IOException 
     */
    @Test
    public void checkUnsupportedProtocols() throws IOException {
        for (String protocolName : conn.getSupportedProtocols()) {
            Class<KismetMessage> protocolClass = pmap.get(protocolName);
            String capabilites = join(conn.getSupportedCapabilities(protocolName),",");            
            if (protocolClass==null && !internalProtocols.contains(protocolName)) {
                Assert.fail(protocolName+" is unsupported (Kismet capabilties are: "+capabilites+")");
            }
        }
    }
    /**
     * This test attempts to subscribe to each capability of the built-in message
     * classes in the za.co.towerman.jkismet.message package.
     * 
     * @throws IOException 
     */
    @Test
    public void subscribeToSupportedProtocols() throws IOException {
          for (Entry<String, Class<KismetMessage>> entry: pmap.entrySet()) {
            Class<KismetMessage> messageClass = entry.getValue();
            String protocolName = entry.getKey();
            String messageCapabilites = join(getMessageClassCapabilities(messageClass),",");
            try {
                listener.subscribe(messageClass, messageCapabilites);
            } catch (Exception ex) {
                ex.printStackTrace();
                Assert.fail("Subscribing to "+protocolName+" failed: "+ex.toString());
            }
          }
    }    
}