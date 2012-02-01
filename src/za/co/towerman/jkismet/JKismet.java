package za.co.towerman.jkismet;

import java.io.IOException;
import za.co.towerman.jkismet.message.BSSIDMessage;
import za.co.towerman.jkismet.message.KismetMessage;
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
        listener.subscribe(BSSIDMessage.class, "mac, channel, frequencies, networkType, dataBytes, carriers, encodings, cryptographies");
        
        conn.register(listener);
        
    }
}
