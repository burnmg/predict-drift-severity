package moa.classifiers.a.VAC.other;

import a.algorithms.DoubleReservoirs;

public class DoubleReservoirsHighForHighLowForLow implements ClassifierSelector {

	DoubleReservoirs doubleReservoirs;
	
	/**
	 * 
	 * @param size: size of the reservoir
	 * @param lambda: allowable fluctuation
	 */
	public DoubleReservoirsHighForHighLowForLow(int size, double lambda) {
		doubleReservoirs = new DoubleReservoirs(size, lambda);
	}
	
	@Override
	public int getDecision(double input) {
		
		if(doubleReservoirs.getDecision(input))
		{
			// high volatility
			return 2;
		}
		else
		{
			// low volatility
			return 1;
		}
		
		
	}
	/**
	 * @return decision after this input. 
	 */
	@Override
	public int input(double input)
	{
		if(doubleReservoirs.setInput(input))
		{
			// high volatility
			return 2;
		}
		else
		{
			// low volatility
			return 1;
		}
	}

	@Override
	public double getThreshold()
	{
		return doubleReservoirs.getMiddleMean();
	}

	@Override
	public boolean getIsActive()
	{
		return doubleReservoirs.isActive();
	}



}
