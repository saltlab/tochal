package com.proteus.stats;

import java.util.Arrays;

public class Statistics {

	public static double getMean(double []data) {
		double sum = 0;
		for (double d : data)
			sum += d;
		return sum / data.length;
	}
	
	public static double getMedian(double []data) {
		double []temp = new double[data.length];
		System.arraycopy(data, 0, temp, 0, temp.length);
		Arrays.sort(temp);
		
		if (data.length % 2 == 0)
			return (temp[temp.length / 2 - 1] + temp[temp.length / 2]) / 2;
		else
			return temp[temp.length / 2];
	}
	
	public static double getVariance(double []data) {
		double mean = Statistics.getMean(data);
		double temp = 0;
		for (double d : data)
			temp += Math.pow((mean - d), 2);
		return temp / data.length;
	}
	
	public static double getStdDev(double []data) {
		double var = Statistics.getVariance(data);
		return Math.sqrt(var);
	}
}
