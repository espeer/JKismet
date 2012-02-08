/**
 * za.co.towerman.jkismet.message.InfoMessage
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
public class InfoMessage extends KismetMessage {
    
    private int networks;
    private int packets;
    private int encryptedPackets;
    private int noise;
    private int droppedPackets;
    private int packetRate;
    private int filteredPackets;
    private int clients;
    private int llcPackets;
    private int dataPackets;
    private int sources;
    private int errorSources;

    public int getClients() {
        return clients;
    }

    @Capability("clients")
    public void setClients(int clients) {
        this.clients = clients;
    }

    public int getDataPackets() {
        return dataPackets;
    }

    @Capability("datapackets")
    public void setDataPackets(int dataPackets) {
        this.dataPackets = dataPackets;
    }

    public int getDroppedPackets() {
        return droppedPackets;
    }

    @Capability("dropped")
    public void setDroppedPackets(int droppedPackets) {
        this.droppedPackets = droppedPackets;
    }

    public int getEncryptedPackets() {
        return encryptedPackets;
    }

    @Capability("crypt")
    public void setEncryptedPackets(int encryptedPackets) {
        this.encryptedPackets = encryptedPackets;
    }

    public int getErrorSources() {
        return errorSources;
    }

    @Capability("numerrorsources")
    public void setErrorSources(int errorSources) {
        this.errorSources = errorSources;
    }

    public int getFilteredPackets() {
        return filteredPackets;
    }

    @Capability("filtered")
    public void setFilteredPackets(int filteredPackets) {
        this.filteredPackets = filteredPackets;
    }

    public int getLlcPackets() {
        return llcPackets;
    }

    @Capability("llcpackets")
    public void setLlcPackets(int llcPackets) {
        this.llcPackets = llcPackets;
    }

    public int getNetworks() {
        return networks;
    }

    @Capability("networks")
    public void setNetworks(int networks) {
        this.networks = networks;
    }

    public int getNoise() {
        return noise;
    }

    @Capability("noise")
    public void setNoise(int noise) {
        this.noise = noise;
    }

    public int getPacketRate() {
        return packetRate;
    }

    @Capability("rate")
    public void setPacketRate(int packetRate) {
        this.packetRate = packetRate;
    }

    public int getPackets() {
        return packets;
    }

    @Capability("packets")
    public void setPackets(int packets) {
        this.packets = packets;
    }

    public int getSources() {
        return sources;
    }

    @Capability("numsources")
    public void setSources(int sources) {
        this.sources = sources;
    }

    @Override
    public String toString() {
        return "InfoMessage{" + "networks=" + networks + ", packets=" + packets + ", encryptedPackets=" + encryptedPackets + ", noise=" + noise + ", droppedPackets=" + droppedPackets + ", packetRate=" + packetRate + ", filteredPackets=" + filteredPackets + ", clients=" + clients + ", llcPackets=" + llcPackets + ", dataPackets=" + dataPackets + ", sources=" + sources + ", errorSources=" + errorSources + '}';
    }
          
    
    
}
