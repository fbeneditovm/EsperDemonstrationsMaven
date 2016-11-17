package com.cor.cep.event;

import java.util.Date;

/**
 * Immutable Temperature Event class. The process control system creates these events. The
 * TemperatureEventHandler picks these up and processes them.
 */
public class TemperatureEvent {

     /** The room in which the event occurred. */
    private int roomId;

    /** Temperature in Celcius. */
    private int temperature;
    
    /** Time temperature reading was taken. */
    private Date timeOfReading;
    
    /**
     * Single value constructor.
     * @param value Temperature in Celsius.
     */
    /**
     * Temperature constructor.
     * @param temperature Temperature in Celsius
     * @param timeOfReading Time of Reading
     */
    public TemperatureEvent(int roomId, int temperature, Date timeOfReading) {
        this.roomId = roomId;
        this.temperature = temperature;
        this.timeOfReading = timeOfReading;
    }
    
    /**
     * Get the id of the Room
     * @return the roomId
     */
    public int getRoomId() {
        return roomId;
    }
    
    /**
     * Get the Temperature.
     * @return Temperature in Celsius
     */
    public int getTemperature() {
        return temperature;
    }
       
    /**
     * Get time Temperature reading was taken.
     * @return Time of Reading
     */
    public Date getTimeOfReading() {
        return timeOfReading;
    }

    @Override
    public String toString() {
        return "TemperatureEvent [" + temperature + "C]";
    }

}
