package archive;

import com.yahoo.labs.samoa.instances.Instance;

public interface Evaluator
{
	public double getOverallMeasurement();
	double addResult(Instance testInst, double[] votes);
}
