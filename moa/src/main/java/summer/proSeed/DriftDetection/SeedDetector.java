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

import volatilityevaluation.BufferInterface;
import volatilityevaluation.LimitedBuffer;
import volatilityevaluation.UnlimitedBuffer;

/**
 * 
 * Proactive drift detection method as published in: Kylie Chen, Yun Sing Koh,
 * and Patricia Riddle. Proactive Drift Detection: Predicting Concept Drifts in
 * Data Streams using Probabilistic Networks. In Proceedings of the
 * International Joint Conference on Neural Networks (IJCNN), 2016.
 * 
 * Usage: setInput(double)
 * 
 * @author Kylie Chen - The University of Auckland
 * @author David T.J. Huang - The University of Auckland
 * @version 2.0
 */
public class SeedDetector implements CutPointDetector
{
	public SeedWindow window;
	private double DELTA;
	private double DELTACoefficient = 1; 

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
	private boolean warning;
	
	private double p = 1;
	private double s = 0;
	private double pTotal = 0;
	private int instCount = 0;
	private int windowSize = 100;
	private int[] errorWindow = new int[windowSize];
	private double pMin = Double.MAX_VALUE;
	private double sMin = Double.MAX_VALUE;
	
	private double WARNING_DELTA;
	private double warningConfidence = 2.0;
	
	private double severity;
	BufferInterface warningBuffer; // unlimited size sliding window. 
	BufferInterface preWarningBuffer; // fixed size sliding window
	private int preWarningBufferSize;
	private int coolingPeriod;
	
	private double previousSnapshot;

	
	public SeedDetector(double delta, int blockSize)
	{
		this.DELTA = delta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize);
	}

	// return new summer.originalSeed.SeedDetector(0.05, 32, 1, 1, 0.01, 0.8, 75); // Seed Best
	public SeedDetector(double delta, int blockSize, int decayMode, int compressionMode, double epsilonHat,
			double alpha, int term)
	{
		this.DELTA = delta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize, decayMode, compressionMode, epsilonHat, alpha, term);
	}
	
	
	/**
	 * 
	 * @param delta 0.05
	 * @param warningDelta 0.1
	 * @param blockSize
	 * @param decayMode
	 * @param compressionMode
	 * @param epsilonHat
	 * @param alpha
	 * @param term
	 * @param preWarningBufferSize 32
	 */
	public SeedDetector(double delta, double warningDelta, int blockSize, int decayMode, int compressionMode, double epsilonHat,
			double alpha, int term, int preWarningBufferSize, int coolingPeriod)
	{
		this.DELTA = delta;
		this.WARNING_DELTA = warningDelta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize, decayMode, compressionMode, epsilonHat, alpha, term);
	
		preWarningBuffer = new LimitedBuffer(preWarningBufferSize);
		warningBuffer = new UnlimitedBuffer(preWarningBufferSize); // assign a same size for warning buffer for optimisation.
		this.preWarningBufferSize = preWarningBufferSize;
		
		this.coolingPeriod = coolingPeriod;
	}


	
	@Override
	public void setPredictions(PredictionModel predictions)
	{ 
		if (predictions != null)
		{
			this.predictions = predictions.predictionDriftPoints;
			this.maxMeanPrediction = findMaxMean(this.predictions); // update
																	// maximum
																	// mean
			this.DELTACoefficient = predictions.deltaCoefficient; 
		}
		
	}

	private double findMaxMean(double[][] predictions2)
	{
		double max = 0.0;
		for (int i = 0; i < predictions[0].length; i++)
		{
			double mean = (predictions[0][i] + predictions[1][i]) / 2.0;
			if (mean > max)
			{
				max = mean;
			}
		}
		return max;
	}

	public void setUpWriter(String s) throws IOException
	{
		writer = new BufferedWriter(new FileWriter(s + ".txt"));
	}

	public void closeWriter() throws IOException
	{
		writer.close();
	}

	@Override
	public boolean setInput(double inputValue)
	{
		SeedBlock cursor;

		addElement(inputValue);

		samples++;

		if (elementCount % blockSize == 0 && window.getBlockCount() >= 2)
		{
			boolean blnReduceWidth = true;
			boolean keepWarning = false;

			while (blnReduceWidth)
			{

				boolean warningFound = false;
				blnReduceWidth = false;
				int n1 = 0;
				int n0 = window.getWidth();
				double u1 = 0;
				double u0 = window.getTotal();

				cursor = window.getTail();

				// diff check for each block splits.
				while (cursor.getPrevious() != null)
				{
					n0 -= cursor.getItemCount();
					n1 += cursor.getItemCount();
					u0 -= cursor.getTotal();
					u1 += cursor.getTotal();
					double diff = Math.abs(u1 / n1 - (u0 / n0));

					checks++;

					// if find a drift.
					// transform the DELTA with the coefficent
					double threshold = getADWINBound(n0, n1, DELTA*DELTACoefficient);
					if (samples%coolingPeriod==0 && diff > threshold)
					{
						blnReduceWidth = true;
						window.resetDecayIteration();
						window.setHead(cursor);

						while (cursor.getPrevious() != null)
						{
							// remove all previous cursor.
							cursor = cursor.getPrevious();
							window.setWidth(window.getWidth() - cursor.getItemCount());
							window.setTotal(window.getTotal() - cursor.getTotal());
							window.setVariance(window.getVariance() - cursor.getVariance());
							window.setBlockCount(window.getBlockCount() - 1);
						}

						window.getHead().setPrevious(null);
						
						setWarningFlags(false); // reset warning flags
						pMin = Double.MAX_VALUE;
						sMin = Double.MAX_VALUE;

						/*
						 * Warning approach
						 */
						// compute severity
						// severity = (warningBuffer.getMean() - preWarningBuffer.getMean())/warningBuffer.size();
						// severity = Math.atan((u1/n1 - u0/n0) / ((n1+n0)/2));
						// severity = sigmoid(((u1/n1 - u0/n0) / ((n1+n0)/2)));
						// severity = (u1/n1 - u0/n0) / ((n1+n0)/2);
						
						// reset
						preWarningBuffer.addAll(warningBuffer);
						preWarningBuffer.add(inputValue); // if there is a drift, add the inputValue to the preWarning buffer used in the future. 
						warningBuffer.clear();
						
						/*
						 * Snapshot approach
						 */
						
						severity = Math.abs(u1 / n1 - previousSnapshot);
						previousSnapshot = u1 / n1;
						return true;
					}
					// else if (currently is warning)
					else if(warning)
					{
						if(!keepWarning && diff>getADWINBound(n0, n1, WARNING_DELTA))
						{
							keepWarning = true;
						}
					}
					// else if (currently is not warning)
					else if(!warning && !warningFound && diff > getADWINBound(n0, n1, WARNING_DELTA))
					{
						warningFound = true;
						keepWarning = true; // warning detected
						setWarningFlags(true); // set warning flags		
					}
					
					

					cursor = cursor.getPrevious();
				}
				
				if (keepWarning == false)
				{
					setWarningFlags(false); // reset warning flags
					// reset warning buffer TODO
					warningBuffer = new UnlimitedBuffer(preWarningBufferSize); 
				}
			}
		}
		
		if (warning == false)
		{
			// window.checkCompression(); // block compression check
		}
		
		if(warning)
		{
			warningBuffer.add(inputValue);
		}
		else
		{
			preWarningBuffer.add(inputValue);
		}

		return false;
	}

	private void setWarningFlags(boolean b)
	{
		warning = b;
	}

	private double getADWINBound(double n0, double n1, double delta)
	{
		double n = n0 + n1;
		double dd = Math.log(2 * Math.log(n) / delta);
		double v = window.getVariance() / window.getWidth();
		double m = (1 / (n0)) + (1 / (n1));
		double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;

		return epsilon;
	}

	public void addElement(double value)
	{
		window.addTransaction(value, predictions, maxMeanPrediction);
		elementCount++;
		
		instCount++; // number of instances in sliding window

		int windowPos = (instCount - 1) % windowSize ;
		pTotal -= errorWindow[windowPos];
		errorWindow[windowPos] = (int) value;
		pTotal += value;

		if (instCount >= windowSize)
		{
			p = pTotal / windowSize;
			s = Math.sqrt(p * (1 - p) / windowSize);
			if ((p + s) < (pMin + sMin))
			{
				pMin = p;
				sMin = s;
			}
		}

		if ((p + s) > pMin + warningConfidence * sMin)
		{
			warning = true;
		} else
		{
			warning = false;
		}
	}

	public long getChecks()
	{
		return checks;
	}

	@Override
	public double getSeverity()
	{
		return this.severity;
	}

	@Override
	public void setPredictions(double[][] predictions)
	{
	
	}

	
}
