/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cor.cep.util;

/**
 *
 * @author fbeneditovm
 */

public class EPLQueries {
    
    private static final String CRITICAL_EVENT_THRESHOLD = "100";
    private static final String CRITICAL_EVENT_MULTIPLIER = "1.5";
    private static final String WARNING_EVENT_THRESHOLD = "400";
    
    public static String createCtxRadSegmentedByRoom(){
        return "create context CtxRadSegmentedByRoom "
                + "partition by roomId from RadiationEvent";
    }
    
    public static String createCtxTempSegmentedByRoom(){
        return "create context CtxTempSegmentedByRoom "
                + "partition by roomId from TemperatureEvent";
    }
    
    public static String createCtx20secAfterTemperature(){
        return "create context Ctx20secAfterTemperature" +
            "  initiated by TemperatureEvent(temperature>300) as tp" +
            "  terminated after 20 seconds";
    }
    
    public static String getLast5Radiation(){
        return "select irstream * from " +
               "RadiationEvent.win:length(5)";
    }
    
    public static String getBatch5Radiation(){
        return "select irstream * from " +
               "RadiationEvent.win:length_batch(5)";
    }
    
    public static String getLast5Temperature(){
        return "select irstream * from " +
               "TemperatureEvent.win:length(5)";
    }
    
    public static String getBatch5Temperature(){
        return "select irstream * from " +
               "TemperatureEvent.win:length_batch(5)";
    }
    /**
     * @Selects: Temperature in Kelvin, roomId, Date with the time of reading
     * @When: Temperature in Kelvin is above 500
     * @Uses: method invocation to convert celsius to Kelvin
     * @return 
     */
    public static String warningTemperature(){
        return "select temperatures.kelvin as tempK, tempEvt.roomId as room, tempEvt.timeOfReading as timeOfReading from " +
               "TemperatureEvent.std:lastevent() as tempEvt, "
                + "method:com.cor.cep.util.Temperatures.getFromC(temperature) as temperatures "
                + "where temperatures.kelvin>500";
    }
    /**
     * @Selects: Average Radiation, roomName, Date with the time of reading
     * @When: In a window of 10 sec more than 2 events have radiation above 5
     * @Uses: database data retrieval
     */
    public static String criticalRadiation(){
        return    "context CtxRadSegmentedByRoom "
                + "\nselect avg(RadEvt.radiation) as avgRd, "
                    + "\nRadEvt.timeOfReading as timeOfReading, "
                    + "\npsql.roomName as roomName "
                + "\nfrom RadiationEvent.win:time(10 sec) as RadEvt, "
                + "\nsql:Postgresql[' SELECT rTb.\"roomName\""
                                      + "\nFROM \"public\".\"Room\" as rTb "
                                      + "\nWHERE rTb.\"roomId\" = ${RadEvt.roomId} '] as psql"
                + "\nwhere radiation>5 "
                + "\nhaving count(*)>2";
    }
    
    public static String warningRadiation(){
        return  "context CtxRadSegmentedByRoom "
                +"\nselect context.key1 as roomId, "
                      + "\nRadEvt.radiation as radiation, "
                      + "\nRadEvt.timeOfReading as timeOfReading, "
                      + "\npsql.roomName as roomName "
                + "\nfrom RadiationEvent.win:length_batch(5) as RadEvt, "
                + "\nsql:Postgresql[' SELECT rTb.\"roomName\""
                                      + "\nFROM \"public\".\"Room\" as rTb "
                                      + "\nWHERE rTb.\"roomId\" = ${RadEvt.roomId} '] as psql"
                + "\nwhere radiation>4";
    }
    
    public static String criticalTemperatureRadiation(){
        return "context Ctx20secAfterTemperature "
                + "select context.tp.roomId as roomId, "
                       + "context.tp.temperature as temp, "
                       + "context.startTime as timeMillisec, "
                       + "radEvt.radiation as rad "
                + "from RadiationEvent.std:lastevent() as radEvt "
                + "where context.tp.roomId = radEvt.roomId and "
                       + "context.tp.temperature>400 and radEvt.radiation>4";
    }
    
    public static String warningTemperatureRadiation(){
        return "context Ctx20secAfterTemperature "
                + "select context.tp.roomId as roomId, "
                       + "context.tp.temperature as temp, "
                       + "context.startTime as timeMillisec, "
                       + "radEvt.radiation as rad "
                + "from RadiationEvent as radEvt "
                + "where context.tp.roomId = radEvt.roomId and "
                       + "radEvt.radiation>3.5 and "
                       + "not(context.tp.temperature>400 and radEvt.radiation>4)";
    }
    
    
    
    
    
    /*
    public static String criticalRadiation(){
        return    "context CtxRadSegmentedByRoom "
                + "select context.key1 as roomId, "
                    + "avg(RadEvt.radiation) as avgRd, "
                    + "RadEvt.timeOfReading as timeOfReading "
                + "from RadiationEvent.win:time(10 sec) as RadEvt "
                + "where radiation>5 "
                + "having count(*)>2";
    }
    */
    /*
    public static String warningRadiation(){
        return  "context CtxRadSegmentedByRoom "
                +"select RadEvt.radiation as radiation, "
                      + "RadEvt.timeOfReading as timeOfReading, "
                      + "psql.roomName as roomName, "
                      + "psql.location as roomLocation "
                + "from RadiationEvent.win:length_batch(5) as RadEvt, "
                    + "sql:postgreslq [' SELECT roomName, location "
                                      + "FROM \"EsperDemonstrations\".\"Room\" "
                                      + "WHERE roomId = ${context.tp.roomId} '] as psql "
                + "where radiation>4";
    }
    */
    
    /*
    public static String criticalTemperatureRadiation(){
        return "context Ctx20secAfterTemperature "
                + "select context.tp.temperature as temp, "
                       + "context.startTime as timeMillisec, "
                       + "radEvt.radiation as rad, "
                       + "psql.roomName as roomName, "
                       + "psql.location as roomLocation "
                + "from RadiationEvent.std:lastevent() as radEvt, "
                     + "sql:postgreslq [' SELECT roomName, location "
                                      + "FROM \"EsperDemonstrations\".\"Room\" "
                                      + "WHERE roomId = ${context.tp.roomId} '] as psql "
                + "where context.tp.roomId = radEvt.roomId and "
                      + "context.tp.temperature>400 and radEvt.radiation>4";
    }
    */
    
    /*
    public static String warningTemperatureRadiation(){
        return "context Ctx20secAfterTemperature "
                + "select context.tp.roomId as roomId, "
                       + "context.tp.temperature as temp, "
                       + "radEvt.radiation as rad, "
                       + "context.startTime as timeMillisec, "
                       + "psql.roomName as roomName, "
                       + "psql.location as roomLocation "
                + "from RadiationEvent as radEvt, "
                     + "sql:postgreslq [' SELECT roomName, location "
                                      + "FROM \"EsperDemonstrations\".\"Room\" "
                                      + "WHERE roomId = ${context.tp.roomId} '] as psql "
                + "where context.tp.roomId = radEvt.roomId and "
                       + "rd.radiation>3.5 and "
                       + "not(context.tp.temperature>400 and radEvt.radiation>4)";
    }
    */
}
