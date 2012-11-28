/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.Map.Entry;
import junit.framework.Assert;
import za.co.towerman.jkismet.KismetConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author simeon
 */
public class AllCapabilitiesTest {
    
    private KismetConnection conn;
    private KismetListener listener;
    private Map<String, Class<KismetMessage>> pmap;
    
    public AllCapabilitiesTest() {
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
    
    @Test
    public void subscribeToEverything() throws IOException {
        for (String protocolName : conn.getSupportedProtocols()) {
            System.out.println("Looking for KismetMessage to match "+protocolName);
            Class<KismetMessage> protocolClass = pmap.get(protocolName);
            String capabilites = join(conn.getSupportedCapabilities(protocolName),",");            
            if (protocolClass==null) {
                System.out.println("Missing KismetMessage for "+protocolName);
                System.out.println("Capability for "+protocolName+" is: "+capabilites);
            }
            Assert.assertNotNull(protocolClass);
            listener.subscribe(protocolClass, capabilites);
        }
    }
    
    @Test
    public void subscribeToSupportedProtocols() throws IOException {
          for (Entry<String, Class<KismetMessage>> entry: pmap.entrySet()) {
            Class<KismetMessage> protocolClass = entry.getValue();
            String protocolName = entry.getKey();
            String capabilites = join(conn.getSupportedCapabilities(protocolName),",");            
            listener.subscribe(protocolClass, capabilites);
          }
    }    
}