/*
 * GradualNetworkStream.java
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

import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Network.ProbabilisticNetwork;

import java.util.Random;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class GradualNetworkStream {

    public int numNoisyTransitions = 0;

    public double networkNoise = 0.0;
    public double intervalNoise = 0.0;
    public double noiseStandardDeviation = 0.0;
    public int stateTimeLength = 100;

    private Pattern[] states;
    private int stateTime = 0;
    private int previousStateIndex = -1;
    private int currentStateIndex;
    private int instances;

    private final int DEFAULT_RANDOM_SEED = 666;
    private int randomSeed;
    private Random random;

    private double[][] network;
    private double[] initialStates;

    private ProbabilisticNetwork actualNetwork;
    private DescriptiveStatistics[] actualStates;
    private int nextStateIndex;

	private boolean moveToNewState = false;

    public GradualNetworkStream(double[][] networkProbabilities, Pattern[] states) {
        this.network = networkProbabilities;
        this.initialStates = new double[this.network.length];
        setEqualProbabilities(this.initialStates);
        this.randomSeed = DEFAULT_RANDOM_SEED;
        this.random = new Random(this.randomSeed);
        this.instances = 0;
        this.states = states;
        this.actualNetwork = new ProbabilisticNetwork(states.length);
        this.actualNetwork.setNumberOfPatterns(states.length);
        this.actualStates = new DescriptiveStatistics[states.length];
    }

    public GradualNetworkStream(double[][] networkProbabilities, double[] initialProbabilities, Pattern[] states) {
        this.network = networkProbabilities;
        this.initialStates = initialProbabilities;
        this.randomSeed = DEFAULT_RANDOM_SEED;
        this.random = new Random(this.randomSeed);
        this.instances = 0;
        this.states = states;
        this.actualNetwork = new ProbabilisticNetwork(states.length);
        this.actualNetwork.setNumberOfPatterns(states.length);
        this.actualStates = new DescriptiveStatistics[states.length];
    }

    public GradualNetworkStream(double[][] networkProbabilities, Pattern[] states, int seed) {
        this.network = networkProbabilities;
        this.initialStates = new double[this.network.length];
        setEqualProbabilities(this.initialStates);
        this.randomSeed = seed;
        this.random = new Random(this.randomSeed);
        this.instances = 0;
        this.states = states;
        this.actualNetwork = new ProbabilisticNetwork(states.length);
        this.actualNetwork.setNumberOfPatterns(states.length);
        this.actualStates = new DescriptiveStatistics[states.length];
    }

    public GradualNetworkStream(double[][] networkProbabilities, double[] initialProbabilities, Pattern[] states, int seed) {
        this.network = networkProbabilities;
        this.initialStates = initialProbabilities;
        this.randomSeed = seed;
        this.random = new Random(this.randomSeed);
        this.instances = 0;
        this.states = states;
        this.actualNetwork = new ProbabilisticNetwork(states.length);
        this.actualNetwork.setNumberOfPatterns(states.length);
        this.actualStates = new DescriptiveStatistics[states.length];
    }

    private void setEqualProbabilities(double[] prob) {
        for (int i = 0; i < prob.length; i++) {
            prob[i] = 1.0 / prob.length;
        }
    }

    public void reset() {
        this.random.setSeed(this.randomSeed);
        this.instances = 0;
        this.stateTime = 0;
    }

    public int generateNext() {
        int trueStateIndex = -1;
        moveToNewState  = false;
        double rand = this.random.nextDouble();
        if (this.instances == 0) {
            // choose initial state
            currentStateIndex = selectState(rand, this.initialStates);

            double[] transitions = this.network[currentStateIndex];
            nextStateIndex = selectState(rand, transitions);

            stateTime = 1;
        } else {
            if (stateTime % stateTimeLength == 0) {
                // choose new state
                currentStateIndex = nextStateIndex;

                moveToNewState = true;
                double[] transitions = this.network[currentStateIndex];
                nextStateIndex = selectState(rand, transitions);

                stateTime = 1;
            } else {
                // stay in current state
                moveToNewState = false;
                stateTime++;
            }
        }

        // save true state index
        trueStateIndex = currentStateIndex;

        // generate cut point interval
        double mean1 = this.states[currentStateIndex].getMean();
        double sd1 = Math.sqrt(this.states[currentStateIndex].getRealVariance());
        
        double mean2 =  this.states[nextStateIndex].getMean();
        double slope = (mean2 - mean1) / (double) stateTimeLength;
        
        double interval = mean1 + this.random.nextGaussian() * sd1 + slope * (double) stateTime;

        // add noise to generated cut point interval
        rand = this.random.nextDouble();
        if (rand < this.intervalNoise) {
            interval = interval + this.random.nextGaussian() * this.noiseStandardDeviation;
        }

        // store state (includes noise around intervals)
        storeState(currentStateIndex, interval);

        // store true underlying network
        if (moveToNewState == true || instances == 0) {
            storeNetwork(trueStateIndex);
            this.previousStateIndex = currentStateIndex;
            this.actualNetwork.setPreviousPatternIndex(currentStateIndex);
        }

        this.instances++;

        return (int) interval;
    }

    private int selectState(double rand, double[] probabilities) {
        double cumProb = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumProb += probabilities[i];
            if (rand < cumProb) {
                return i;
            }
        }
        return -1;
    }

    private void storeState(int index, double interval) {
        if (this.actualStates[index] == null) {
            this.actualStates[index] = new DescriptiveStatistics();
        } else {
            this.actualStates[index].addValue(interval);
        }
    }

    private void storeNetwork(int current) {
        this.actualNetwork.incrementTransition(current);
    }

    private Pattern[] getStates() {
        Pattern[] patterns = new Pattern[this.states.length];
        for (int i = 0; i < patterns.length; i++) {
            double mean = this.actualStates[i].getMean();
            double variance = this.actualStates[i].getVariance();
            patterns[i] = new Pattern(mean, variance);
        }

        return patterns;
    }

    public String getStatesString() {
        String result = "States\nIndex\tMean\tVariance\n";
        Pattern[] patternRes = getStates();
        int i = 0;
        for (Pattern p : patternRes) {
            result = result + i + "\t" + p + "\n";
            i++;
        }
        return result;
    }

    public ProbabilisticNetwork getActualNetwork() {
        return this.actualNetwork;
    }
    
    public void setStateTimeMean(int time) {
    	this.stateTimeLength = time;
    }
    
    public int getStateTimeMean() {
    	return this.stateTimeLength;
    }
    
    public boolean hasChanged() {
    	return moveToNewState;
    }

}
