package moa.classifiers.a.VAC.other;



import a.algorithms.Reservoir;
import cutpointdetection.CutPointDetector;
import volatilityevaluation.LimitedBuffer;

public class RelativeVolatilityDetectorMeasureNoCutpointDect implements CurrentVolatilityMeasure
{
	// private ADWIN cutpointDetector;
	private Reservoir reservoir;
	private LimitedBuffer buffer;
	private double confidence;

	private int timestamp = 0;
	private boolean conceptDrift;

	public RelativeVolatilityDetectorMeasureNoCutpointDect(int resSize)
	{
		this.conceptDrift = false;
		this.reservoir = new Reservoir(resSize);
		this.buffer = new LimitedBuffer(resSize);
		this.confidence = 0.05;
	}

	public RelativeVolatilityDetectorMeasureNoCutpointDect(int resSize, double confidence)
	{
		this.conceptDrift = false;
		this.reservoir = new Reservoir(resSize);
		this.buffer = new LimitedBuffer(resSize);
		this.confidence = confidence;
	}
	
	public double getBufferMean()
	{
		return buffer.getMean();
	}
	
	public int setInput(boolean drift)
	{
		
		if (drift)
		{
			this.conceptDrift = true;
			if (buffer.isFull())
			{
				reservoir.addElement(buffer.add(++timestamp));
			} 
			else
			{
				buffer.add(++timestamp);
			}

			if (buffer.isFull() && reservoir.isFull())
			{

				double RelativeVar = buffer.getStdev() / reservoir.getReservoirStdev();

				if (RelativeVar > 1.0 + confidence || RelativeVar < 1.0 - confidence) // <<<<< Threshold
				{
					reservoir.clear();
					
					return (int) this.getBufferMean();		    
				} 
				else
				{
					return -1;
				}
			}
			timestamp = 0;

		}
		else
		{
			this.conceptDrift = false;
			timestamp++;
			return -1;
		}
		return -1;
	}
	
	
	@Override
	public int setInput(double inputValue)
	{
		return 0;
	}

	public class Mergesort
	{
		private double[] numbers;
		private double[] helper;

		private int number;

		public double[] sort(double[] values)
		{
			this.numbers = values;
			number = values.length;
			this.helper = new double[number];
			mergesort(0, number - 1);
			return numbers;
		}

		private void mergesort(int low, int high)
		{
			// Check if low is smaller then high, if not then the array is
			// sorted
			if (low < high)
			{
				// Get the index of the element which is in the middle
				int middle = low + (high - low) / 2;
				// Sort the left side of the array
				mergesort(low, middle);
				// Sort the right side of the array
				mergesort(middle + 1, high);
				// Combine them both
				merge(low, middle, high);
			}
		}

		private void merge(int low, int middle, int high)
		{

			// Copy both parts into the helper array
			for (int i = low; i <= high; i++)
			{
				helper[i] = numbers[i];
			}

			int i = low;
			int j = middle + 1;
			int k = low;
			// Copy the smallest values from either the left or the right side
			// back
			// to the original array
			while (i <= middle && j <= high)
			{
				if (helper[i] <= helper[j])
				{
					numbers[k] = helper[i];
					i++;
				} else
				{
					numbers[k] = helper[j];
					j++;
				}
				k++;
			}
			// Copy the rest of the left side of the array into the target array
			while (i <= middle)
			{
				numbers[k] = helper[i];
				k++;
				i++;
			}

		}
	}

	@Override
	public boolean conceptDrift()
	{
		return this.conceptDrift;
	}

	@Override
	public double getMeasure()
	{
		
		return this.getBufferMean();
	}
}
