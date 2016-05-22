package lin.volatility;

import cutpointdetection.CutPointDetector;
import moa.classifiers.core.driftdetection.ADWIN;

public class VolatilityMonitor
{
	private ADWIN cutpointDetector = new ADWIN(0.1);
	
	private int timestamp = 0;
	
	/**
	 * 
	 * @param inputValue
	 * @return interval of a volatility level
	 */
	public int setInput(double inputValue)
	{
		if (cutpointDetector.setInput(inputValue))
		{
			int r = timestamp;
			timestamp = 0;
			return r;
		} 
		else
		{
			timestamp++;
			return -1;
		}
	}

}
