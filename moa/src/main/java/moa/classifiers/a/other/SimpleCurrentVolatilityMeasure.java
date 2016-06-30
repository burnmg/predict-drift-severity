package moa.classifiers.a.other;

import moa.classifiers.core.driftdetection.ADWIN;

public class SimpleCurrentVolatilityMeasure implements CurrentVolatilityMeasure {

	private int timestamp;
	private ADWIN cutpointDetector;
	
	public SimpleCurrentVolatilityMeasure(double d) {
		cutpointDetector = new ADWIN(d);
		timestamp = 0;
	}
	
	@Override
	
	public int setInput(double input) {
		timestamp++;
		if(cutpointDetector.setInput(input))
		{
			int temp = timestamp;
			timestamp = 0;
			return temp;
		}
		return -1;
	}

}
