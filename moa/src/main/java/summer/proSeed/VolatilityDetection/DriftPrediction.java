/*
 * DriftPrediction.java
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

package summer.proSeed.VolatilityDetection;

import java.util.ArrayList;
import java.util.function.DoublePredicate;

import org.apache.commons.math3.stat.StatUtils;

import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.PatternReservoir;
import summer.proSeed.PatternMining.PatternTransition;
import summer.proSeed.PatternMining.Network.SeverityReservoirSampingEdge;
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
public class DriftPrediction {
    private PatternReservoir patternReservoir;

    private int patternLength = 0;

    public int predictionCorrect = 0;
    public int numPredicted = 0;
    
    private double ksDelta = 0.05; //  ks test default delta for pattern matching
    private int numOfFrequentTransitions; // may not need to adjust this
    
    private ArrayList<PatternTransition> frequentList;    
    private double[] currentBounds;
    private double currentPatternLength;
    private Pattern currentPattern;
    private double[][] nextBounds;
    
    /**
     * 
     * @param m merge parameter. default: 3. Merge to 3 patterns
     * @param patternReservoirSize
     * @param ksConfidence
     * @param freqTransitions K for the top K.
     */
    public DriftPrediction(int m, int patternReservoirSize, double ksConfidence, int freqTransitions, int severitySampleSize) {    	
    	patternReservoir = new PatternReservoir(patternReservoirSize, severitySampleSize);
    	patternReservoir.setMergeParameter(m);
    	ksDelta = ksConfidence;
    	numOfFrequentTransitions = freqTransitions;
    }
    
    public void setSlopeCompression(double slope) {
    	patternReservoir.setSlopeCompression(slope);
    }
       
    public void setFrequentTransitionNumber(int number) {
        numOfFrequentTransitions = number;
    }

    public void setPatternDelta(double delta) {
        ksDelta = delta;
    }
    
    private void updateFrequentPatternsList(int currentPatternIndex) {
    	frequentList = this.patternReservoir.getNetwork().getTopKTransitionIndices(currentPatternIndex, numOfFrequentTransitions);    	
    	currentBounds = currentPattern.getConfidenceBounds();
        currentPatternLength = currentPattern.getAverageLength();
        if (frequentList == null) {
        	return;
        }
        nextBounds = new double[frequentList.size()][3];
        int count = 0;
        for (PatternTransition topPattern : frequentList) {
            Pattern nextPattern = this.patternReservoir.getPatterns()[topPattern.getIndex()];
            nextBounds[count] = nextPattern.getConfidenceBounds();            
            count++;
        }
    }
     
    private double[] estimateBounds(int freqIndex, double time) {
    	double[] next = nextBounds[freqIndex];
        double estimatedLower;
        double estimatedUpper;                
        if (currentPattern.isAbrupt() == true) {
            estimatedUpper = next[1]; // abrupt transitions
            estimatedLower = next[0]; 
        } else {        
	        double slopeUpper = (next[1] - currentBounds[1]) / next[2]; // gradual transitions
	        double slopeLower = (next[0] - currentBounds[0]) / next[2];
			estimatedUpper = currentBounds[1] + slopeUpper * time; 
			estimatedLower = currentBounds[0] + slopeLower * time; 
        }
        double[] result = {estimatedLower, estimatedUpper};
        return result;
    }
    
    public double[][] predictNextCI(boolean volatilityChanged, double timestep) {
        int latestPatternIndex = this.patternReservoir.getLatestPatternIndex();
        if (latestPatternIndex == -1) {
            return null;
        }

        if (volatilityChanged) {
        	currentPattern = this.patternReservoir.getPatterns()[latestPatternIndex];
        	updateFrequentPatternsList(latestPatternIndex);
        }
        
        if (frequentList == null) {
            return null;
        }        

        double[][] confidenceIntervals = new double[3][frequentList.size()];
        // row 0 lower bound
        // row 1 upper bound          
        for (int i = 0; i < nextBounds.length; i++) {            
			// estimate bounds
            double[] estimates = estimateBounds(i, timestep);
            confidenceIntervals[0][i] = estimates[0];
            confidenceIntervals[1][i] = estimates[1];
        }
        return confidenceIntervals;
    }

    /* pattern matching via buffer
     * perform the pattern match before inserting a new pattern
     */
    public void addNewPattern(double interval, Buffer buffer, double[] recentIntervals, long sample, int patternLength, double[] severitySample) {
    	
        double[] buffData = buffer.getBuffer();
        // remove outliers
        double q1 = StatUtils.percentile(buffData, 25);
        double q3 = StatUtils.percentile(buffData, 75);
        double iqr = q3 - q1;

        if (sample > recentIntervals.length) {
            // recent interval quartiles
            double rq1 = StatUtils.percentile(recentIntervals, 25);
            double rq3 = StatUtils.percentile(recentIntervals, 75);
            double riqr = rq3 - rq1;

            if (riqr <= iqr) {
                buffData = recentIntervals;
                q1 = rq1;
                q3 = rq3;
                iqr = riqr;
            }
        }

        double lowerBound = q1 - (1.5 * iqr);
        double upperBound = q3 + (1.5 * iqr);

        double[] nonOutliers = new double[buffData.length];
        int outlierCount = 0;
        int nonOutlierCount = 0;

        for (double d : buffData) {
            if (d < lowerBound || d > upperBound) {
                outlierCount++; // outlier
            } else {
                nonOutliers[nonOutlierCount] = d;
                nonOutlierCount++;
            }
        }
        
        int patternIndex = patternReservoir.addPattern(nonOutliers, nonOutlierCount, patternLength, severitySample);
        boolean compressed = patternReservoir.getCompression();
        patternReservoir.getPatterns()[patternIndex].addLength(patternLength, compressed);
        
    }

	public PatternReservoir getPatternReservoir() {
		return this.patternReservoir;
	}
	
	public SeveritySamplingEdgeInterface[][] getNetworkEdges()
	{
		return this.patternReservoir.getNetwork().getEdges();
	}



	public double getDeltaCoefficient()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
