/**
 * za.co.towerman.jkismet.JKismet
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
import za.co.towerman.jkismet.message.AlertMessage;
import za.co.towerman.jkismet.message.BSSIDSourceMessage;
import za.co.towerman.jkismet.message.InfoMessage;
import za.co.towerman.jkismet.message.KismetMessage;
import za.co.towerman.jkismet.message.StatusMessage;
import za.co.towerman.jkismet.message.TimeMessage;

/**
 *
 * @author espeer
 */
public class JKismet {

    public static void main(String[] args) throws IOException {
        KismetConnection conn = new KismetConnection(args[0], Integer.parseInt(args[1]));
        
        System.out.println("Server: " + conn.getServerName() + " Started: " + conn.getStartTime());
        
        KismetListener listener = new KismetListener() {

            @Override
            public void onMessage(KismetMessage message) {
                System.out.println(message);
            }

            @Override
            public void onTerminated(String reason) {
                System.out.println("Connection terminated by server: " + reason);
            }
        };
        
        listener.subscribe(TimeMessage.class, "time");
        // listener.subscribe(BatteryMessage.class, "percentage, mainsPowered, charging, remainingSeconds");
        listener.subscribe(StatusMessage.class, "flags, text");
        listener.subscribe(AlertMessage.class, "time, timeMicroseconds, header, bssid, source, destination, other, channel, text");
        listener.subscribe(InfoMessage.class, "networks, packets, noise, droppedPackets, filteredPackets");
        listener.subscribe(BSSIDSourceMessage.class, "mac, packets, uuid, lastTime");
        //listener.subscribe(SourceMessage.class, "iface,type,user,channel,channels,uuid,packets,hop,velocity,dwell,hopTimeSeconds,hopTimeMicroSeconds,error,warning");
        //listener.subscribe(PluginMessage.class, "name, description, version, file, unloadable, root");
        //listener.subscribe(SSIDMessage.class, "name, mac, type, checksum, beaconInfo, cryptographies, cloaked, firstTime, lastTime, maxRate, packets, beacons, channels");
        //listener.subscribe(BSSIDMessage.class, "mac, channel, frequencies, networkType, addressType, dataBytes, carriers, encodings, cryptographies");
        //listener.subscribe(PacketMessage.class, "type, subType");
        
        conn.register(listener);
        
    }
}
