package moa.classifiers.a.other;

import cutpointdetection.ADWIN;
import volatilityevaluation.RelativeVolatilityDetector;

public class RelativeVolatilityDetectorMeasure implements CurrentVolatilityMeasure
{
	
	private RelativeVolatilityDetector volatilityDriftDetector; 
	
	public RelativeVolatilityDetectorMeasure()
	{
		volatilityDriftDetector = new RelativeVolatilityDetector(new ADWIN(0.002), 32); 
	}

	@Override
	public int setInput(double input)
	{
		if (volatilityDriftDetector.setInputVar(input))
		{
			return (int)volatilityDriftDetector.getBufferMean();
		}
		else
		{
			return -1;
		}
	}

}
