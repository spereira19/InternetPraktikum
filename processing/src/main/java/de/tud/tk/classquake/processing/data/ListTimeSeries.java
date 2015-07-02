package de.tud.tk.classquake.processing.data;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Lists;

public class ListTimeSeries implements TimeSeries {
	protected final List<Double> values = Lists.newArrayList();
	protected final DescriptiveStatistics stats = new DescriptiveStatistics();

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public double getValue(int pos) {
		return values.get(pos);
	}

	public void add(Double[] array) {
		for (int i = 0; i < array.length; i++) {
			add(array[i]);
		}
	}

	public void add(List<Double> d) {
		for(Double val : d)
			add(val);
	}
	
	public void add(TimeSeries t) {
		for(int i = 0; i < t.size(); i++) {
			add(t.getValue(i));
		}
	}
	
	public void add(Double d) {
		values.add(d);
		stats.addValue(d);
	}

	public static TimeSeries fromBytes(byte[] data) {
		// FIXME move me out here
		ByteBuffer buf = ByteBuffer.wrap(data);
		if (buf.asDoubleBuffer().hasArray()) {
			double[] values = buf.asDoubleBuffer().array();
			return new ListTimeSeries();
		} else {
			throw new IllegalArgumentException(
					"Given Data do not contain a timeseries");
		}

	}

	@Override
	public double getMean() {
		return stats.getMean();
	}

	@Override
	public double getVariance() {
		return stats.getVariance();
	}
}
