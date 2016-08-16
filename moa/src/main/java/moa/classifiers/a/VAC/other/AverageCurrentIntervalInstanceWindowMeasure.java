package moa.classifiers.a.VAC.other;

import java.util.LinkedList;
import java.util.Queue;

import cutpointdetection.CutPointDetector;

public class AverageCurrentIntervalInstanceWindowMeasure implements CurrentVolatilityMeasure
{
	Queue<Integer> queue = new LinkedList<Integer>();
	int queueSum;
	int windowSize;
	private CutPointDetector cutPointDetector;
	private boolean isDrifting;
	private int timestamp;
	private int coolingPeriod;
	
	public AverageCurrentIntervalInstanceWindowMeasure(int windowSize, CutPointDetector cutPointDetector, int coolingPeriod)
	{
		this.windowSize = windowSize;
		this.queueSum = 0;
		this.cutPointDetector = cutPointDetector;
		this.isDrifting = false;
		this.timestamp = 0;
		this.coolingPeriod = coolingPeriod;
	}
	
	/**
	 * @return Number of drifts in the window. 
	 */
	@Override
	public int setInput(double input)
	{
		if(timestamp > coolingPeriod){

			double oldError = cutPointDetector.getEstimation();
			boolean errorChange = cutPointDetector.setInput(input);

			if(oldError > cutPointDetector.getEstimation())
			{
				errorChange = false;
			}

			if(errorChange)
			{
				queue.add(this.timestamp);
				queueSum += this.timestamp;
				isDrifting = true;
				this.timestamp = 0;

				while(this.queueSum > this.windowSize)
				{
					queueSum -= queue.poll(); 
				}
//				return queueSum!=0?this.queue.size()*this.windowSize / queueSum : 0;
				return this.queue.size();
			}
			else
			{
				isDrifting = false;
				this.timestamp++;
				return -1;

			}
		}
		else{
			isDrifting = false;
			this.timestamp++;
			return -1;
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
