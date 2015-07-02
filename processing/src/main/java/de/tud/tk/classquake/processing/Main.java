package de.tud.tk.classquake.processing;

import java.io.IOException;

import javax.swing.JFrame;

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
		String broker = "tcp://iot.eclipse.org:1883";

		try (DataSource src = new MQTTDataSource(broker, "processing-reader",
				"in-topic");
				DataSink sink = new MQTTDataSink(broker, "processing-writer",
						"out-topic")) {
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
			
			// FIXME this is not feasible
			new JFrame().setVisible(true);
			
		} catch (IOException e) {
			log.error("connection faild", e);
		}
	}
}
