package com.joel.iot.azurestreaming;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

public class AzureSubject {
	
	private IotHubClientProtocol protocol;
	private DeviceClient client;
	private String connectionString;
	private static Logger log = Logger.getLogger("AzureSubject");
	
	public AzureSubject(String connectionString) {
    	protocol = IotHubClientProtocol.MQTT;
    	this.connectionString = connectionString;
	}
	
	public void connect() {		
    	try {
			client = new DeviceClient(connectionString, protocol);
	    	client.setOption("SetSASTokenExpiryTime", 2400l);
	    	client.open();
		}
    	catch (URISyntaxException ex) {
    		log.error(String.format("Azure connect exception: %s", ex.getMessage()));
		}
    	catch (IOException ex) {
    		log.error(String.format("Azure connect exception: %s", ex.getMessage()));
		}		
	}
	
	public void disconnect() {
		try {
			client.closeNow();
		} catch (IOException ex) {
			log.error(String.format("Azure disconnect exception: %s", ex.getMessage()));
		}
	}
	
	public void publish(String payload) {
		
		Message message = new Message(payload);
		message.setMessageId(java.util.UUID.randomUUID().toString());
		EventCallback callback = new EventCallback();
		client.sendEventAsync(message, callback, message);
		
	}
	
	private class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            Message msg = (Message) context;
            log.info(String.format("Azure responded to message %s with status %s.", msg.getMessageId(), status.name()));
        }
	}

}
