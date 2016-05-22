package a.tools;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.plaf.metal.MetalIconFactory.FileIcon16;

import moa.DoTask;
import weka.gui.graphvisualizer.DotParser;

public class EvaluateAlgorithm
{
	final static int HT = 0;
	
	public static void main(String args[])
	{
		evaluatePrequential(HT, "regularchangstream.arff");
	}
	
	//test HAT in 12500-10m limit memory
	// "EvaluatePrequential -l (trees.HoeffdingAdaptiveTree -m 33554) -s (ArffFileStream -f /Users/rl/789/Streams/12500-10m.arff) -f 1000 -q 1000 -d /Users/rl/789/test/12500-10m-limit-m/HAT/res.csv",

	//TODO 
	public static void evaluatePrequential(int algorithm, String streamName)
	{
		String algorithmName = null; 
		switch(algorithm)
		{
		case HT: 
			algorithmName = "a.HoeffdingTreeADWIN"; 
			break;
		
		default: algorithmName = "error";
		}
		

		File algorithmDir = new File("/Users/rl/789/test/"+algorithmName);
		
		if(!(algorithmDir.exists() && algorithmDir.isDirectory()))
		{
			algorithmDir.mkdir();
		}
		File streamDir = new File(algorithmDir.getPath()+"/"+streamName);
		if(!(streamDir.exists() && streamDir.isDirectory()))
		{
			streamDir.mkdir();
		}
		
		String[] t = 
			{
					 "EvaluatePrequential -l "+algorithmName+" -s (ArffFileStream -f /Users/rl/789/Streams/"+streamName+"/"+streamName+") -f 1000 -q 1000 -d "+algorithmDir+"res.csv",

			}; 
		DoTask.main(t);
			
	}
}
