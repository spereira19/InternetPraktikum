package de.tud.tk.classquake.processing.net;

import java.io.Closeable;
import java.io.IOException;

public interface DataSink extends Closeable {
	public void sendData(double value) throws IOException;
}
