package za.co.towerman.jkismet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import za.co.towerman.jkismet.message.KismetMessage;

/**
 *
 * @author espeer
 */
public abstract class KismetListener {
    Map<String, Set<String>> subscriptions = new HashMap<String, Set<String>>();
    
    public void subscribe(Class messageType, String fields) {
        String protocol = null;
        try {
            protocol = (String) messageType.getField("PROTOCOL").get(null);
        }
        catch (NoSuchFieldException ex) {
            throw new IllegalArgumentException("invalid message type");
        }
        catch (IllegalAccessException ex) { 
            throw new IllegalArgumentException("invalid message type");
        }
        catch (ClassCastException ex) {
            throw new IllegalArgumentException("invalid message type");
        }
        
        if (subscriptions.get(protocol) == null) {
            subscriptions.put(protocol, new HashSet<String>());
        }
        
        for (String field : fields.split(",")) {
            field = field.trim();
            
            Capability capability = this.findCapability(messageType, field);
            if (capability == null) {
                throw new IllegalArgumentException("invalid field: " + field);
            }
            
            subscriptions.get(protocol).add(capability.value());
        }
    }
    
    private Capability findCapability(Class target, String field) {
        for (Method method : target.getMethods()) {
            Capability capability = method.getAnnotation(Capability.class);
            if (capability != null && method.getName().equalsIgnoreCase("set" + field)) {
                return capability;
            }
        }
        
        return null;
    }
    
    public abstract void onMessage(KismetMessage message);
    public abstract void onTerminated(String reason);
}
