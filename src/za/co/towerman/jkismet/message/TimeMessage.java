package za.co.towerman.jkismet.message;

import java.util.Date;
import za.co.towerman.jkismet.Capability;

/**
 *
 * @author espeer
 */
public class TimeMessage extends KismetMessage {

    public static final String PROTOCOL = "TIME";
    
    private Date time;
    
    @Capability("timesec")
    public void setTime(Date time) {
        this.time = time;
    }
    
    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TimeMessage{" + "time=" + time + '}';
    }
    
}
