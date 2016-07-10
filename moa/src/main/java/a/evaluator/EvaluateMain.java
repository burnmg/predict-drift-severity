package a.evaluator;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import a.tools.Directory;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.streams.ExampleStream;

public class EvaluateMain
{

	public static void main(String[] args)
	{
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		
		// tasks
		Runnable[] tasks = {
				buildTask("10,100,10,100.arff"),
				};
		
		for(Runnable task : tasks)
		{
			executorService.submit(task);
		}
		
		executorService.shutdown();
		try
		{
			executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		};
	}
	
	/**
	 * 
	 * @param streamName: give a streamName, it will generate a evaluation task for this stream.
	 * @return
	 */
	private static Runnable buildTask(String streamName)
	{
		
		// classifier
		File resultFolder = new File(Directory.root+"/Results/"+streamName);
		resultFolder.mkdirs();
		
		VolatilityAdaptiveClassifer classifier = new VolatilityAdaptiveClassifer();
		classifier.getOptions().resetToDefaults();
		classifier.dumpFileDirOption.setValue(resultFolder.getPath());
		classifier.resetLearning();
		
		return new EvaluateAlgorithmTask(classifier, streamName, resultFolder.getAbsolutePath());
		
	}
	


}
