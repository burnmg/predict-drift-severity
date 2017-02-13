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

    private int randomSeed;
    private Random random;
    private double p; // probability of success (expected value)  

    private int sample;
    private int streamLength = Integer.MAX_VALUE;
    private double noiseProb;

    public BernoulliGenerator(double prob) {
        sample = 0;
        p = prob;
        randomSeed = DEFAULT_SEED;
        random = new Random(DEFAULT_SEED);
        noiseProb = 0.0;
    }

    public BernoulliGenerator(double prob, int seed) {
        sample = 0;
        p = prob;
        randomSeed = seed;
        random = new Random(seed);
        noiseProb = 0.0;
    }

    public BernoulliGenerator(double prob, double noise, int seed) {
        sample = 0;
        p = prob;
        randomSeed = seed;
        random = new Random(seed);
        noiseProb = noise;
    }



    public double getMean() {
        return p;
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

    @Override
    public double generateNext() {
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

	@Override
	public void addDrift(double driftSeverity)
	{
		p = p + driftSeverity;
		if(p>1) p = 1;
		if(p<0) p = 0;		
	}

	@Override
	public void setSeed(int seed)
	{
		random = new Random(seed);	
	}
}
