package de.tud.tk.classquake.processing.net;

import de.tud.tk.classquake.processing.data.TimeSeries;

public interface DataSourceListener {
	public void onNewTimeSeries(TimeSeries ts);
}
