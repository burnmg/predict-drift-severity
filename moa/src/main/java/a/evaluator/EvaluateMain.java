package a.evaluator;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import a.tools.Directory;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.streams.ArffFileStream;
import moa.streams.ExampleStream;

public class EvaluateMain
{

	public static void main(String[] args)
	{
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		// tasks
		Runnable[] tasks = {
				buildTask("200,10,200,10,200,10,200,10.arff"),
				buildTask("10,1000,10,1000.arff"),
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
	
	private static Runnable buildTask(String streamName)
	{
		//stream
		ExampleStream stream = getStreamFromFile(streamName);
		
		// classifier
		File resultFolder = new File(Directory.root+"/Results/"+streamName);
		resultFolder.mkdirs();
		VolatilityAdaptiveClassifer classifier = new VolatilityAdaptiveClassifer();
		classifier.getOptions().resetToDefaults();
		classifier.currentVolatilityLevelWriterDumpFileOption.setValue(resultFolder.getPath()+"/currentVolatilityLevel.csv");
		classifier.classifierChangePointDumpFileOption.setValue(resultFolder.getPath()+"/classifierChangePointDumpFile.csv");
		classifier.resetLearning();
		
		return new EvaluateAlgorithmTask(classifier, stream, resultFolder.getAbsolutePath());
		
	}
	
	private static ArffFileStream getStreamFromFile(String streamName)
	{
		String path = Directory.streamsPath + streamName + '/' + streamName;
		ArffFileStream stream = new ArffFileStream();
		stream.arffFileOption.setValue(path);
		stream.prepareForUse();
		
		return stream;
	}

}
