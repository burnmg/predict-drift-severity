package moa.classifiers.a.VAC.other;

public interface CurrentVolatilityMeasure {
	
	/**
	 * 
	 * @param input:Accuracy
	 * @return 
	 * -1:no measure available
	 */
	public int setInput(double input);
	
	public int setInput(boolean drift);
	
	public boolean conceptDrift();
	
	public double getMeasure();
	
	public double getMaxWindowSize();
	

}
