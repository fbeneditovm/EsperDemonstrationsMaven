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
 *
 * @author fbeneditovm
 */
public class TemperatureWarningListener implements UpdateListener{
    
    WarningScreen screen;
    LinkedList<String> inEvents;
    
    public void setScreen(WarningScreen screen){
        this.screen = screen;
    }
    
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
            inEvents.add("WARNING: Room "+newData[i].get("room")+" Temperature levels are too high: "+new DecimalFormat("#.###").format((Integer)newData[i].get("tempK"))+" K "+
                          "- at "+(Date)newData[i].get("timeOfReading"));
            System.out.println("Event received: "+ newData[i].getUnderlying());
        }
        
        //Send Events to GUI
        screen.newWarning(inEvents);
    }
}
