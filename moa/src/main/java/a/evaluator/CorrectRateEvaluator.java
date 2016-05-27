package a.evaluator;

import com.yahoo.labs.samoa.instances.Instance;

import moa.classifiers.AbstractClassifier;
import moa.core.Utils;

public class CorrectRateEvaluator implements Evaluator
{
	private double correctSum;
	private int instanceCount;

	@Override
	public double getOverallMeasurement()
	{
		return correctSum/instanceCount;
	}

	@Override
	public double addResult(Instance testInst, double[] votes)
	{
		int trueClass = (int) testInst.classValue();
		int prediction = Utils.maxIndex(votes);
        correctSum += trueClass==prediction?1.0:0.0; 
        instanceCount++;
        
        return correctSum/instanceCount;
	}

}
