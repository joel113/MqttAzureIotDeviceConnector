package com.joel.iot.events;

public class TemperatureSensor {
	
	private String temperature;
	private String humidity;
	
	public TemperatureSensor(String temperature, String humidity) {
		this.temperature = temperature;
		this.humidity = humidity;
	}
	
	public String getTemperature() {
		return temperature;
	}
	
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	
	public String getHumidity() {
		return humidity;
	}
	
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

}
