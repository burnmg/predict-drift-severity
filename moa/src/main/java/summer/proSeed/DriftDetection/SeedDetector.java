/*
 * SeedDetector.java
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
 
package summer.proSeed.DriftDetection;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

 /**
 * 
 * Proactive drift detection method as published in:
 * Kylie Chen, Yun Sing Koh, and Patricia Riddle. Proactive Drift Detection: Predicting Concept Drifts in Data Streams using Probabilistic Networks.  
 * In Proceedings of the International Joint Conference on Neural	Networks (IJCNN), 2016.
 * 
 * Usage: setInput(double)
 * 
 * @author Kylie Chen - The University of Auckland
 * @author David T.J. Huang - The University of Auckland
 * @version 2.0
 */
public class SeedDetector implements CutPointDetector {
	public SeedWindow window;
	private double DELTA;
	private int defaultBlockSize;
	private int blockSize;
	private int elementCount;

	// Testing purpose public variable
	public long checks;
	public int warningCount = 0;

	private double[][] predictions;
	
	private int samples = 0;
	private BufferedWriter writer;
	
	private double maxMeanPrediction;

	public SeedDetector(double delta, int blockSize) {
		this.DELTA = delta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize);		
	}

	public SeedDetector(double delta, int blockSize, int decayMode, int compressionMode, double epsilonHat,
			double alpha, int term) {
		this.DELTA = delta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize, decayMode, compressionMode, epsilonHat, alpha, term);
	}

	@Override
	public void setPredictions(double[][] predictions) {
		if (predictions != null) {
			this.predictions = predictions;		
			this.maxMeanPrediction = findMaxMean(this.predictions); // update maximum mean
		}
	}

	private double findMaxMean(double[][] predictions2) {
		double max = 0.0;
		for (int i = 0; i < predictions[0].length; i++) {
			double mean = (predictions[0][i] + predictions[1][i]) / 2.0;
			if (mean > max) {
				max = mean;
			}
		}
		return max;
	}

	public void setUpWriter(String s) throws IOException {
		writer = new BufferedWriter(new FileWriter(s + ".txt"));
	}
	
	public void closeWriter() throws IOException {
		writer.close();
	}
	
	@Override
	public boolean setInput(double inputValue) {
		SeedBlock cursor;

		addElement(inputValue);
		
		samples++;

		if (elementCount % blockSize == 0 && window.getBlockCount() >= 2) 
		{
			boolean blnReduceWidth = true;

			while (blnReduceWidth) {

				blnReduceWidth = false;
				int n1 = 0;
				int n0 = window.getWidth();
				double u1 = 0;
				double u0 = window.getTotal();

				cursor = window.getTail();
				while (cursor.getPrevious() != null) {
					n0 -= cursor.getItemCount();
					n1 += cursor.getItemCount();
					u0 -= cursor.getTotal();
					u1 += cursor.getTotal();
					double diff = Math.abs(u1 / n1 - (u0 / n0));

					checks++;
					if (diff > getADWINBound(n0, n1)) {				
						blnReduceWidth = true;
						window.resetDecayIteration();
						window.setHead(cursor);

						while (cursor.getPrevious() != null) {
							cursor = cursor.getPrevious();
							window.setWidth(window.getWidth() - cursor.getItemCount());
							window.setTotal(window.getTotal() - cursor.getTotal());
							window.setVariance(window.getVariance() - cursor.getVariance());
							window.setBlockCount(window.getBlockCount() - 1);
						}

						window.getHead().setPrevious(null);
						
						return true;
					}

					cursor = cursor.getPrevious();
				}
			}
		}

		return false;
	}

	private double getADWINBound(double n0, double n1) {
		double n = n0 + n1;
		double dd = Math.log(2 * Math.log(n) / DELTA);
		double v = window.getVariance() / window.getWidth();
		double m = (1 / (n0)) + (1 / (n1));
		double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;

		return epsilon;
	}

	public void addElement(double value) {
		window.addTransaction(value, predictions, maxMeanPrediction);
		elementCount++;
	}

	public long getChecks() {
		return checks;
	}
}
