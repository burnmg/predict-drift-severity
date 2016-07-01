package moa.classifiers.a.other;

import cutpointdetection.ADWIN;
import volatilityevaluation.RelativeVolatilityDetector;

public class RelativeVolatilityDetectorMeasure implements CurrentVolatilityMeasure
{
	
	private RelativeVolatilityDetector volatilityDriftDetector; 
	
	public RelativeVolatilityDetectorMeasure(double lambda) //lambda = 0.05
	{
		volatilityDriftDetector = new RelativeVolatilityDetector(new ADWIN(lambda), 32); 
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
