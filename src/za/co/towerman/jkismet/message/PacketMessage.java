/**
 * za.co.towerman.jkismet.message.PacketMessage
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
package za.co.towerman.jkismet.message;

import za.co.towerman.jkismet.Capability;

/**
 *
 * @author espeer
 */
public class PacketMessage {
    public static final String PROTOCOL = "PACKET";
    
    public PacketMessage() {
        System.out.println("Created PacketMessage");
    }
    
    public enum Type implements ValueEnum {
        NOISE(-2),
        UNKNOWN(-1),
        MANAGEMENT(0),
        PHY(1),
        DATA(2);
                
        private int value;
        
        private Type(int value) {
            this.value = value;
        }
        
        @Override
        public int value() {
            return value;
        }
    }
    
    public enum ManagementType implements ValueEnum {
        ASSOCIATION_REQUEST(0),
        ASSOCIATION_RESPONSE(1),
        REASSOCIATION_REQUEST(2),
        REASSOCIATION_RESPONSE(3),
        PROBE_REQUEST(4),
        PROBE_RESPONSE(5),
        BEACON(8),
        ATIM(9),        
        DISACCOCIATION(10),
        AUTHENTICATION(11),
        DEAUTHENTICATION(12);
        
        private int value;
        
        private ManagementType(int value) {
            this.value = value;
        }
        
        @Override
        public int value() {
            return value;
        }
    }
    
    public enum PhyType implements ValueEnum {
        PS_POLL(10),
        RTS(11),
        CTS(12),
        ACK(13),
        CF_END(14),
        CF_END_ACK(15);
        
        private int value;
        
        private PhyType(int value) {
            this.value = value;
        }
        
        @Override
        public int value() {
            return value;
        }
    }
    
    public enum DataType implements ValueEnum {
        DATA(0),
        DATA_CF_ACK(1),
        DATA_CF_POLL(2),
        DATA_CF_ACK_POLL(3),
        NULL(4),
        NULL_CF_ACK(5),
        NULL_CF_ACK_POLL(6),
        QOS_DATA(8),
        QOS_DATA_CF_ACK(9),
        QOS_DATA_CF_POLL(10),
        QOS_DATA_CF_ACK_POLL(11),
        QOS_NULL(12),
        QOS_NULL_CF_POLL(14),
        QOS_NULL_CF_ACK_POLL(15);
        
        private int value;
        
        private DataType(int value) {
            this.value = value;
        }
        
        public int value() {
            return value;
        }
    }
    
    private Type type;
    private int subType;
    
    public Type getType() {
        return type;
    }

    @Capability("type")
    public void setType(Type type) {
        this.type = type;
    }

    public Enum getSubType() {
        Enum[] values = null;
        switch (type) {
            case MANAGEMENT: { values = ManagementType.values(); break; }
            case DATA: { values = DataType.values(); break; }
            case PHY: { values = PhyType.values(); break; }
        }
        
        for (Enum value : values) {
            if (subType == ((ValueEnum) value).value()) {
                return value;
            }
        }
        
        return null;
    }

    @Capability("subtype")
    public void setSubType(int subType) {
        this.subType = subType;
    }
    
    
}
