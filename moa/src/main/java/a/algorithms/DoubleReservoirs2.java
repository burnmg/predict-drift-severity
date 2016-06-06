package a.algorithms;

public class DoubleReservoirs2
{
	public Reservoir highReservoir;
	public Reservoir lowReservoir;
	
	private double variance;
	private double mean; 
	private double M2; 

	public DoubleReservoirs2(int size)
	{
		this.highReservoir = new Reservoir(size);
		this.lowReservoir = new Reservoir(size);
		
		variance = 0;
		mean = 0;
		M2 = 0;
	}

	private int getWidth()
	{
		return highReservoir.getWidth() + lowReservoir.getWidth();
	}
	
	private double getTotal()
	{
		return highReservoir.getTotal() + lowReservoir.getTotal();
	}
    
//	def online_variance(data):
//	    n = 0
//	    mean = 0.0
//	    M2 = 0.0
//	     
//	    for x in data:
//	        n += 1
//	        delta = x - mean
//	        mean += delta/n
//	        M2 += delta*(x - mean)
//
//	    if n < 2:
//	        return float('nan')
//	    else:
//	        return M2 / (n - 1)
//
//	
	public void setInput(double input)
	{
		// update variance 
		
		
		
		
		if (highReservoir.getElementNum() == 0)
		{
			highReservoir.addElement(input);
			return;
		}
		
		if (lowReservoir.getElementNum() == 0)
		{
			lowReservoir.addElement(input);
			return;
		}

		double threshold = getMean();

		if (input > threshold)
		{
			highReservoir.addElement(input);
		} else
		{
			lowReservoir.addElement(input);
		}

	}
	
	public double getVariance()
	{
		return variance / getWidth();
	}

	
	public boolean isActive()
	{
		
		return false;
	}

	public double getMean()
	{
		if (highReservoir.size() == 0 && lowReservoir.size() == 0)
			return 0;

		// Can be optimised if needed.
		return (highReservoir.getTotal() + lowReservoir.getTotal()) / (highReservoir.getElementNum() + lowReservoir.getElementNum());
	}

}
