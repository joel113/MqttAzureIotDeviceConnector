package com.joel.iot.events;

public class TemperatureMessage {
	
	private String Device;
	private String Temperature;
	private String Humidity;
	
	public TemperatureMessage(String device, String temperature, String humidity) {
		this.Device = device;
		this.Temperature = temperature;
		this.Humidity = humidity;
	}
	
	public String getDevice() {
		return Device;
	}

	public void setDevice(String device) {
		this.Device = device;
	}

	public String getTemperature() {
		return Temperature;
	}
	
	public void setTemperature(String temperature) {
		this.Temperature = temperature;
	}
	
	public String getHumidity() {
		return Humidity;
	}
	
	public void setHumidity(String humidity) {
		this.Humidity = humidity;
	}
}
