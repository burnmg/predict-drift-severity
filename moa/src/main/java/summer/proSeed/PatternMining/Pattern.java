/*
 * Pattern.java
 * Author: Kylie Chen - The University of Auckland 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 * 
 */

package summer.proSeed.PatternMining;

import java.util.Random;
import org.apache.commons.math3.stat.StatUtils;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;

public class Pattern implements Comparable<Pattern> {

	public static double DELTA = 0.05; // delta parameter for Hoeffding bound with Bonferroni correction
	public static double ALPHA = 0.1; // alpha parameter for KS test   
	public static boolean ABRUPT = false; // nature of volatility changes
	public static int NUM_SAMPLES = 100; // number of samples to store

	private static double[][] KSTable = { 
			{ 0.1, 0.05, 0.025, 0.01, 0.005, 0.001 },
			{ 1.22, 1.36, 1.48, 1.63, 1.73, 1.95 } };

	private double mean;
	private double variance;

	private Random random = new Random(666);

	private int size = 0;
	private double total = 0;
	private double[] data; // sample of data points

	public static double CONFIDENCE = 1.96; // confidence for drift estimate interval    

	private double lengthTotal = 0;
	private double lengthNum = 0;

	public int weight = 1;

	private static Rengine re;

	public Pattern() {
		mean = 0;
		variance = 0;
	}

	/**
	 * creates a pattern with mean m, and variance v
	 *
	 * @param m
	 *            mean of pattern
	 * @param v
	 *            variance of pattern
	 */
	public Pattern(double m, double v) {
		mean = m;
		variance = v;
		data = new double[NUM_SAMPLES];
	}

	/**
	 * creates a pattern with mean m, variance v, and confidence c
	 *
	 * @param m
	 *            mean of pattern
	 * @param v
	 *            variance of pattern
	 * @param c
	 *            confidence for pattern similarity
	 */
	public Pattern(double m, double v, double c) {
		mean = m;
		variance = v;
		CONFIDENCE = c;
		data = new double[NUM_SAMPLES];
	}

	public Pattern(double[] dataPoints, int length) {
		data = new double[NUM_SAMPLES];
		this.total = 0;
		int maxLength = Math.min(length, data.length);
		for (int i = 0; i < maxLength; i++) {
			this.data[i] = dataPoints[i];
			this.total += dataPoints[i];
		}
		size = maxLength;
		mean = this.total / (double) length;
	}

	public Pattern(double[] dataPoints) {		
		this(dataPoints, dataPoints.length);
	}

	public static void setRengine(Rengine engine) {
		Pattern.re = engine;
	}

	public static Rengine getRengine() {
		return Pattern.re;
	}

	public void printData() {
		for (int i = 0; i < this.size; i++) {
			System.out.print(data[i] + " ");
		}
		System.out.println();
	}

	private double getStatistic(double[] x, double[] y) {
		long xVec = re.rniPutDoubleArray(x);
		re.rniAssign("a", xVec, 0);
		long yVec = re.rniPutDoubleArray(y);
		re.rniAssign("b", yVec, 0);
		REXP ans = re.eval("ks.test(a, b)");

		RList ansList = ans.asList();
		REXP rStat = ansList.at("statistic");
		double stat = rStat.asDouble();

		return stat;
	}

	public void addData(double[] extraData, int length) {
		for (int i = 0; i < length; i++) {
			if (size < this.data.length) {
				this.data[size] = extraData[i];
				this.total += extraData[i];
				size++;
			} else {
				int randomIndex = random.nextInt(this.data.length);
				this.total = this.total - this.data[randomIndex];
				this.total += extraData[i];
				this.data[randomIndex] = extraData[i];

			}
		}
		mean = this.total / (double) this.size;
		weight++;
	}

	/**
	 * sets the mean of a pattern
	 * 
	 * @param m
	 *            value to set pattern mean to pattern mean
	 */
	public void setMean(double m) {
		mean = m;
	}

	/**
	 * sets the variance of a pattern
	 * 
	 * @param v
	 *            value to set pattern variance to pattern variance
	 */
	public void setVariance(double v) {
		variance = v;
	}

	/**
	 * gets the mean of a pattern
	 * 
	 * @return pattern mean
	 */
	public double getMean() {
		return mean;
	}

	/**
	 * gets the variance of a pattern
	 * 
	 * @return pattern variance
	 */
	public double calcVariance() {
		variance = StatUtils.variance(data, mean, 0, this.size);
		return variance;
	}

	public double[] getData() {
		return data;
	}

	public int getSize() {
		return size;
	}

	public double getTotal() {
		return total;
	}

	public int getWeight() {
		return weight;
	}

	public double getMeanDifference(Pattern p2) {
		double m2 = p2.getMean();
		return Math.abs(mean - m2);
	}

	/**
	 * test for pattern equality using the KS test from R
	 * 
	 * @param p2
	 *            pattern to be compared to
	 * @return true if patterns are equivalent, or false otherwise
	 */
	public boolean equals(Pattern p2) {
		int size1 = this.size;
		int size2 = p2.getSize();

		double[] data1 = new double[size1];
		for (int i = 0; i < size1; i++) {
			data1[i] = this.data[i];
		}

		double[] data2 = new double[size2];
		for (int i = 0; i < size2; i++) {
			data2[i] = p2.getData()[i];
		}

		double rDiff = getStatistic(data1, data2);

		if (rDiff > getKSBound(size1, size2)) {
			return false;
		} else {
			return true;
		}

	}

	public double computeD(Pattern p2) {
		int size1 = this.size;
		int size2 = p2.getSize();

		double[] data1 = new double[size1];
		for (int i = 0; i < size1; i++) {
			data1[i] = this.data[i];
		}

		double[] data2 = new double[size2];
		for (int i = 0; i < size2; i++) {
			data2[i] = p2.getData()[i];
		}

		return getStatistic(data1, data2);
	}

	private double getKSBound(double n1, double n2) {
		double sum = n1 + n2;
		double product = n1 * n2;
		return getConfidence(ALPHA) * Math.sqrt(sum / product);
	}

	private double getConfidence(double alphaValue) {
		double epsilon = Math.pow(10, -15);
		for (int i = 0; i < KSTable[0].length; i++) {
			double diff = alphaValue - KSTable[0][i];
			if (diff < epsilon) {
				return KSTable[1][i];
			}
		}
		throw new IllegalArgumentException("Invalid alpha parameter for KS confidence table.");
	}

	public String toString() {
		double[] interval = getConfidenceBounds();
		double lower = interval[0];
		double upper = interval[1];
		return getMean() + "\t" + calcVariance() + "\t" + ALPHA + "\t" + lower + "\t" + upper;
	}

	@Override
	public int compareTo(Pattern t) {
		if (mean < t.getMean()) {
			return -1;
		} else {
			return 1;
		}
	}

	public void merge(Pattern other) {
		double[] extraData = other.getData();
		for (int i = 0; i < other.getSize(); i++) {
			double value = extraData[i];
			if (this.size < this.data.length) {
				this.data[this.size] = value;
				this.total += value;
				this.size++;
			} else {
				int randomIndex = random.nextInt(this.data.length);
				this.total = this.total - this.data[randomIndex];
				this.total += value;
				this.data[randomIndex] = value;
			}
		}

		mean = this.total / (double) this.size;
		variance = StatUtils.variance(data, mean, 0, this.size);
	}

	public void addLength(int patternLength, boolean compressed) {
		lengthTotal += patternLength;
		if (compressed == false) {
			lengthNum++;
		}
	}

	public double getAverageLength() {
		return lengthTotal / lengthNum;
	}

	/**
	 * gets drift location estimates as a confidence interval
	 * 
	 * @return lower and upper bounds and average length of pattern
	 */
	public double[] getConfidenceBounds() {
		double sd = Math.sqrt(calcVariance());
		double upper = getMean() + CONFIDENCE * sd;
		double lower = getMean() - CONFIDENCE * sd;
		double[] bounds = { lower, upper, getAverageLength() };
		return bounds;
	}

	public boolean isAbrupt() {
		return this.ABRUPT; // defaults to gradual
	}

	/**
	 * gets the true variance of the pattern (ground truth)
	 * 
	 * @return true variance for stream generation
	 */
	public double getRealVariance() {
		return variance;
	}

}
