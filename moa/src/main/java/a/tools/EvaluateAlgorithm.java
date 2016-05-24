package a.tools;

import java.io.File;
import java.io.IOException;
import moa.DoTask;

public class EvaluateAlgorithm
{
	final static int HT = 0;
	
	public static void main(String args[]) throws IOException
	{
		evaluatePrequential(HT, "smalldrift.arff");
	}
	
	//test HAT in 12500-10m limit memory
	// "EvaluatePrequential -l (trees.HoeffdingAdaptiveTree -m 33554) -s (ArffFileStream -f /Users/rl/789/Streams/12500-10m.arff) -f 1000 -q 1000 -d /Users/rl/789/test/12500-10m-limit-m/HAT/res.csv",


	public static void evaluatePrequential(int algorithm, String streamName) throws IOException
	{
		String algorithmName = null; 
		switch(algorithm)
		{
		case HT: 
			algorithmName = "a.HoeffdingTreeADWIN"; 
			break;
		
		default: algorithmName = "error";
		}
		

		File algorithmLevelDir = new File(Directory.root+"test/"+algorithmName);
		
		if(!(algorithmLevelDir.exists() && algorithmLevelDir.isDirectory()))
		{
			algorithmLevelDir.mkdir();
		}
		File streamLevelDir = new File(algorithmLevelDir.getPath()+"/"+streamName);
		if(!(streamLevelDir.exists() && streamLevelDir.isDirectory()))
		{
			streamLevelDir.mkdir();
		}
		
		String[] t = 
			{
					 "EvaluatePrequential -l "+algorithmName+" -s (ArffFileStream -f "+Directory.root+"Streams/"+streamName+"/"+streamName+") -f 1 -q 1 -d "+streamLevelDir+"/res.csv",

			}; 
		DoTask.main(t);
		
		//use python to analyse the experiment results
		Runtime rt = Runtime.getRuntime();
		String res_file = streamLevelDir.getAbsolutePath()+"/res.csv";
		String drift_point_file = Directory.root+"Streams/"+streamName+"/streamDescription.csv";
		String to_summary_file = streamLevelDir.getAbsolutePath()+"/summary.csv";
		String to_figure_file = streamLevelDir.getAbsolutePath()+"/figure.png";
		
		String analyse_dir = "/Users/rl/PycharmProjects/plot/analyser/analyse.py"; 
		
		String command = "python "+analyse_dir+' '+res_file+' '+drift_point_file+' '+to_summary_file+' '+to_figure_file;
		rt.exec(command);
	}
}
