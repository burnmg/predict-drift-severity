package a.evaluator;

import com.yahoo.labs.samoa.instances.Instance;

import meka.core.A;
import moa.classifiers.AbstractClassifier;

public interface Evaluator
{
	public double getOverallMeasurement();
	double addResult(Instance testInst, double[] votes);
}
