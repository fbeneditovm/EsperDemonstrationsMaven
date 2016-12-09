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
     * @When: Temperature in Kelvin is above 300
     * @Uses: method invocation to convert celsius to Kelvin
     */
    public static String warningTemperature(){
        return "select temperatures.kelvin as tempK, tempEvt.roomId as room, tempEvt.timeOfReading as timeOfReading from "
                + "pattern[ "
                + "every tempEvt=TemperatureEvent(com.cor.cep.util.Temperatures.isCAboveThresholdK(temperature, 300))], "
                + "method:com.cor.cep.util.Temperatures.getFromC(tempEvt.temperature) as temperatures ";
    }
    
    /**
     * @Selects: Average Radiation, roomName, Date with the time of reading
     * @When: In a window of 10 sec more than 2 events have radiation above 5
     * @Uses: match recognize
     */
    public static String criticalRadiation(){
        return    "select * from RadiationEvent "
                + "match_recognize ( "
                    + "partition by roomId "
                    + "measures avg(B.radiation) as avgRd, "
                        + "A.timeOfReading as timeOfReading, "//==A[0].timeofReading
                        + "A.roomId as roomId "
                    + "pattern (A B*) "
                        + "interval 10 seconds "
                        + "define "
                        + "A as A.radiation>5, "
                        + "B as B.radiation>5)";
    }
    
    /**
     * @Selects: Radiation, roomName, Date with the time of reading
     * @When: Radiation is above 4
     * @Uses: match recognize
     */
    public static String warningRadiation(){
        return  "select * from RadiationEvent "
              + "match_recognize ( "
                    + "partition by roomId "
                    + "measures A.radiation as radiation, "
                        + "A.timeOfReading as timeOfReading, "//==A[0].timeofReading
                        + "A.roomId as roomId "
                    + "pattern (A) "
                        + "define "
                        + "A as A.radiation>4)";
    }
    
    /**
     * @Selects: Temperature, Radiation, RoomID, the time in milliseconds
     * @When: Radiation is over 4 within 20 after Temperature is over 400
     */
    public static String criticalTemperatureRadiation(){
        return "select tempEvt.roomId as roomId, "
                + "tempEvt.temperature as temp, "
                + "tempEvt.timeOfReading as timeOfReading, "
                + "radEvt.radiation as rad "
                + "from pattern [(every tempEvt=TemperatureEvent(temperature>400) -> "
                    + "radEvt=RadiationEvent(radiation>4) where timer:within(20 sec))]";
    }
    
    /**
     * @Selects: Temperature, Radiation, RoomID, the time in milliseconds
     * @When: Radiation is over 3.5 within 20 after Temperature is over 300
     *        excluding the cases that fit the criticalTemperatureRadiation query
     */
    public static String warningTemperatureRadiation(){
        return "select tempEvt.roomId as roomId, "
                + "tempEvt.temperature as temp, "
                + "tempEvt.timeOfReading as timeOfReading, "
                + "radEvt.radiation as rad "
                + "from pattern [(every tempEvt=TemperatureEvent(temperature>300) -> "
                    + "radEvt=RadiationEvent(radiation>3.5) where timer:within(20 sec))]"
                + "where not(tempEvt.temperature>400 and radEvt.radiation>4)";
    }
}
