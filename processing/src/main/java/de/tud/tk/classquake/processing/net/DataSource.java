package de.tud.tk.classquake.processing.net;

import java.io.Closeable;

public interface DataSource extends Closeable{
	public void addDataSourceListener(DataSourceListener l);
	public void removeDataSourceListener(DataSourceListener l);
}
