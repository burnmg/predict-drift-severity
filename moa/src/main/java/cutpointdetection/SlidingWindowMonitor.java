package cutpointdetection;

import org.apache.poi.hssf.record.cf.Threshold;

public class SlidingWindowMonitor implements CutPointDetector
{
	private static final long serialVersionUID = 1L;
	
	private int pos;
	private double[] window;
	private double sum;
	private int lenWindow;
	
	private double threshold;
	private int gracingPeriod;
	private int instanceSeen;
	
	public SlidingWindowMonitor(int windowSize, double threshold, int gracingPeriod)
	{
		this.window = new double[windowSize];
		this.threshold = threshold;
		this.gracingPeriod = gracingPeriod;
		
		this.lenWindow = 0;
		this.pos = 0;
		this.instanceSeen = 0;
	}
	
	@Override
	public boolean setInput(double d)
	{
		sum -= window[pos];
		sum += d;
		window[pos] = d;
		pos++;
		instanceSeen++;
		
		if(pos == window.length)
		{
			pos = 0;
		}
		
		if(lenWindow < window.length)
		{
			lenWindow++;
		}
		
		if(instanceSeen >= gracingPeriod && sum/lenWindow > threshold)
		{
			instanceSeen = 0;
			return true;
		}
		else 
		{
			
			return false;
		}
		
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getEstimation()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	

}
