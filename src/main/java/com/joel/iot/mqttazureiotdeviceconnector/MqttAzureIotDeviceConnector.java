package com.joel.iot.mqttazureiotdeviceconnector;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import com.google.gson.Gson;
import com.joel.iot.azurestreaming.AzureSubject;
import com.joel.iot.events.TemperatureMessage;
import com.joel.iot.events.TemperatureSensor;
import com.joel.iot.mqttstreaming.MqttSubject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Hello world!
 *
 */
public class MqttAzureIotDeviceConnector {
	
	private String broker;
	private static Logger log = Logger.getRootLogger();
	
	public MqttAzureIotDeviceConnector(String broker) {
		this.broker = broker;
		log.setLevel(Level.INFO);
	}
	
	public void startProcessing(String clientId, String topic) {
			
		final MqttSubject eventSubject = new MqttSubject(broker, clientId, topic);
		eventSubject.connect();
		
		final AzureSubject azureSubject = new AzureSubject("HostName=temp-at-homecaa07.azure-devices.net;DeviceId=esp01-1;SharedAccessKey=0ReqIT63NCn2+e+x6Wy26r1cVn0OOYRq6q7rMX7i56k=");
		azureSubject.connect();	
		
		final Observable<String> events = eventSubject.observe().doOnSubscribe(new Action0() {
			@Override
			public void call() {
				log.info(String.format("Subscribe to %s to process events.", topic));
			}
		}).doOnUnsubscribe(new Action0() {
			@Override
			public void call() {
				log.info(String.format("Unsubscribe from %s.", topic));		
			}
		}).map(new Func1<String, String>() {
			@Override
			public String call(String json) {
				log.info(String.format("Received sensor event with content %s.", json));
				Gson gson = new Gson();
				TemperatureSensor temperatureSensor = gson.fromJson(json, TemperatureSensor.class);
				TemperatureMessage temperatureMessage = new TemperatureMessage("esp01-1",temperatureSensor.getTemperature(), temperatureSensor.getHumidity());
				return gson.toJson(temperatureMessage);
			}	
		}).doOnNext(new Action1<String>() {
			@Override
			public void call(String string) {
				log.info(String.format("Processed sensor event with content %s.", string));
			}
		});				
		
		events.forEach(s -> azureSubject.publish(s));
		
	}
	
    public static void main( String[] args ) {
    	
    	try {
	        SimpleLayout layout = new SimpleLayout();
	        ConsoleAppender consoleAppender = new ConsoleAppender( layout );
	        log.addAppender(consoleAppender);
	        FileAppender fileAppender = new FileAppender( layout, "logs/eventprocessor.log", false );
	        log.addAppender(fileAppender);
    	}
    	catch(Exception ex) {
    		System.out.println(ex);
    	}
    	
    	MqttAzureIotDeviceConnector processor = new MqttAzureIotDeviceConnector("tcp://joel-flocke:1883");
    	processor.startProcessing("event-processor-coppola-sensor","/joel-weatherman/sensor");
    	
    }
    
}
