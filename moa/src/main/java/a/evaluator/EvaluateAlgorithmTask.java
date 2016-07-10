package a.evaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.IntPredicate;

import a.tools.Directory;
import java_cup.internal_error;
import moa.classifiers.AbstractClassifier;
import moa.streams.ExampleStream;
import moa.tasks.StandardTaskMonitor;
import weka.core.converters.Loader;

public class EvaluateAlgorithmTask implements Callable<Integer>
{
	private AbstractClassifier classifier;
	private String resultFolderPath;
	private MyEvaluatePrequential evaluatePrequential;
	private String streamName;


	public EvaluateAlgorithmTask(AbstractClassifier classifier, String streamName, String resultFolderPath)
	{
		this.classifier = classifier;
		ExampleStream stream = Directory.getStreamFromFileByName(streamName);
		this.streamName = streamName;
		this.resultFolderPath = resultFolderPath;

		evaluatePrequential = new MyEvaluatePrequential();
		evaluatePrequential.getOptions().resetToDefaults();
		evaluatePrequential.setLearner(this.classifier);
		evaluatePrequential.setStream(stream);
		evaluatePrequential.sampleFrequencyOption.setValue(100);
		evaluatePrequential.dumpFileOption.setValue(this.resultFolderPath+"/dump.csv");



	}

	@Override
	public Integer call()
	{
		//		 evaluatePrequential.doMainTask(new StandardTaskMonitor(), null);

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
					new File(Directory.streamsPath+this.streamName+"/"+"volExpectedIntervalDescription.csv")));
			expected = load2DArray(readExpected);



		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		EvaluateCorreteModeCoverage evaluateCorreteModeCoverage = new EvaluateCorreteModeCoverage();
		System.out.println(evaluateCorreteModeCoverage.evalutate(expected, actual));


		//analyse TODO

		return 0;
	}

	private int[][] load2DArray(BufferedReader br) throws IOException
	{
		String test = br.readLine();

		String line = null;
		ArrayList<int[]> list = new ArrayList<int[]>();
		while((line = br.readLine())!=null)
		{
			String[] es = line.split(",");
			for(int i=0;i<es.length;i++)
			{
				int[] a = new int[3];
				a[i] = Integer.parseInt(es[i]);
			}
		}
		int[][] result = new int[list.size()][list.get(0).length];
		for(int i=0;i<result.length;i++)
		{
			result[i] = list.get(i);

		}
		return result;
	}


}
