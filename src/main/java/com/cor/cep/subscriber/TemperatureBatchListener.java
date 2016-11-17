/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cor.cep.subscriber;

import GUI.EventLogScreen;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import java.util.Date;
import java.util.LinkedList;

/**
 * Update Listener that gets the TemperatureEvents 5 by 5
 */
public class TemperatureBatchListener implements UpdateListener{
    
    EventLogScreen screen;
    LinkedList<String> inEvents;
    LinkedList<String> rmEvents;
    
    public void setScreen(EventLogScreen screen){
        this.screen = screen;
    }
    
    @Override
    public void update(EventBean[] newData, EventBean[] oldData) {
        inEvents = new LinkedList<String>();
        rmEvents = new LinkedList<String>();
        
        System.out.println("Number of news"+newData.length);
        
        if(oldData == null)
            System.out.println("No old Events");
        else
            System.out.println("Number of olds"+oldData.length);
        
        
        //Store Events in Arrays
        for(int i=0; i<newData.length; i++){
            if(newData[i]==null){
                System.out.println("We got a null");
                break;
            }
            inEvents.add("Temperature: "+(Integer)newData[i].get("temperature")+"º C "+
                          "- at Room "+newData[i].get("roomId")+" "+(Date)newData[i].get("timeOfReading"));
            System.out.println("Event received: "+ newData[i].getUnderlying());
        }
        if(oldData != null){
            for(int i=0; i<oldData.length; i++){
                rmEvents.add("Temperature: "+(Integer)oldData[i].get("temperature")+"º C "+
                             "- at Room "+newData[i].get("roomId")+" "+(Date)newData[i].get("timeOfReading"));
            }
        }
        
        //Send Events to GUI
        screen.newEvents(inEvents, rmEvents);
    }
}
