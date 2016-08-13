package a.algorithms;

public class DoubleReservoirs
{
	public Reservoir highReservoir;
	public Reservoir lowReservoir;
	
	private double variance;
	private double delta = 0.002; 
	private double lambda; // allowable fluctuation

	/**
	 * 
	 * @param size: size of the reservoir
	 * @param lambda: allowable fluctuation
	 */
	public DoubleReservoirs(int size, double lambda)
	{
		this.highReservoir = new Reservoir(size);
		this.lowReservoir = new Reservoir(size);
		this.variance = 0;
		this.lambda = lambda; 
	}
	

	private int getWidth()
	{
		return highReservoir.getWidth() + lowReservoir.getWidth();
	}
	
	private double getTotal()
	{
		return highReservoir.getTotal() + lowReservoir.getTotal();
	}
    
	/**
	 * 
	 * @param input: Interval of Drift.
	 * @return 
	 * true: is in high value mode
	 * false: is in low value mode
	 */
	public boolean setInput(double input)
	{


		double mean = this.getMean();
		if (input > mean)
		{
			
			highReservoir.addElement(input);
			return true;

		} else
		{
			lowReservoir.addElement(input);
			return false;
		}
		

	}
	
	public double getVariance()
	{
		double mean = this.getMean();
		double sum = 0;
	
		double[] highElements = highReservoir.getElements();
		for(int i=0; i<highReservoir.getWidth(); i++)
		{
			sum += Math.pow(highElements[i] - mean, 2);
		}
		
		double[] lowElements = lowReservoir.getElements();
		for(int i=0; i<lowReservoir.getWidth(); i++)
		{
			sum += Math.pow(lowElements[i] - mean, 2);
		}
		
		return sum / getWidth(); 
		
	}
	
	public boolean isActive()
	{
		return (highReservoir.getReservoirMean() - lowReservoir.getReservoirMean()) > this.lambda;
	}


	// with ADWIN akin approach
//	public boolean isActive()
//	{
//		int n = getWidth();
//		double v = getVariance();
//		double m = 1.0 / (1.0 / highReservoir.getWidth() + 1.0 / lowReservoir.getWidth());
//
//		double epsilon = Math.sqrt(
//				(2.0/m) * v * Math.log(2.0/delta)
//				)
//				+
//				2.0/(3*m) * Math.log(2.0/delta) + this.lambda;
//		
//		System.out.println(epsilon);
//		
//		return Math.abs(highReservoir.getReservoirMean() - lowReservoir.getReservoirMean()) > epsilon;
//	}

	// with VFDT akin approach
//	public boolean isActive()
//	{
//		double range = 20000;
//		double m = 1.0 / (1.0 / highReservoir.getWidth() + 1.0 / lowReservoir.getWidth());
//		double epsilon = Math.sqrt(
//				(Math.pow(range, 2)*Math.log(2/delta))
//				/
//				8.0 * m
//				);
//		
//		return Math.abs(highReservoir.getReservoirMean() - lowReservoir.getReservoirMean()) > epsilon;
//	}
	
	
	// mean of totla
//	public double getMean()
//	{
//		if (highReservoir.size() == 0 && lowReservoir.size() == 0)
//			return 0;
//
//		return (highReservoir.getTotal() + lowReservoir.getTotal()) / (highReservoir.getWidth() + lowReservoir.getWidth());
//	}
	
	// mean of two means of reservoirs
	public double getMean()
	{
		if (highReservoir.getWidth() == 0 && lowReservoir.getWidth() == 0)
			return 0;

		return (highReservoir.getTotal() + lowReservoir.getTotal()) / (highReservoir.getWidth() + lowReservoir.getWidth());
//		return (highReservoir.getReservoirMean() + lowReservoir.getReservoirMean())/2;
	}

}
