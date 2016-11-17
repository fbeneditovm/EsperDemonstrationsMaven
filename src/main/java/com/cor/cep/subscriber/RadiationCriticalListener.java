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

public class RadiationCriticalListener implements UpdateListener{
    
    WarningScreen screen;
    LinkedList<String> inEvents;
    
    public void setScreen(WarningScreen screen){
        this.screen = screen;
    }
    
    @Override
    public void update(EventBean[] newData, EventBean[] oldData) {
        inEvents = new LinkedList<String>();
        
        System.out.println("Number of news RdCritical "+newData.length);
        
        //Store Events in Arrays
        for(int i=0; i<newData.length; i++){
            if(newData[i]==null){
                System.out.println("We got a null");
                break;
            }
            inEvents.add("CRITICAL: "+newData[i].get("roomId")+" Average Radiation: "+new DecimalFormat("#.###").format((Double)newData[i].get("avgRd"))+" uSv "+
                          "- at "+(Date)newData[i].get("timeOfReading"));
            System.out.println("Event received: "+ newData[i].getUnderlying());
        }
        
        //Send Events to GUI
        screen.newCritical(inEvents);
    }
}