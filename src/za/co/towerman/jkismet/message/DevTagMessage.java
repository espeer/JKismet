/**
 * za.co.towerman.jkismet.KismetConnection
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
import za.co.towerman.jkismet.Protocol;

/**
 *
 * @author simeon
 */
@Protocol("DEVTAG")
public class DevTagMessage implements KismetMessage {
    private String macaddr;
    private String phytype; // FIXME: should this be an Enum?... Look at the data
    private String tag;
    private String value;

    /**
     * @return the macaddr
     */
    public String getMacaddr() {
        return macaddr;
    }

    /**
     * @param macaddr the macaddr to set
     */
    @Capability("macaddr")
    public void setMacaddr(String macaddr) {
        this.macaddr = macaddr;
    }

    /**
     * @return the phytype
     */
    public String getPhytype() {
        return phytype;
    }

    /**
     * @param phytype the phytype to set
     */
    @Capability("phytype")
    public void setPhytype(String phytype) {
        this.phytype = phytype;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    @Capability("value")
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    @Capability("tag")
    public void setTag(String tag) {
        this.tag = tag;
    }
}
