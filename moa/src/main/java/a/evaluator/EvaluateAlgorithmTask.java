package a.evaluator;

import moa.classifiers.AbstractClassifier;
import moa.streams.ExampleStream;
import moa.tasks.StandardTaskMonitor;

public class EvaluateAlgorithmTask implements Runnable
{
	private AbstractClassifier classifier;
	private ExampleStream stream;
	private String resultFolderPath;
	private MyEvaluatePrequential evaluatePrequential;
	

	public EvaluateAlgorithmTask(AbstractClassifier classifier, ExampleStream stream, String resultFolderPath)
	{
		this.classifier = classifier;
		this.stream = stream;
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
		
	}

}
