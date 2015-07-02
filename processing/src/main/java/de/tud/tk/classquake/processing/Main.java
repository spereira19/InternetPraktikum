package de.tud.tk.classquake.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.tk.classquake.processing.data.TimeSeries;
import de.tud.tk.classquake.processing.net.DataSink;
import de.tud.tk.classquake.processing.net.DataSource;
import de.tud.tk.classquake.processing.net.DataSourceListener;
import de.tud.tk.classquake.processing.net.MQTTDataSink;
import de.tud.tk.classquake.processing.net.MQTTDataSource;

public class Main {
	public static Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException {
		log.info("Processing started");
		// String broker = "tcp://iot.eclipse.org:1883";
		String broker = "tcp://130.83.160.103:1883";
		String intopic = "MQTT Test from Source code";
		String outtopic = "de.tud.tk.classquake.processed";

		try (DataSource src = new MQTTDataSource(broker, "processing-reader",
				intopic);
				DataSink sink = new MQTTDataSink(broker, "processing-writer",
						outtopic)) {
			src.addDataSourceListener(new DataSourceListener() {
				@Override
				public void onNewTimeSeries(TimeSeries ts) {
					try {
						sink.sendData(ts.getVariance());
					} catch (IOException e) {
						log.error("sending data faild", e);
					}
				}
			});

			String input;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			do {
				input = in.readLine().toLowerCase().trim();
			} while (input != "exit");
		} catch (IOException e) {
			log.error("connection faild", e);
		}
	}
}
