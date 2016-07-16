package moa.classifiers.a.other;

public interface ClassifierSelector
{
	/**
	 * 
	 * @return 1 for classifer1, 2 for classifier2.
	 */
	public int makeDecision(double avgInterval);
	public double getMeasure();

}
