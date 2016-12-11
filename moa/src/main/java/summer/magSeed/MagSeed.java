package summer.magSeed;
import org.apache.commons.math3.analysis.function.Sigmoid;

/*
 * MagSeed.java
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
import volatilityevaluation.BufferInterface;
import volatilityevaluation.LimitedBuffer;
import volatilityevaluation.UnlimitedBuffer;

/**
 * 
 * Drift detection method as published in: Kylie Chen, Yun Sing Koh, and
 * Patricia Riddle. Tracking Drift Severity in Data Streams. In AI 2015:
 * Advances in Artificial Intelligence, pages 96-108, 2015.
 * 
 * @author Kylie Chen - The University of Auckland
 * @author David T.J. Huang - The University of Auckland
 * @version 1.0
 */

public class MagSeed implements CutPointDetector
{

	public SeedWindow window;
	private double DELTA;
	private int blockSize;
	private int elementCount;

	// warning window counters
	private boolean warning = false;

	private double p = 1;
	private double s = 0;
	private double pTotal = 0;
	private int instCount = 0;
	private int windowSize = 100;
	private int[] errorWindow = new int[windowSize];

	private double pMin = Double.MAX_VALUE;
	private double sMin = Double.MAX_VALUE;

	// warning parameters
	private double warningDelta = 0.1;
	private double warningConfidence = 2.0;
	
	//buffers
	BufferInterface warningBuffer; // unlimited size sliding window. 
	BufferInterface preWarningBuffer; // fixed size sliding window
	private int preWarningBufferSize;
	
	private double severity;
	private double sigmoidBeta;
	
	private int numInstances = 0;


	
	public MagSeed(double delta, double warningDelta, int blockSize, int preWarningBufferSize, double sigmoidBeta)
	{
		this.DELTA = delta;
		this.warningDelta = warningDelta;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize);
		
		preWarningBuffer = new LimitedBuffer(preWarningBufferSize);
		warningBuffer = new UnlimitedBuffer(preWarningBufferSize); // assign a same size for warning buffer for optimisation.
		this.preWarningBufferSize = preWarningBufferSize;
		
		this.sigmoidBeta = sigmoidBeta;
		
	}

	public MagSeed(double delta, int blockSize, int decayMode, int compressionMode, double epsilonHat, double alpha,
			int term)
	{
		this.DELTA = delta;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize, decayMode, compressionMode, epsilonHat, alpha, term);
	}
	
	/**
	 * 
	 * @param delta ADWIN Hoeffding Bound parameter
	 * @param blockSize
	 * @param decayMode
	 * @param compressionMode
	 * @param epsilonHat
	 * @param alpha Decay coefficient
	 * @param term
	 * @param preWarningBufferSize
	 */
	public MagSeed(double delta, int blockSize, int decayMode, int compressionMode, double epsilonHat, double alpha,
			int term, int preWarningBufferSize) // Lin's new constructor
	{
		this.DELTA = delta;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize, decayMode, compressionMode, epsilonHat, alpha, term);
		
		preWarningBuffer = new LimitedBuffer(preWarningBufferSize);
		warningBuffer = new UnlimitedBuffer(preWarningBufferSize); // assign a same size for warning buffer for optimisation.
		this.preWarningBufferSize = preWarningBufferSize;
	}

	public void setWarningConfidence(double confidence)
	{
		warningConfidence = confidence;
	}

	public void setWindowSize(int size)
	{
		windowSize = size;
		errorWindow = new int[size];
	}

	public void setWarningDelta(double delta)
	{
		warningDelta = delta;
	}

	public void setWarning(boolean warningFlag)
	{
		warning = warningFlag;
	}

	public boolean getWarning()
	{
		return warning;
	}

	@Override
	public boolean setInput(double inputValue)
	{
		this.numInstances++;
		
		SeedBlock cursor;
		addElement(inputValue); // no block compression
		boolean keepWarning = false;

		if (elementCount % blockSize == 0 && window.getBlockCount() >= 2)
		{
			boolean blnReduceWidth = true;

			while (blnReduceWidth)
			{
				blnReduceWidth = false;
				boolean warningFound = false;

				// drift check from tail
				int n1 = 0;
				int n0 = window.getWidth();
				double u1 = 0;
				double u0 = window.getTotal();

				cursor = window.getTail();
				while (cursor.getPrevious() != null)
				{
					n0 -= cursor.getItemCount();
					n1 += cursor.getItemCount();
					u0 -= cursor.getTotal();
					u1 += cursor.getTotal();
					double diff = Math.abs(u1 / n1 - (u0 / n0));

					if (diff > getADWINBound(n0, n1))
					{
						blnReduceWidth = true; // drift detected
						window.resetDecayIteration();
						window.setHead(cursor);

						while (cursor.getPrevious() != null)
						{
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

						// compute severity
						severity = (warningBuffer.getMean() - preWarningBuffer.getMean())/warningBuffer.size();
						// severity = Math.atan((u1/n1 - u0/n0) / ((n1+n0)/2));
						// severity = sigmoid(((u1/n1 - u0/n0) / ((n1+n0)/2)));
						// severity = (u1/n1 - u0/n0) / ((n1+n0)/2);
						
						// reset
						preWarningBuffer.addAll(warningBuffer);
						preWarningBuffer.add(inputValue); // if there is a drift, add the inputValue to the preWarning buffer used in the future. 
						warningBuffer.clear();
						
						return true;

					} else if (warning)
					{
						if (keepWarning == false && diff > getWarningBound(n0, n1))
						{
							keepWarning = true; // warning detected							
						}
					} else if (!warning && !warningFound && diff > getWarningBound(n0, n1))
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
					warningBuffer = new UnlimitedBuffer(preWarningBufferSize); // reset warning buffer
				}
			}

		}

		if (warning == false)
		{
			window.checkCompression(); // block compression check
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
	
	private double computeSeverity()
	{
		 return (warningBuffer.getMean()-preWarningBuffer.getMean()) / warningBuffer.size(); 
	}

	private double getWarningBound(double b0, double b1)
	{
		double epsilon = getADWINBound(b0, b1, warningDelta);
		return epsilon;
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

	private double getADWINBound(double n0, double n1)
	{
		double n = n0 + n1;
		double dd = Math.log(2 * Math.log(n) / DELTA);
		double v = window.getVariance() / window.getWidth();
		double m = (1 / (n0)) + (1 / (n1));
		double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;
		return epsilon;
	}

	public void addElement(double value)
	{
		window.addTransaction(value, false); // add element without compressing
												// blocks
		elementCount++;
		instCount++; // number of instances in sliding window

		int windowPos = (instCount - 1) % windowSize;
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

	private void setWarningFlags(boolean bool)
	{
		warning = bool;
		window.isWarning = bool;
	}

	public double getEstimation()
	{
		return window.getTotal() / window.getWidth();
	}
	
	public double getSeverity()
	{
		return severity;
	}
	
	public int getWindowSize()
	{
		return window.getWidth();
	}
	
	private double sigmoid(double x) {
		    return (1/( 1 + Math.pow(Math.E,(-sigmoidBeta*x))));
	}
	


}








