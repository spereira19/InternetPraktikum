package de.tud.tk.classquake.processing.data;

public interface TimeSeries {
	int size();
	double getValue(int pos);
	double getMean();
	double getVariance();
}
