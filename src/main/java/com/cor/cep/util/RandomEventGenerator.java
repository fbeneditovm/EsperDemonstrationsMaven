/**
 * Project Based on https://github.com/corsoft/esper-demo-nuclear.git
 */
package com.cor.cep.util;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cor.cep.event.*;

import com.cor.cep.handler.EventHandler;

/**
 * Just a simple class to create a number of Random Events and pass them off to the
 EventHandler.
 */
@Component
public class RandomEventGenerator {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(RandomEventGenerator.class);

    /** The EventHandler - wraps the Esper engine and processes the Events  */
    @Autowired
    private EventHandler temperatureEventHandler;

    /**
     * Creates simple random Temperature events and lets the implementation class handle them.
     */
    public void startSendingReadings(final long noOfTemperatureEvents) {

        ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

        xrayExecutor.submit(new Runnable() {
            public void run() {
                
                int count = 0;
                int roomId = 1;
                while (count < noOfTemperatureEvents) {
                    if(roomId>3) roomId = 1;
                    TemperatureEvent tpEvent = new TemperatureEvent(roomId, new Random().nextInt(500), new Date());
                    temperatureEventHandler.handle(tpEvent);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        LOG.error("Thread Interrupted", e);
                    }
                    double radiation = (double) new Random().nextInt(6) +new Random().nextDouble();
                    RadiationEvent rdEvent = new RadiationEvent(roomId, radiation, new Date());
                    temperatureEventHandler.handle(rdEvent);
                    count++;
                    roomId++;
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        LOG.error("Thread Interrupted", e);
                    }
                }

            }
        });
    }
}
