/*
 * VolatilityDetection.java
 * Authors: Kylie Chen - The University of Auckland
 * 			David T.J. Huang - The University of Auckland
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

package summer.proSeed.VolatilityDetection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.PatternMining.Network.SeveritySamplingEdgeInterface;

 /**
 * 
 * Proactive drift detection method as published in:
 * Kylie Chen, Yun Sing Koh, and Patricia Riddle. Proactive Drift Detection: Predicting Concept Drifts in Data Streams using Probabilistic Networks.  
 * In Proceedings of the International Joint Conference on Neural	Networks (IJCNN), 2016.
 * 
 * @author Kylie Chen - The University of Auckland
 * @author David T.J. Huang - The University of Auckland
 * @version 2.0
 */
public class RelativeVolatilityDetector {

	private long sample = 0;

	private CutPointDetector cutpointDetector;
	private Reservoir reservoir;
	private Buffer buffer;
	private double confidence;
	private int timestamp = 0;
	
	private long previousDriftPoint = -1;
	
	private ArrayList<Double> severityBuffer;

	private int patternLength = 0;

	private DriftPrediction driftPredictor;
	private int rollingIndex = 0;
	private double[] recentIntervals = new double[100]; // default rolling window size

	private boolean driftFound = false;

	private boolean volatilityDriftFound;

	private int severityGracingPeriod = 0;
	

	public void setRecentIntervals(int size) {
		recentIntervals = new double[size];
	}

	public RelativeVolatilityDetector(CutPointDetector cutpointDetector, int resSize) throws FileNotFoundException, IOException {
		this.cutpointDetector = cutpointDetector;
		this.reservoir = new Reservoir(resSize);
		this.buffer = new Buffer(resSize);
		this.confidence = 0.05;
		this.driftPredictor = new DriftPrediction(50, 100, 0.05, 100, 1000, true); // use default drift predictor
		setRecentIntervals(resSize * 2);
	}

	public RelativeVolatilityDetector(CutPointDetector cutpointDetector, int resSize, double confidence) throws FileNotFoundException, IOException {
		this.cutpointDetector = cutpointDetector;
		this.reservoir = new Reservoir(resSize);
		this.buffer = new Buffer(resSize);
		this.confidence = confidence;
		this.driftPredictor = new DriftPrediction(50, 100, 0.05, 100, 1000, true); // use default drift predictor
		setRecentIntervals(resSize * 2);
	}

	public RelativeVolatilityDetector(CutPointDetector cutpointDetector, int resSize, DriftPrediction driftPredictor) throws FileNotFoundException, IOException {
		this.cutpointDetector = cutpointDetector;
		this.reservoir = new Reservoir(resSize);
		this.buffer = new Buffer(resSize);
		this.confidence = 0.05;
		this.driftPredictor = driftPredictor;
		setRecentIntervals(resSize * 2);
	}

	// I am using this constructor. 
	public RelativeVolatilityDetector(CutPointDetector cutpointDetector, int resSize, double confidence, DriftPrediction driftPredictor) throws FileNotFoundException, IOException {
		this.cutpointDetector = cutpointDetector;
		this.reservoir = new Reservoir(resSize);
		this.buffer = new Buffer(resSize);
		this.confidence = confidence;
		this.driftPredictor = driftPredictor;
		setRecentIntervals(resSize * 2);
		
		this.severityBuffer = new ArrayList<Double>(1000);
	}

	public CutPointDetector getDetector() {
		return this.cutpointDetector;
	}

	public DriftPrediction getPredictor() {
		return this.driftPredictor;
	}

	public int getTimeStamp() {
		return timestamp;
	}

	public boolean getDriftFound() {
		return driftFound;
	}

	public boolean getVolatilityDriftFound()
	{
		return this.volatilityDriftFound;
	}
	/**
	 * Input
	 * @param inputValue
	 * @return
	 * @throws IOException
	 */
	
	public double getCurrentBufferMean()
	{
		return this.buffer.getMean();
	}
	public Boolean setInputVarViaBuffer(double inputValue) throws IOException {
		sample++;
		driftPredictor.getPatternReservoir().incrementTimeStayingInCurrentState();

		driftFound = cutpointDetector.setInput(inputValue);
		if (driftFound) {
			if (buffer.isFull()) {
				reservoir.addElement(buffer.add(++timestamp));
			} else {
				buffer.add(++timestamp);
			}

			double interval = timestamp;

			// most recent intervals
			recentIntervals[rollingIndex] = interval;
			rollingIndex++;
			if (rollingIndex == recentIntervals.length) {
				rollingIndex = 0;
			}
			
			if(sample-previousDriftPoint>severityGracingPeriod)
			{
				severityBuffer.add(cutpointDetector.getSeverity());
			}
			
			timestamp = 0;
			
			previousDriftPoint = sample;
			
			if (buffer.isFull() && reservoir.isFull()) {
				double RelativeVar = buffer.getStdev() / reservoir.getReservoirStdev();
				if (RelativeVar > 1.0 + confidence || RelativeVar < 1.0 - confidence) { // find a volatility drift 
					// pass output to drift predictor
					// add severity
					double[] array = new double[severityBuffer.size()];
					for(int i=0;i<severityBuffer.size();i++)
					{
						array[i] = severityBuffer.get(i);
					}
					driftPredictor.addNewPattern(interval, buffer, recentIntervals, sample, patternLength, array);

					// clear reservoir
					// clear severity buffer
					
					reservoir.clear();
					severityBuffer.clear();
					patternLength = 0;
					
					this.volatilityDriftFound = true;
					return true;
				} else {
					patternLength++;
					
					this.volatilityDriftFound = false;
					return false;
				}
			}

			patternLength++;

		} else {
			timestamp++;
			patternLength++;
			
			this.volatilityDriftFound = false;
			return false;
		}
		
		this.volatilityDriftFound = false;
		return false;
	}

	public Boolean setInputIntervalViaBuffer(double interval) throws IOException {
		if (buffer.isFull()) {
			reservoir.addElement(buffer.add(interval));
		} else {
			buffer.add(interval);
		}

		// most recent intervals
		recentIntervals[rollingIndex] = interval;
		rollingIndex++;
		if (rollingIndex == recentIntervals.length) {
			rollingIndex = 0;
		}

		timestamp = 0;

		if (buffer.isFull() && reservoir.isFull()) {
			double RelativeVar = buffer.getStdev() / reservoir.getReservoirStdev();
			if (RelativeVar > 1.0 + confidence || RelativeVar < 1.0 - confidence) {
				// pass output to drift predictor
				driftPredictor.addNewPattern(interval, buffer, recentIntervals, sample, patternLength, null);

				// clear reservoir
				reservoir.clear();
				patternLength = 0;
				return true;
			} else {
				patternLength++;
				return false;
			}
		}

		patternLength++;

		return false;

	}
	
	public DriftPrediction getDriftPrediction()
	{
		return this.driftPredictor;
	}


}
