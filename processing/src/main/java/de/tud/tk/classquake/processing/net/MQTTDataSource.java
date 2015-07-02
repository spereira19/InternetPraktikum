package de.tud.tk.classquake.processing.net;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.tk.classquake.processing.data.ListTimeSeries;

public class MQTTDataSource extends AbstractDataSource {
	private final MqttClient client;
	private static final Logger log = LoggerFactory
			.getLogger(MQTTDataSource.class);

	public MQTTDataSource(String broker, String clientId, String topic)
			throws IOException {
		try {
			MemoryPersistence persistence = new MemoryPersistence();
			client = new MqttClient(broker, clientId, persistence);

			MqttConnectOptions conOptions = new MqttConnectOptions();
			conOptions.setCleanSession(true);
			
			log.info("Connecting to broker: " + broker);
			client.connect(conOptions);
			log.info("connected");

			client.setCallback(new MqttCallback() {

				@Override
				public void messageArrived(String topic, MqttMessage msg)
						throws Exception {
					byte[] data = msg.getPayload();
					if(log.isDebugEnabled())
						log.debug("receved msg on "+topic+": "+Arrays.toString(data));
					fireNewTimeseriesEvent(ListTimeSeries.fromBytes(data));
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					;
				}

				@Override
				public void connectionLost(Throwable cause) {
					;
				}
			});

			client.subscribe(topic);
			log.info("subscribed to " + topic);
		} catch (MqttException e) {
			throw new IOException("Error while init", e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			client.disconnect();
		} catch (MqttException e) {
			throw new IOException("error while disconnecting", e);
		}
		log.info("disconnected");
	}

}
