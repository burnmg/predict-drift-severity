package a;

import moa.DoTask;
import weka.gui.graphvisualizer.DotParser;

public class EvaluateAlgorithm
{
	final int HT = 0;
	public static void main(String args[])
	{
		
	}
	
	//test HAT in 12500-10m limit memory
	// "EvaluatePrequential -l (trees.HoeffdingAdaptiveTree -m 33554) -s (ArffFileStream -f /Users/rl/789/Streams/12500-10m.arff) -f 1000 -q 1000 -d /Users/rl/789/test/12500-10m-limit-m/HAT/res.csv",

	//TODO 
	public static void evaluatePrequential(int algorithm, String stream)
	{
		String algorithmString = null; 
		String dirString = null;
		switch(algorithm)
		{
		case 0: 
			algorithmString = "trees.HoeffdingTree"; 
			//TODO dirString = ""; 
		
		default: algorithmString = "error";
		}
		String[] t = 
			{
					 "EvaluatePrequential -l "+algorithmString+" -s (ArffFileStream -f /Users/rl/789/Streams/12500-10m.arff) -f 1000 -q 1000 -d /Users/rl/789/test/12500-10m-limit-m/HAT/res.csv",

			}; 
		DoTask.main(t);
			
	}
}
