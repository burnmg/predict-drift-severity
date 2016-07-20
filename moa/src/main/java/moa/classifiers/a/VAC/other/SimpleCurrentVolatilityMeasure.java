package moa.classifiers.a.VAC.other;

import moa.classifiers.core.driftdetection.ADWIN;

public class SimpleCurrentVolatilityMeasure implements CurrentVolatilityMeasure {

	private int timestamp;
	private ADWIN cutpointDetector;
	private boolean conceptDrift;
	
	public SimpleCurrentVolatilityMeasure(double d) {
		cutpointDetector = new ADWIN(d);
		timestamp = 0;
	}
	
	@Override
	
	public int setInput(double input) {
		timestamp++;
		if(cutpointDetector.setInput(input))
		{
			this.conceptDrift = true;
			int temp = timestamp;
			timestamp = 0;
			return temp;
		}
		else{
			this.conceptDrift = false;
		}
		
		return -1;
	}

	@Override
	public int setInput(boolean drift)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean conceptDrift()
	{
		// TODO Auto-generated method stub
		return conceptDrift;
	}

	@Override
	public double getMeasure()
	{
		// TODO Auto-generated method stub
		return timestamp;
	}

}
