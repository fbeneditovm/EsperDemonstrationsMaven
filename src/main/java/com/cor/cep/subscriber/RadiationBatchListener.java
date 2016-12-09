package com.cor.cep.subscriber;

import GUI.EventLogScreen;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Update Listener that gets the RadiationEvents 5 by 5.
 * @author fbeneditovm
 */
public class RadiationBatchListener implements UpdateListener{
    
    EventLogScreen screen; //The log Screen used to display the Events
    LinkedList<String> inEvents; //Stores the new events received (InsertStream)
    LinkedList<String> rmEvents; //Stores the old events received (RemoveStream)
    
    /**
     * Sets the Log Screen
     * @param screen the log Screen used to display the Events
     */
    public void setScreen(EventLogScreen screen){
        this.screen = screen;
    }
    
    /**
     * The update method that receives the new and old events
     * @param newData An event bean with the new events
     * @param oldData An event bean with the old events
     */
    @Override
    public void update(EventBean[] newData, EventBean[] oldData) {
        inEvents = new LinkedList<String>();
        rmEvents = new LinkedList<String>();
        
        /** Store Events in Arrays */
        for(int i=0; i<newData.length; i++){
            if(newData[i]==null){
                System.out.println("We got a null");
                break;
            }
            inEvents.add("Radiation: "+new DecimalFormat("#.###").format((Double)newData[i].get("radiation"))+" uSv "+
                          "- at Room "+newData[i].get("roomId")+" "+(Date)newData[i].get("timeOfReading"));
            System.out.println("Event received: "+ newData[i].getUnderlying());
        }
        if(oldData != null){
            for(int i=0; i<oldData.length; i++){
                rmEvents.add("Radiation: "+new DecimalFormat("#.###").format((Double)oldData[i].get("radiation"))+" uSv "+
                             "- at Room "+newData[i].get("roomId")+" "+(Date)newData[i].get("timeOfReading"));
            }
        }
        
        /** Send Events to GUI */
        screen.newEvents(inEvents, rmEvents);
    }
}
