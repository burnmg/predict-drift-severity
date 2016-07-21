package a.evaluator;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import a.tools.Directory;
import moa.classifiers.AbstractClassifier;

/**
 * 
 * @author rl
 * Hold evaluation Tasks for one dataset with many samples.
 */
public class EvaluateTaskPool
{
	
	private AbstractClassifier classifier;
	private String resultFolderPath;
	private MyEvaluatePrequential evaluatePrequential;
	private String streamName;
	
	private ArrayList<Callable<Integer>> list;
	
	public EvaluateTaskPool(AbstractClassifier classifier, String streamName, String resultFolderPath)
	{
		int numSamples = new File(Directory.streamsPath+'/'+streamName +"/samples").list().length;
		
		list = new ArrayList<Callable<Integer>>(numSamples);
		
		AbstractClassifier newClassifier = (AbstractClassifier) classifier.copy();
		
		for(int i=0;i<numSamples;i++)
		{
			list.add(new EvaluateTask(newClassifier, streamName, i, resultFolderPath + "/" + i));
		}
		
		
	}
	
	public Callable<Integer> get(int i)
	{
		return list.get(i);
	}

	public int size()
	{
		return list.size();
	}
}
