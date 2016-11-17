package com.cor.cep.handler;

import GUI.EventLogScreen;
import GUI.WarningScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cor.cep.event.*;
import com.cor.cep.subscriber.*;
import com.cor.cep.util.*;

import com.espertech.esper.client.*;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

/**
 * This class handles incoming Temperature Events. It processes them through the EPService, to which
 * it has attached the 3 queries.
 */
@Component
@Scope(value = "singleton")
public class TemperatureEventHandler implements InitializingBean{

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(TemperatureEventHandler.class);

    /** Esper service */
    private EPServiceProvider epService;
    private EPStatement criticalEventStatement;
    private EPStatement warningEventStatement;
    private EPStatement monitorEventStatement;

    @Autowired
    @Qualifier("criticalEventSubscriber")
    private StatementSubscriber criticalEventSubscriber;

    @Autowired
    @Qualifier("warningEventSubscriber")
    private StatementSubscriber warningEventSubscriber;    
    
    EventLogScreen logScreen;
    WarningScreen warningScreen;
    
    RadiationWindowListener rwListener;
    RadiationBatchListener rbListener;
    
    TemperatureWindowListener twListener;
    TemperatureBatchListener tbListener;
    
    EPStatement last5RadiationStatement;
    EPStatement batch5RadiationStatement;
    
    EPStatement last5TemperatureStatement;
    EPStatement batch5TemperatureStatement;
    
    /**
     * Get the Events by Batch
     */
    public void getByBatch(){
        last5RadiationStatement.removeAllListeners();
        last5TemperatureStatement.removeAllListeners();
        
        batch5RadiationStatement.addListener(rbListener);
        batch5TemperatureStatement.addListener(tbListener);
        System.out.println("Changed to batch");
    }
    
    /**
     * Get the Events by Window
     */
    public void getByWindow(){
        batch5RadiationStatement.removeAllListeners();
        batch5TemperatureStatement.removeAllListeners();
        
        last5RadiationStatement.addListener(rwListener);
        last5TemperatureStatement.addListener(twListener);
        System.out.println("Changed to window");
    }
    

    /**
     * Configure Esper Statement(s).
     */
    public void initService() {
        
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
        
        //Start GUI
        logScreen.setVisible(true);
        warningScreen.setVisible(true);

        LOG.debug("Initializing Servcie ..");
        //Relational DB Configuration
        ConfigurationDBRef dbConfig = new ConfigurationDBRef();
        dbConfig.setDriverManagerConnection("org.postgresql.Driver",
                                            "jdbc:postgresql://localhost:5432/EsperDemonstrations", 
                                            "esper", 
                                            "esperdemonstrations");
        dbConfig.setMetadataOrigin(ConfigurationDBRef.MetadataOriginEnum.SAMPLE);
        
        //Configuration
        Configuration config = new Configuration();
        config.addImport("com.cor.cep.util.*");
        config.addDatabaseReference("Postgresql", dbConfig);
        config.addEventTypeAutoName("com.cor.cep.event");
        epService = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator epAdm = epService.getEPAdministrator();
        
        //EPLStatement and Listener registration
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
        
        createCriticalTemperatureCheckExpression();
        createWarningTemperatureCheckExpression();

    }

    /**
     * EPL to check for a sudden critical rise across 4 events, where the last event is 1.5x greater
     * than the first event. This is checking for a sudden, sustained escalating rise in the
     * temperature
     */
    private void createCriticalTemperatureCheckExpression() {
        
        LOG.debug("create Critical Temperature Check Expression");
        criticalEventStatement = epService.getEPAdministrator().createEPL(criticalEventSubscriber.getStatement());
        criticalEventStatement.setSubscriber(criticalEventSubscriber);
    }

    /**
     * EPL to check for 2 consecutive Temperature events over the threshold - if matched, will alert
     * listener.
     */
    private void createWarningTemperatureCheckExpression() {

        LOG.debug("create Warning Temperature Check Expression");
        warningEventStatement = epService.getEPAdministrator().createEPL(warningEventSubscriber.getStatement());
        warningEventStatement.setSubscriber(warningEventSubscriber);
    }

    
    /**
     * Handle the incoming TemperatureEvent.
     */
    public void handle(TemperatureEvent event) {

        LOG.debug(event.toString());
        epService.getEPRuntime().sendEvent(event);

    }
    
    /**
     * Handle the incoming RadiationEvent.
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
