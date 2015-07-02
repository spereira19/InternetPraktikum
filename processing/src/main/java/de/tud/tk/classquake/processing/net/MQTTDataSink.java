package de.tud.tk.classquake.processing.net;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQTTDataSink implements DataSink {
	private final MqttClient client;
	private final String topic;
	private static final Logger log = LoggerFactory
			.getLogger(MQTTDataSink.class);

	public MQTTDataSink(String broker, String clientId, String topic)
			throws IOException {
		try {
			this.topic = topic;
			MemoryPersistence persistence = new MemoryPersistence();
			client = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setCleanSession(true);
			client.connect(conOptions);
		} catch (MqttException e) {
			throw new IOException("Error while init", e);
		}
	}

	@Override
	public void sendData(double value) throws IOException {
		byte[] data = new byte[8];
		ByteBuffer.wrap(data).putDouble(value);
		MqttMessage msg = new MqttMessage(data);
		try {
			client.publish(topic, msg);
		} catch (MqttException e) {
			throw new IOException("Error sending " + String.valueOf(value)
					+ " to " + topic, e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			client.disconnect();
		} catch (MqttException e) {
			throw new IOException(e);
		}
		log.info("disconnected");
	}

}
