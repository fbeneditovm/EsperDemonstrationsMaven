package com.cor.cep.subscriber;

import GUI.WarningScreen;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Update Listener that gets the RadiationEvents 5 by 5.
 * @author fbeneditovm
 */
public class RadiationCriticalListener implements UpdateListener{
    
    WarningScreen screen; //The Warning Screen used to display the Events
    LinkedList<String> inEvents; //Stores the new events received (InsertStream)
    
    /**
     * Sets the Warning Screen
     * @param screen the warning Screen used to display the Events
     */
    public void setScreen(WarningScreen screen){
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
        
        /** Store Events in Arrays */
        for(int i=0; i<newData.length; i++){
            inEvents.add("CRITICAL: Room: "+newData[i].get("roomId")+" Average Radiation: "+new DecimalFormat("#.###").format((Double)newData[i].get("avgRd"))+" uSv "+
                          "- at "+(Date)newData[i].get("timeOfReading"));
            for(String buffer : inEvents){
                System.out.println(buffer);
            }
        }
        
        /** Send Events to GUI */
        screen.newCritical(inEvents);
    }
}