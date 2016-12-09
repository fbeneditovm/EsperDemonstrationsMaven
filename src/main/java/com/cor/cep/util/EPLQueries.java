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
     * @When: Temperature 9s above 250
     * @Uses: filter
     */
    public static String warningTemperature(){
        return "select tempEvt.temperature as temp, tempEvt.roomId as room, tempEvt.timeOfReading as timeOfReading from " +
               "\nTemperatureEvent(temperature > 250) as tempEvt";
    }
    
    /**
     * @Selects: Radiation, roomName, Date with the time of reading
     * @When: Radiation is above 4
     * @Uses: database data retrieval
     */
    public static String warningRadiation(){
        return "select radEvt.radiation as rad, radEvt.roomId as room, radEvt.timeOfReading as timeOfReading from " +
               "\nRadiationEvent as radEvt"
                + "\nwhere radiation>4";
    }
}