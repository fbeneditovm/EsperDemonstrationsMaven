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

/**
 * Update Listener that gets the RadiationEvents 5 by 5.
 * @author fbeneditovm
 */
public class TemperatureWarningListener implements UpdateListener{
    
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
        
        System.out.println("Number of news RdWarning "+newData.length);
        
        //Store Events in Arrays
        for(int i=0; i<newData.length; i++){
            if(newData[i]==null){
                System.out.println("We got a null");
                break;
            }
            inEvents.add("WARNING: Room "+newData[i].get("room")+" Temperature levels are too high: "+new DecimalFormat("#.###").format((Integer)newData[i].get("temp"))+" K "+
                          "- at "+(Date)newData[i].get("timeOfReading"));
            System.out.println("Event received: "+ newData[i].getUnderlying());
        }
        
        /** Send Events to GUI */
        screen.newWarning(inEvents);
    }
}
