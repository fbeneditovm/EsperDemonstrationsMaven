/**
 * Project Based on https://github.com/corsoft/esper-demo-nuclear.git
 */
package com.cor.cep.util;

/**
 * A class of static methods that return EPLQueries
 * @author fbeneditovm
 */
public class EPLQueries {
    /**
     * A context that segments the Radiation Events by room
     */
    public static String createCtxRadSegmentedByRoom(){
        return "create context CtxRadSegmentedByRoom "
                + "partition by roomId from RadiationEvent";
    }
    
    /**
     * A context that segments the Temperature Events by room
     */
    public static String createCtxTempSegmentedByRoom(){
        return "create context CtxTempSegmentedByRoom "
                + "partition by roomId from TemperatureEvent";
    }
    
    /**
     * A context that starts when a Temperature above 300 is reached
     * and ends after 20 seconds
     */
    public static String createCtx20secAfterTemperature(){
        return "create context Ctx20secAfterTemperature" +
            "  initiated by TemperatureEvent(temperature>300) as tp" +
            "  terminated after 20 seconds";
    }
    /**
     * @Selects The last 5 Radiation Events using a regular window of length 5
     */
    public static String getLast5Radiation(){
        return "select irstream * from " +
               "RadiationEvent.win:length(5)";
    }
    
    /**
     * @Selects The Radiation Events 5 by 5
     */
    public static String getBatch5Radiation(){
        return "select irstream * from " +
               "RadiationEvent.win:length_batch(5)";
    }
    
    /**
     * @Selects The last 5 Temperature Events using a regular window of length 5
     */
    public static String getLast5Temperature(){
        return "select irstream * from " +
               "TemperatureEvent.win:length(5)";
    }
    
    /**
     * @Selects The Temperature Events 5 by 5
     */
    public static String getBatch5Temperature(){
        return "select irstream * from " +
               "TemperatureEvent.win:length_batch(5)";
    }
    
    /**
     * @Selects: Temperature in Kelvin, roomId, Date with the time of reading
     * @When: Temperature in Kelvin is above 600
     * @Uses: method invocation to convert celsius to Kelvin
     */
    public static String warningTemperature(){
        return "select temperatures.kelvin as tempK, tempEvt.roomId as room, tempEvt.timeOfReading as timeOfReading from " +
               "\nTemperatureEvent.std:lastevent() as tempEvt, "
                + "\nmethod:com.cor.cep.util.Temperatures.getFromC(temperature) as temperatures "
                + "\nwhere temperatures.kelvin>600";
    }
    
    /**
     * @Selects: Average Radiation, roomName, Date with the time of reading
     * @When: In a window of 15 sec more than 2 events in the same room
     * have radiation above 4
     * @Uses: database data retrieval
     */
    public static String criticalRadiation(){
        return    "context CtxRadSegmentedByRoom "
                + "\nselect avg(RadEvt.radiation) as avgRd, "
                    + "\nRadEvt.timeOfReading as timeOfReading, "
                    + "\npsql.roomName as roomName "
                + "\nfrom RadiationEvent(radiation>4).win:time(15 sec) as RadEvt, "
                + "\nsql:Postgresql[' SELECT rTb.\"roomName\""
                                      + "\nFROM \"public\".\"Room\" as rTb "
                                      + "\nWHERE rTb.\"roomId\" = ${RadEvt.roomId} '] as psql"
                + "\nhaving count(RadEvt)>2";
    }
    /**
     * @Selects: Radiation, roomName, Date with the time of reading
     * @When: Radiation is above 4
     * @Uses: database data retrieval
     */
    public static String warningRadiation(){
        return  "select RadEvt.radiation as radiation, "
                      + "\nRadEvt.timeOfReading as timeOfReading, "
                      + "\npsql.roomName as roomName "
                + "\nfrom RadiationEvent.std:lastevent() as RadEvt, "
                + "\nsql:Postgresql[' SELECT rTb.\"roomName\""
                                      + "\nFROM \"public\".\"Room\" as rTb "
                                      + "\nWHERE rTb.\"roomId\" = ${RadEvt.roomId} '] as psql"
                + "\nwhere radiation>4";
    }
    
    /**
     * @Selects: Temperature, Radiation, RoomID, the time in milliseconds
     * @When: Radiation is over 4 within 20 after Temperature is over 400
     * @Uses: database data retrieval
     */
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
    
    /**
     * @Selects: Temperature, Radiation, RoomID, the time in milliseconds
     * @When: Radiation is over 3.5 within 20 after Temperature is over 300
     *        excluding the cases that fit the criticalTemperatureRadiation query
     * @Uses: database data retrieval
     */
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
}
