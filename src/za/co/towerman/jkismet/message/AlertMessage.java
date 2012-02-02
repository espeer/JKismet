/**
 * za.co.towerman.jkismet.message.KismetMessage
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

import java.util.Date;
import za.co.towerman.jkismet.Capability;

/**
 *
 * @author espeer
 */
public class AlertMessage extends KismetMessage {
    
    private Date time;
    private int timeMicroseconds;
    private String header;
    private String bssid;
    private String source;
    private String destination;
    private String other;
    private int channel;
    private String text;

    public String getBssid() {
        return bssid;
    }

    @Capability("bssid")
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public int getChannel() {
        return channel;
    }
    
    @Capability("channel")
    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getDestination() {
        return destination;
    }

    @Capability("dest")
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getHeader() {
        return header;
    }

    @Capability("header")
    public void setHeader(String header) {
        this.header = header;
    }

    public String getOther() {
        return other;
    }

    @Capability("other")
    public void setOther(String other) {
        this.other = other;
    }

    public String getSource() {
        return source;
    }

    @Capability("source")
    public void setSource(String source) {
        this.source = source;
    }

    public String getText() {
        return text;
    }

    @Capability("text")
    public void setText(String text) {
        this.text = text;
    }

    public Date getTime() {
        return time;
    }

    @Capability("sec")
    public void setTime(Date time) {
        this.time = time;
    }

    public int getTimeMicroseconds() {
        return timeMicroseconds;
    }

    @Capability("usec")
    public void setTimeMicroseconds(int timeMicroseconds) {
        this.timeMicroseconds = timeMicroseconds;
    }

    @Override
    public String toString() {
        return "AlertMessage{" + "time=" + time + ", timeMicroseconds=" + timeMicroseconds + ", header=" + header + ", bssid=" + bssid + ", source=" + source + ", destination=" + destination + ", other=" + other + ", channel=" + channel + ", text=" + text + '}';
    }
 
    
}
