package moa.classifiers.a.VAC.other;

import cutpointdetection.CutPointDetector;
import volatilityevaluation.Buffer;

public class AverageCurrentDriftIntervalMeasure implements CurrentVolatilityMeasure
{
	private Buffer buffer;
	private CutPointDetector cutPointDetector;
	private boolean isDrifting;
	private int timestamp;
	
	public AverageCurrentDriftIntervalMeasure(int bufferSize, CutPointDetector cutPointDetector)
	{
		this.buffer = new Buffer(bufferSize);
		this.cutPointDetector = cutPointDetector;
		this.isDrifting = false;
		this.timestamp = 0;
	}

	@Override
	public int setInput(double input)
	{
		if(cutPointDetector.setInput(input))
		{
			buffer.add(this.timestamp);
			isDrifting = true;
			this.timestamp = 0;
			return (int)buffer.getMean();
		}
		else
		{
			isDrifting = false;
			this.timestamp++;
			return -1;
		}

	}

	@Override
	public boolean conceptDrift()
	{
		return isDrifting;
	}

	@Override
	public double getMeasure()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setInput(boolean drift)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
