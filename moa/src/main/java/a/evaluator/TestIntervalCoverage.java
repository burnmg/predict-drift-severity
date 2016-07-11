package a.evaluator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestIntervalCoverage
{

	public static void main(String[] args)
	{
		EvaluateAlgorithmTask task = new EvaluateAlgorithmTask("10,100,10,100.arff", "/Users/rl/789/Results/10,100,10,100.arff");
		task.evluateVolIntervalCoverage();
		
		
	}

}
