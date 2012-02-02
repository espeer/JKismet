/**
 * za.co.towerman.jkismet.message.StatusMessage
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

import java.util.EnumSet;
import java.util.Set;
import za.co.towerman.jkismet.Capability;
import za.co.towerman.jkismet.message.BSSIDMessage.CryptoType;

/**
 *
 * @author espeer
 */
public class StatusMessage extends KismetMessage {

    public static final String PROTOCOL = "STATUS";
    
    public enum MessageFlag {
        DEBUG,
        INFO,
        ERROR,
        ALERT,
        FATAL,
        LOCAL,
        PRINT
    }
    
    private Set<MessageFlag> flags;
    private String text;

    public Set<MessageFlag> getFlags() {
        return flags;
    }
    
    @Capability("flags")
    public void setFlags(int flags) {
        this.flags = EnumSet.noneOf(MessageFlag.class);
        for (int i = 1; i < MessageFlag.values().length; ++i) {
            if (((flags >>> i) & 0x01) > 0) {
                this.flags.add(MessageFlag.values()[i]);
            }
        }
    }

    public String getText() {
        return text;
    }

    @Capability("text")
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "StatusMessage{" + "flags=" + flags + ", text=" + text + '}';
    }
    
}
