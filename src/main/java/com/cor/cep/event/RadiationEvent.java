package com.cor.cep.event;

import java.util.Date;

/**
 * A Simple Radiation Event
 * @author fbeneditovm
 */
public class RadiationEvent{
    
    /** The room in which the event occurred. */
    private int roomId;
    
    /** Radiation in uSv/h. */
    private double radiation;
    
    /** Time radiation reading was taken. */
    private Date timeOfReading;
    
    
    /**
     * Radiation constructor.
     * @param radiation Temperature in Celsius
     * @param timeOfReading Time of Reading
     */
    public RadiationEvent(int roomId, double radiation, Date timeOfReading) {
        this.roomId = roomId;
        this.radiation = radiation;
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
     * Get the Radiation.
     * @return Radiation in uSv/h
     */
    public double getRadiation() {
        return radiation;
    }
       
    /**
     * Get time Radiation reading was taken.
     * @return Time of Reading
     */
    public Date getTimeOfReading() {
        timeOfReading.toString();
        return timeOfReading;
    }

    @Override
    public String toString() {
        return "RadiationEvent [" + radiation + "uSv/h]";
    }
}
