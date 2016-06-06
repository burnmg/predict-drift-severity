package a.algorithms;

import org.jfree.util.Log;

public class DoubleReservoirs
{
	public Reservoir highReservoir;
	public Reservoir lowReservoir;
	
	private double variance;
	private double delta = 0.002; 

	public DoubleReservoirs(int size)
	{
		this.highReservoir = new Reservoir(size);
		this.lowReservoir = new Reservoir(size);
		variance = 0;
	}
	
	
	private int getWidth()
	{
		return highReservoir.getWidth() + lowReservoir.getWidth();
	}
	
	private double getTotal()
	{
		return highReservoir.getTotal() + lowReservoir.getTotal();
	}
    
	public void setInput(double input)
	{
		if (highReservoir.getWidth() == 0)
		{
			highReservoir.addElement(input);
			return;
		}
		
		if (lowReservoir.getWidth() == 0)
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
		double mean = getMean();
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

//    private boolean blnCutexpression(int n0, int n1, double u0, double u1, double v0, double v1, double absvalue, double delta) {
//        int n = getWidth();
//        double dd = Math.log(2 * Math.log(n) / delta);     // -- ull perque el ln n va al numerador.
//        // Formula Gener 2008
//        double v = getVariance();
//        double m = ((double) 1 / ((n0 - mintMinWinLength + 1))) + ((double) 1 / ((n1 - mintMinWinLength + 1)));
//        double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;
//
//        return (Math.abs(absvalue) > epsilon);
//    }
	public boolean isActive()
	{
		
		int n = getWidth();
		double v = getVariance();
		double m = 1.0 / (1.0 / highReservoir.getWidth() + 1.0 / lowReservoir.getWidth());
		
		double epsilon = Math.sqrt(
				(2.0/m) * v * Math.log(2/delta)
				)
				+
				2.0/(3*m) * Math.log(2.0/delta); 
		return Math.abs(highReservoir.getReservoirMean() - lowReservoir.getReservoirMean()) > epsilon;
	}

	public double getMean()
	{
		if (highReservoir.size() == 0 && lowReservoir.size() == 0)
			return 0;

		return (highReservoir.getTotal() + lowReservoir.getTotal()) / (highReservoir.getWidth() + lowReservoir.getWidth());
	}

}
