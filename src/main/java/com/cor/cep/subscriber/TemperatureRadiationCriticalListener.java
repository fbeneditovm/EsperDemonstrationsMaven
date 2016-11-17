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

public  class TemperatureRadiationCriticalListener implements UpdateListener{
    WarningScreen screen;
    LinkedList<String> inEvents;
    
    public void setScreen(WarningScreen screen){
        this.screen = screen;
    }
    
    @Override
    public void update(EventBean[] newData, EventBean[] oldData) {
        inEvents = new LinkedList<String>();
        
        System.out.println("Number of news TpRdCritical "+newData.length);
        
        inEvents.add("CRITICAL: Room "+newData[0].get("roomId")+" VERY High Temp: "+(Integer)newData[0].get("temp")+" º C "+
                          " followed by VERY High Rad: "+new DecimalFormat("#.###").format((Double)newData[0].get("rad"))+" uSv "+
                          "- at "+new Date((Long)newData[0].get("timeMillisec")));
        System.out.println("Event received: "+ newData[0].getUnderlying());
        
        
        //Send Events to GUI
        screen.newWarning(inEvents);
    }
}