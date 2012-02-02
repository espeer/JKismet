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

import java.net.InetAddress;
import java.util.Date;
import za.co.towerman.jkismet.Capability;

/**
 *
 * @author espeer
 */
public class PacketMessage extends KismetMessage {
    
    public static final String PROTOCOL = "PACKET";
    
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
    private Date time;
    private boolean encrypted;
    private boolean weakIV;
    private int beaconRate;
    private String sourceMac;
    private InetAddress sourceIp;
    private int sourcePort;
    private String destinationMac;
    private InetAddress destinationIp;
    private int destinationPort;
    private String bssid;
    private String ssid;
    private String prototype;
    private String sourceName;
    private int netbiosType;
    private String netbiosSource;
    
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

    public Date getTime() {
        return time;
    }

    @Capability("timesec")
    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    @Capability("encrypted")
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public boolean isWeakIV() {
        return weakIV;
    }

    @Capability("weak")
    public void setWeakIV(boolean weakIV) {
        this.weakIV = weakIV;
    }

    public int getBeaconRate() {
        return beaconRate;
    }

    @Capability("beaconrate")
    public void setBeaconRate(int beaconRate) {
        this.beaconRate = beaconRate;
    }

    public String getBssid() {
        return bssid;
    }

    @Capability("bssid")
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getDestinationMac() {
        return destinationMac;
    }

    @Capability("destmac")
    public void setDestinationMac(String destinationMac) {
        this.destinationMac = destinationMac;
    }

    public String getSourceMac() {
        return sourceMac;
    }

    @Capability("sourcemac")
    public void setSourceMac(String sourceMac) {
        this.sourceMac = sourceMac;
    }

    public String getSsid() {
        return ssid;
    }

    @Capability("ssid")
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public InetAddress getDestinationIp() {
        return destinationIp;
    }

    @Capability("destip")
    public void setDestinationIp(InetAddress destinationIp) {
        this.destinationIp = destinationIp;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    @Capability("destport")
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public InetAddress getSourceIp() {
        return sourceIp;
    }

    @Capability("sourceip")
    public void setSourceIp(InetAddress sourceIp) {
        this.sourceIp = sourceIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    @Capability("sourceport")
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getNetbiosSource() {
        return netbiosSource;
    }

    @Capability("nbsource")
    public void setNetbiosSource(String netbiosSource) {
        this.netbiosSource = netbiosSource;
    }

    public int getNetbiosType() {
        return netbiosType;
    }

    @Capability("nbtype")
    public void setNetbiosType(int netbiosType) {
        this.netbiosType = netbiosType;
    }

    public String getPrototype() {
        return prototype;
    }

    @Capability("protoype")
    public void setPrototype(String prototype) {
        this.prototype = prototype;
    }

    public String getSourceName() {
        return sourceName;
    }

    @Capability("sourcename")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public String toString() {
        return "PacketMessage{" + "type=" + type + ", subType=" + subType + ", time=" + time + ", encrypted=" + encrypted + ", weakIV=" + weakIV + ", beaconRate=" + beaconRate + ", sourceMac=" + sourceMac + ", sourceIp=" + sourceIp + ", sourcePort=" + sourcePort + ", destinationMac=" + destinationMac + ", destinationIp=" + destinationIp + ", destinationPort=" + destinationPort + ", bssid=" + bssid + ", ssid=" + ssid + ", prototype=" + prototype + ", sourceName=" + sourceName + ", netbiosType=" + netbiosType + ", netbiosSource=" + netbiosSource + '}';
    }
    
}
