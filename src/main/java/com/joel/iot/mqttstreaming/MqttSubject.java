package com.joel.iot.mqttstreaming;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MqttSubject implements MqttCallback {

	private String broker;
	private String topic;
	private String clientId;
	private MqttClient client;
	private final PublishSubject<String> subject = PublishSubject.create();	
	private static Logger log = Logger.getLogger("MqttSubject");

	public MqttSubject(String broker, String clientId, String topic) {
		this.broker = broker;
		this.clientId = clientId;
		this.topic = topic;
	}

	public void connect() {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			log.info(String.format("Mqtt connect to broker %s and topic %s.", broker, topic));
			client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(MqttConnectOptions.CLEAN_SESSION_DEFAULT);
			client.connect(options);
			client.subscribe(topic);
			client.setCallback(this);
		} catch (MqttException ex) {
			log.error(String.format("Mqtt connect exception: %", ex.getMessage()));
			subject.onError(ex);
		}
	}
	
	public void disconnect() {
		try {
			client.disconnect();
		} catch (MqttException ex) {
			log.error(String.format("Mqtt connect exception: %", ex.getMessage()));
			subject.onError(ex);
		}
	}
	
	public Observable<String> observe() {
		return subject;
	}

	public void publish(String message) {
		MqttMessage mqttMessage = new MqttMessage(message.getBytes());
		try {
			if (!client.isConnected()) {
				client.connect();
			}
			client.publish(this.topic, mqttMessage);
		} catch (MqttPersistenceException ex) {
			log.error(String.format("Mqtt publish exception: %", ex.getMessage()));
		} catch (MqttException ex) {
			log.error(String.format("Mqtt publish exception: %", ex.getMessage()));
		}
	}

	public void connectionLost(Throwable cause) {
		try {
			client.connect();
		} catch (MqttSecurityException ex) {
			log.error(String.format("Mqtt connection lost: %", ex.getMessage()));
		} catch (MqttException ex) {
			log.error(String.format("Mqtt connection lost: %", ex.getMessage()));
		}
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		log.info("Put a new message to the subject.");
		subject.onNext(new String(message.getPayload()));
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		log.info("onCompleted");
	}
	
}
