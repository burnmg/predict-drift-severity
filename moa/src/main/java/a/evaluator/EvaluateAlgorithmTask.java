package a.evaluator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import a.tools.Directory;
import moa.classifiers.AbstractClassifier;
import moa.streams.ExampleStream;
import moa.tasks.StandardTaskMonitor;

public class EvaluateAlgorithmTask implements Runnable
{
	private AbstractClassifier classifier;
	private ExampleStream stream;
	private String resultFolderPath;
	private MyEvaluatePrequential evaluatePrequential;
	

	public EvaluateAlgorithmTask(AbstractClassifier classifier, String streamName, String resultFolderPath)
	{
		this.classifier = classifier;
		this.stream = Directory.getStreamFromFileByName(streamName);
		this.resultFolderPath = resultFolderPath;
		
		evaluatePrequential = new MyEvaluatePrequential();
		evaluatePrequential.getOptions().resetToDefaults();
		evaluatePrequential.setLearner(this.classifier);
		evaluatePrequential.setStream(stream);
		evaluatePrequential.sampleFrequencyOption.setValue(100);
		evaluatePrequential.dumpFileOption.setValue(this.resultFolderPath+"/dump.csv");
		
		
		
	}
	
	@Override
	public void run()
	{
		evaluatePrequential.doMainTask(new StandardTaskMonitor(), null);
		
		// evaluate the volatility interval coverage
		BufferedReader readActual;
		try
		{
			readActual = new BufferedReader(new FileReader(this.resultFolderPath+"/"));
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		//analyse TODO
	}

}
