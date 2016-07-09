package moa.classifiers.a.other;

import a.algorithms.DoubleReservoirs;

public class DoubleReservoirsClassifierSelector implements ClassifierSelector {

	DoubleReservoirs doubleReservoirs;
	
	/**
	 * 
	 * @param size: size of the reservoir
	 * @param lambda: allowable fluctuation
	 */
	public DoubleReservoirsClassifierSelector(int size, double lambda) {
		doubleReservoirs = new DoubleReservoirs(size, lambda);
	}
	
	@Override
	public int makeDecision(double avgInterval) {
		
		if(doubleReservoirs.setInput(avgInterval))
		{
			// low volatility
			return 1;
		}
		else
		{
			// high volatility
			return 2;
		}
		
		
	}

}
