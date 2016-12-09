/**
 * Project Based on https://github.com/corsoft/esper-demo-nuclear.git
 */
package com.cor.cep.handler;

import GUI.EventLogScreen;
import GUI.WarningScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cor.cep.event.*;
import com.cor.cep.subscriber.*;
import com.cor.cep.util.*;

import com.espertech.esper.client.*;

/**
 * This class Configures the EsperCEP environment 
 * as well as handles incoming Events. 
 * It processes them through the EPService, to which it has attached the queries.
 */
@Component
@Scope(value = "singleton")
public class EventHandler implements InitializingBean{

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(EventHandler.class);

    private EPServiceProvider epService; //The esper service  
    
    EventLogScreen logScreen; //The screen to show a log of the simple events
    WarningScreen warningScreen; //The scren to show the complex warning events
    
    /** The Listeners */
    RadiationWindowListener rwListener;
    RadiationBatchListener rbListener;
    TemperatureWindowListener twListener;
    TemperatureBatchListener tbListener;
    
    /** The Statements (EPL Queries) */
    EPStatement last5RadiationStatement;
    EPStatement batch5RadiationStatement;
    
    EPStatement last5TemperatureStatement;
    EPStatement batch5TemperatureStatement;
    
    /**
     * Get the Events by Batch.
     */
    public void getByBatch(){
        last5RadiationStatement.removeAllListeners();
        last5TemperatureStatement.removeAllListeners();
        
        batch5RadiationStatement.addListener(rbListener);
        batch5TemperatureStatement.addListener(tbListener);
        System.out.println("Changed to batch");
    }
    
    /**
     * Get the Events by Window.
     */
    public void getByWindow(){
        batch5RadiationStatement.removeAllListeners();
        batch5TemperatureStatement.removeAllListeners();
        
        last5RadiationStatement.addListener(rwListener);
        last5TemperatureStatement.addListener(twListener);
        System.out.println("Changed to window");
    }
    

    /**
     * Configure the Esper Environment.
     */
    public void initService() {
        
        /** The Screen Configuration */
        logScreen = new EventLogScreen(this);
        warningScreen = new WarningScreen();
        rwListener = new RadiationWindowListener();
        rwListener.setScreen(logScreen);
        rbListener = new RadiationBatchListener();
        rbListener.setScreen(logScreen);
        twListener = new TemperatureWindowListener();
        twListener.setScreen(logScreen);
        tbListener = new TemperatureBatchListener();
        tbListener.setScreen(logScreen);
        
        /** Start GUI */
        logScreen.setVisible(true);
        warningScreen.setVisible(true);

        LOG.debug("Initializing Servcie ..");
        
        /** Relational DB Configuration */
        ConfigurationDBRef dbConfig = new ConfigurationDBRef();
        dbConfig.setDriverManagerConnection("org.postgresql.Driver",
                                            "jdbc:postgresql://localhost:5432/EsperDemonstrations", 
                                            "esper", 
                                            "esperdemonstrations");
        dbConfig.setMetadataOrigin(ConfigurationDBRef.MetadataOriginEnum.SAMPLE);
        
        /** Setting the Esper Configuration and EPService */
        Configuration config = new Configuration();
        config.addImport("com.cor.cep.util.*");
        config.addDatabaseReference("Postgresql", dbConfig);
        config.addEventTypeAutoName("com.cor.cep.event");
        epService = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator epAdm = epService.getEPAdministrator();
        
        
        /** Start of EPLStatement and Listener registration */
        last5RadiationStatement = epAdm.createEPL(EPLQueries.getLast5Radiation());
        batch5RadiationStatement = epAdm.createEPL(EPLQueries.getBatch5Radiation());
        last5RadiationStatement.addListener(rwListener);
        
        last5TemperatureStatement = epAdm.createEPL(EPLQueries.getLast5Temperature());
        batch5TemperatureStatement = epAdm.createEPL(EPLQueries.getBatch5Temperature());
        last5TemperatureStatement.addListener(twListener);
        
        EPStatement ctxRadSegmentedByRoom = epAdm.createEPL(EPLQueries.createCtxRadSegmentedByRoom());
        EPStatement ctxTempSegmentedByRoom = epAdm.createEPL(EPLQueries.createCtxTempSegmentedByRoom());
        EPStatement ctx20secAfterTemp = epAdm.createEPL(EPLQueries.createCtx20secAfterTemperature());
        
        EPStatement warningTemperatureStatement = epAdm.createEPL(EPLQueries.warningTemperature());
        TemperatureWarningListener twl = new TemperatureWarningListener();
        warningTemperatureStatement.addListener(twl);
        twl.setScreen(warningScreen);
        
        EPStatement criticalRadiationStatement = epAdm.createEPL(EPLQueries.criticalRadiation());
        RadiationCriticalListener rcl = new RadiationCriticalListener();
        criticalRadiationStatement.addListener(rcl);
        rcl.setScreen(warningScreen);
        
        EPStatement warningRadiationStatement = epAdm.createEPL(EPLQueries.warningRadiation());
        RadiationWarningListener rwl = new RadiationWarningListener();
        warningRadiationStatement.addListener(rwl);
        rwl.setScreen(warningScreen);
        
        EPStatement criticalTemperatureRadiationStatement = epAdm.createEPL(EPLQueries.criticalTemperatureRadiation());
        TemperatureRadiationCriticalListener trcl = new TemperatureRadiationCriticalListener();
        criticalTemperatureRadiationStatement.addListener(trcl);
        trcl.setScreen(warningScreen);
        
        EPStatement warningTemperatureRadiationStatement = epAdm.createEPL(EPLQueries.warningTemperatureRadiation());
        TemperatureRadiationWarningListener trwl = new TemperatureRadiationWarningListener();
        warningTemperatureRadiationStatement.addListener(trwl);
        trwl.setScreen(warningScreen);
        /** End of EPLStatement and Listener registration */
    }
    
    /**
     * Handle the incoming TemperatureEvent.
     * @param event the event to handle
     */
    public void handle(TemperatureEvent event) {

        LOG.debug(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }
    
    /**
     * Handle the incoming RadiationEvent.
     * @param event the event to handle
     */
    public void handle(RadiationEvent event) {

        LOG.debug(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }

    @Override
    public void afterPropertiesSet() {
        
        LOG.debug("Configuring..");
        initService();
    }
}
