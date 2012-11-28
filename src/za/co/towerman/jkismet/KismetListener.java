/**
 * za.co.towerman.jkismet.KismetListener
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
package za.co.towerman.jkismet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import za.co.towerman.jkismet.message.KismetMessage;

/**
 *
 * @author espeer
 */
public abstract class KismetListener {
    KismetConnection connection = null;
    Map<String, Set<Class>> subscriptions = new HashMap<String, Set<Class>>();
    Map<Class, Set<String>> capabilities = new HashMap<Class, Set<String>>();
    
    public void subscribe(Class messageType, String fields) throws IOException {
        if (! KismetMessage.class.isAssignableFrom(messageType)) {
            throw new IllegalArgumentException("invalid message type: must implement KismetMessage interface");
        }
        
        Protocol protocol = (Protocol) messageType.getAnnotation(Protocol.class);
        
        if (protocol == null) {
            throw new IllegalArgumentException("invalid message type: must declare protocol via annotation");
        }
        
        if (connection != null) {
            this.checkServerSupport(connection);
        }
        
        Set<Class> messageSet = subscriptions.get(protocol.value());
        
        if (messageSet == null) {
            messageSet = new HashSet<Class>();
            subscriptions.put(protocol.value(), messageSet);
        }
        
        messageSet.add(messageType);
        
        Set<String> fieldSet = capabilities.get(messageType);
        
        if (fieldSet == null) {
            fieldSet = new HashSet<String>();
            capabilities.put(messageType, fieldSet);
        }
        
        for (String field : fields.split(",")) {
            field = field.trim();
            
            Capability capability = this.findCapability(messageType, field);
            
            if (capability == null) {
                throw new IllegalArgumentException("invalid field: " + field);
            }
            
            fieldSet.add(capability.value());
        }
        
        if (connection != null) {
            connection.updateServerSubscriptions();
        }
    }
    
    private Capability findCapability(Class target, String field) {
        for (Method method : target.getMethods()) {
            Capability capability = method.getAnnotation(Capability.class);
            if (capability != null) {
                return capability;
            }
        }
        
        return null;
    }

    void checkServerSupport(KismetConnection c) {
        for (Entry<String, Set<Class>> entry : subscriptions.entrySet()) {  
            if (! c.getSupportedProtocols().contains(entry.getKey())) {
                throw new IllegalArgumentException("server does not support protocol: " + entry.getKey());
            }
            
            for (Class messageType : entry.getValue()) {
                for (String capability : capabilities.get(messageType)) {
                    if (!  c.getSupportedCapabilities(entry.getKey()).contains(capability)) {
                        throw new IllegalArgumentException("server does not support capability: \"" + capability + "\" for protocol: \"" + entry.getKey() + "\"");
                    }
                }
            }
        }
    }
    
    public abstract void onMessage(KismetMessage message);
    public abstract void onTerminated(String reason);
}
