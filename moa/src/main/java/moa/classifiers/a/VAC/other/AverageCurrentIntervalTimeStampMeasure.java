package moa.classifiers.a.VAC.other;

import java.util.LinkedList;
import java.util.Queue;

import cutpointdetection.CutPointDetector;

public class AverageCurrentIntervalTimeStampMeasure implements CurrentVolatilityMeasure
{
	Queue<Long> queue = new LinkedList<Long>();
	int windowSize;
	private CutPointDetector cutPointDetector;
	private boolean isDrifting;
	private int timestamp;
	private int coolingPeriod;
	private long numInstances; 
	
	public AverageCurrentIntervalTimeStampMeasure(int windowSize, CutPointDetector cutPointDetector, int coolingPeriod)
	{
		this.windowSize = windowSize;
		this.cutPointDetector = cutPointDetector;
		this.isDrifting = false;
		this.timestamp = 0;
		this.coolingPeriod = coolingPeriod;
		this.numInstances = 0;
	}
	
	/**
	 * @return Number of drifts in the window. 
	 */
	@Override
	public int setInput(double input)
	{
		if(numInstances > coolingPeriod){
			double oldError = cutPointDetector.getEstimation();
			boolean errorChange = cutPointDetector.setInput(input);

			if(oldError > cutPointDetector.getEstimation())
			{
				errorChange = false;
			}

			if(errorChange)
			{
				queue.add(numInstances);
				isDrifting = true;
				while(queue.peek()!=null && queue.peek() < this.numInstances - this.windowSize) queue.poll();
				this.numInstances++;
				
				return queue.size();
			}
			else
			{
				while(queue.peek()!=null && queue.peek() < this.numInstances - this.windowSize) queue.poll();
				isDrifting = false;
				this.numInstances++;

				return queue.size();

			}
		}
		else{
			while(queue.peek()!=null && queue.peek() < this.numInstances - this.windowSize) queue.poll();
			isDrifting = false;
			this.numInstances++;

			return queue.size();
		}
	}

	@Override
	public int setInput(boolean drift)
	{
		return 0;
	}

	@Override
	public boolean conceptDrift()
	{
		return isDrifting;
	}

	@Override
	public double getMeasure()
	{
		return 0;
	}

	@Override
	public double getMaxWindowSize()
	{
		return windowSize;
	}

}
