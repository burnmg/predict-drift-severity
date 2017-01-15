/*
 * BernoulliGenerator.java
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

public class BernoulliGenerator implements StreamGenerator {

    private final int DEFAULT_SEED = 666;
    private boolean lowMean;

    private int randomSeed;
    private Random random;
    private double p; // probability of success (expected value)  

    private int sample;
    private int streamLength = Integer.MAX_VALUE;
    private double noiseProb;

    private final double MEAN_HIGH = 0.8;
    private final double MEAN_LOW = 0.2;

    public BernoulliGenerator(double prob) {
        sample = 0;
        p = prob;
        randomSeed = DEFAULT_SEED;
        random = new Random(DEFAULT_SEED);
        lowMean = true;
        noiseProb = 0.0;
    }

    public BernoulliGenerator(double prob, int seed) {
        sample = 0;
        p = prob;
        randomSeed = seed;
        random = new Random(seed);
        lowMean = true;
        noiseProb = 0.0;
    }

    public BernoulliGenerator(double prob, double noise, int seed) {
        sample = 0;
        p = prob;
        randomSeed = seed;
        random = new Random(seed);
        lowMean = true;
        noiseProb = noise;
    }

    public void swapMean() {
        if (lowMean) {
            lowMean = false; // change mean from low to high
            p = MEAN_HIGH;
        } else {
            lowMean = true; // change mean from high to low
            p = MEAN_LOW;
        }
    }

    public double getMean() {
        return p;
    }

    public boolean getLowMean() {
        return lowMean;
    }

    public void setLowMean(boolean b) {
        lowMean = b;
    }

    public void setMean(double prob) {
        p = prob;
    }
    
    public void setNoise(double noise) {
    	this.noiseProb = noise;
    }

    public long getSeed() {
        return randomSeed;
    }

    public int getNumSamples() {
        return sample;
    }

    public int getStreamLength() {
        return streamLength;
    }

    public void setStreamLength(int l) {
        streamLength = l;
    }

    public boolean hasNext() {
        if (sample < streamLength) {
            return true;
        }
        return false;
    }

    public int generateNext() {
        int output;
        double rand = this.random.nextDouble();
        sample++;
        if (rand < p) {
            output = 1;
        } else {
            output = 0;
        }
        if (noiseProb > 0) {
            rand = this.random.nextDouble();
            if (rand < noiseProb) {
                // add noise
                if (output == 1) {
                    output = 0;
                } else if (output == 0) {
                    output = 1;
                }
            }
        }
        return output;
    }

    public void restart() {
        this.random = new Random(this.randomSeed);
        sample = 0;
    }
}
