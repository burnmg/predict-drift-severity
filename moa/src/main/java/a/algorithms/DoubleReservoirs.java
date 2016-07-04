package a.algorithms;

import org.jfree.util.Log;
import org.omg.CORBA.PUBLIC_MEMBER;

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
	
	public double getLambda()
	{
		return this.lambda;
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
//		if (highReservoir.getWidth() == 0)
//		{
//			highReservoir.addElement(input);
//			return false;
//		}
//		
//		if (lowReservoir.getWidth() == 0)
//		{
//			lowReservoir.addElement(input);
//			return false;
//		}

		if (input > this.getMean())
		{
			highReservoir.addElement(input);
			return true;
		} else
		{
			lowReservoir.addElement(input);
			return false;
		}
		
		// update variance incrementally
//		double incVariance = 0;
//		if(getWidth()>1)
//		{
//			incVariance = (getWidth() - 1) * (input - getTotal() / (getWidth() - 1)) * (input - getTotal() / (getWidth() - 1)) / getWidth();
//		}	
//		variance += incVariance;

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


	// with ADWIN akin approach
	public boolean isActive()
	{
		int n = getWidth();
		double v = getVariance();
		double m = 1.0 / (1.0 / highReservoir.getWidth() + 1.0 / lowReservoir.getWidth());

		double epsilon = Math.sqrt(
				(2.0/m) * v * Math.log(2.0/delta)
				)
				+
				2.0/(3*m) * Math.log(2.0/delta) + this.lambda;
		
		System.out.println(epsilon);
		
		return Math.abs(highReservoir.getReservoirMean() - lowReservoir.getReservoirMean()) > epsilon;
	}

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
	
	

	public double getMean()
	{
		if (highReservoir.size() == 0 && lowReservoir.size() == 0)
			return 0;

		return (highReservoir.getTotal() + lowReservoir.getTotal()) / (highReservoir.getWidth() + lowReservoir.getWidth());
	}

}
