package moa.classifiers.a.VAC.other;

import com.yahoo.labs.samoa.instances.Instance;

import cutpointdetection.CutPointDetector;
import volatilityevaluation.Buffer;

public class AverageCurrentDriftIntervalMeasure implements CurrentVolatilityMeasure
{
	private Buffer buffer;
	private CutPointDetector cutPointDetector;
	private boolean isDrifting;
	private int timestamp;
	private int coolingPeriod;
	//	private int instanceSeen;

	public AverageCurrentDriftIntervalMeasure(int bufferSize, CutPointDetector cutPointDetector, int coolingPeriod)
	{
		this.buffer = new Buffer(bufferSize);
		this.cutPointDetector = cutPointDetector;
		this.isDrifting = false;
		this.timestamp = 0;
		this.coolingPeriod = coolingPeriod;
		//		this.instanceSeen = 0;
	}

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
				buffer.add(this.timestamp);
				isDrifting = true;
				this.timestamp = 0;
				//				instanceSeen = 0

				return (int)buffer.getMean();
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
