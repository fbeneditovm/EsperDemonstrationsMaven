/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cor.cep.sddl;

import com.cor.cep.event.*;
import com.cor.cep.handler.TemperatureEventHandler;
import com.cor.cep.util.RandomEventGenerator;
import java.util.IllegalFormatConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;


 
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.serialization.Serialization;
import lac.cnet.sddl.objects.ApplicationObject;
import lac.cnet.sddl.objects.Message;
import lac.cnet.sddl.objects.PrivateMessage;
import lac.cnet.sddl.udi.core.SddlLayer;
import lac.cnet.sddl.udi.core.UniversalDDSLayerFactory;
import lac.cnet.sddl.udi.core.listener.UDIDataReaderListener;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class HelloCoreServer implements UDIDataReaderListener<ApplicationObject> {
    
    /** Logger */
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(HelloCoreServer.class);
    
    /** The SDDL core */
    SddlLayer  core;
    
    /** The TemperatureEventHandler - wraps the Esper engine and processes the Events  */
    @Autowired
    private TemperatureEventHandler temperatureEventHandler;
    
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.OFF);
 
        new HelloCoreServer();
    }
    
    public HelloCoreServer() {
        core = UniversalDDSLayerFactory.getInstance();
        core.createParticipant(UniversalDDSLayerFactory.CNET_DOMAIN);
 
        core.createPublisher();
        core.createSubscriber();
 
        Object receiveMessageTopic = core.createTopic(Message.class, Message.class.getSimpleName());
        core.createDataReader(this, receiveMessageTopic);
 
        Object toMobileNodeTopic = core.createTopic(PrivateMessage.class, PrivateMessage.class.getSimpleName());
        core.createDataWriter(toMobileNodeTopic);
 
        LOG.debug("=== Server Started (Listening) ===");
    
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                LOG.error("Not able to wait", e);
            }
        }
    }
  
    public void onNewData(ApplicationObject topicSample){
        Message message = (Message) topicSample;
        Object topic = null;
    
        try{
            topic = Serialization.fromJavaByteStream(message.getContent());
        }catch(IllegalFormatConversionException e){
            LOG.error("Problem in Deserialization", e);
        }
        
        if(topic instanceof RadiationEvent){
            RadiationEvent evt = (RadiationEvent) topic;
            temperatureEventHandler.handle(evt);
        } else if(topic instanceof TemperatureEvent){
            TemperatureEvent evt = (TemperatureEvent) topic;
            temperatureEventHandler.handle(evt);
      
        }
    }
    /**
    * Metodo responsavel por enviar um mensagem para o dispostivo móvel
    * @param msg
    * @param m
    * @param imei
    */
    public void responder(Message msg, double m,String imei){
	PrivateMessage privateMessage = new PrivateMessage();
	  
	LOG.debug("GatewayID: "+msg.getGatewayId());
        privateMessage.setGatewayId(msg.getGatewayId());
        LOG.debug("SendID: "+msg.getSenderId());
        privateMessage.setNodeId(msg.getSenderId());
   
        ApplicationMessage appMsg = new ApplicationMessage();
      
        //String m2 = buscarAlert(m,imei, msg.getSenderId().toString());
        //System.out.println(m2);
      
        //appMsg.setContentObject(m2);
        //privateMessage.setMessage(Serialization.toProtocolMessage(appMsg));
   
        core.writeTopic(PrivateMessage.class.getSimpleName(), privateMessage);
    }
}
