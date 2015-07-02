package de.tud.tk.classquake.processing.net;

import java.util.Collection;

import com.google.common.collect.Lists;

import de.tud.tk.classquake.processing.data.ListTimeSeries;
import de.tud.tk.classquake.processing.data.TimeSeries;

public abstract class AbstractDataSource implements DataSource {
	private Collection<DataSourceListener> listener = Lists.newArrayList();

	@Override
	public void addDataSourceListener(DataSourceListener l) {
		listener.add(l);
	}

	@Override
	public void removeDataSourceListener(DataSourceListener l) {
		listener.remove(l);
	}

	protected void fireNewTimeseriesEvent(TimeSeries ts) {
		for (DataSourceListener l : listener)
			l.onNewTimeSeries(ts);
	}

}
