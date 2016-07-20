package a.evaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;


import a.tools.Directory;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.streams.ExampleStream;
import moa.tasks.StandardTaskMonitor;

public class EvaluateAlgorithmTask implements Callable<Integer>
{
	private AbstractClassifier classifier;
	private String resultFolderPath;
	private MyEvaluatePrequential evaluatePrequential;
	private String streamName;


//	public EvaluateAlgorithmTask(String streamName, String resultFolderPath)
//	{
//		this.streamName = streamName;
//		this.resultFolderPath = resultFolderPath;
//	}
	
	public EvaluateAlgorithmTask(AbstractClassifier classifier, String streamName, String resultFolderPath)
	{
		this.classifier = classifier;
		ExampleStream stream = Directory.getStreamFromFileByName(streamName);
		this.streamName = streamName;
		this.resultFolderPath = resultFolderPath;

		evaluatePrequential = new MyEvaluatePrequential(this.classifier, stream, Directory.streamsPath+'/'+streamName, this.resultFolderPath, 1000);
		evaluatePrequential.getOptions().resetToDefaults();
		evaluatePrequential.sampleFrequencyOption.setValue(100);
		

	}

	
	@Override
	public Integer call()
	{
		evaluatePrequential.doMainTask(new StandardTaskMonitor(), null);
		evaluateVolIntervalCoverage();
		return 0;
	}
	
	
	public void evaluateVolIntervalCoverage()
	{
		if(!this.classifier.getClass().isAssignableFrom(VolatilityAdaptiveClassifer.class)) return;
		
		
		//		 evaluate the volatility interval coverage
		BufferedReader readActual;
		BufferedReader readExpected;
		int[][] actual = null;
		int[][] expected = null;
		try
		{
			//load actual
			readActual = new BufferedReader(new FileReader(this.resultFolderPath+"/"+"volSwitchIntervalDesc.csv"));
			actual = load2DArray(readActual);

			//load expected
			readExpected = new BufferedReader(new FileReader(
					new File(Directory.streamsPath +"/"+this.streamName+"/"+"volExpectedIntervalDescription.csv")));
			expected = load2DArray(readExpected);

			readActual.close();
			readExpected.close();

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		CorreteModeCoverageEvaluator evaluateCorreteModeCoverage = new CorreteModeCoverageEvaluator();
		double result = evaluateCorreteModeCoverage.evalutate(expected, actual);
		
		// output the result to file. 
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.resultFolderPath+"/summary.txt", false));
			writer.write("VolCoverageRate:"+result);
			
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private int[][] load2DArray(BufferedReader br) throws IOException
	{
		String test = br.readLine();

		String line = null;
		ArrayList<int[]> list = new ArrayList<int[]>();
		while((line = br.readLine())!=null)
		{
			String[] es = line.split(",");
			int[] a = new int[3];
			for(int i=0;i<es.length;i++)
			{
				a[i] = Integer.parseInt(es[i]);
			}
			list.add(a);
		}
		int[][] result = new int[list.size()][list.get(0).length];
		for(int i=0;i<result.length;i++)
		{
			result[i] = list.get(i);

		}
		return result;
	}


}
