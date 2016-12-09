/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cor.cep.subscriber;

import GUI.WarningScreen;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;

public class TemperatureRadiationWarningListener implements UpdateListener{
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
        
        System.out.println("Number of news TpRdWarning "+newData.length);
        
        inEvents.add("WARNING: Room "+newData[0].get("roomId")+" High Temp: "+(Integer)newData[0].get("temp")+"º C "+
                          " followed by High Rad: "+new DecimalFormat("#.###").format((Double)newData[0].get("rad"))+" uSv "+
                          "- at "+new Date((Long)newData[0].get("timeMillisec")));
        System.out.println("Event received: "+ newData[0].getUnderlying());
        
        
        /** Send Events to GUI */
        screen.newWarning(inEvents);
    }
}