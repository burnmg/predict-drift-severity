package a.evaluator;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import a.tools.Directory;
import cutpointdetection.ADWIN;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.a.HoeffdingTreeADWIN;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.classifiers.trees.HoeffdingAdaptiveTree;

public class EvaluateMain
{

	final static int VOL_ADAPTIVE_CLASSIFIER = 0;
	final static int HOEFFDING_ADWIN = 1;
	final static int HAT = 2;
	
	public static void main(String[] args) throws Exception
	{
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		
		// tasks
		Callable[] tasks = {
				buildTask("10,100,10,100,10,100,10,100,10,100.arff", VOL_ADAPTIVE_CLASSIFIER),
				buildTask("10,100,10,100,10,100,10,100,10,100.arff", HAT),
				buildTask("10,100,10,100,10,100,10,100,10,100.arff", HOEFFDING_ADWIN),
//				buildTask("1,100,1,1,100,1,1.arff", VOL_ADAPTIVE_CLASSIFIER),
//				buildTask("1,100,1,1,100,1,1.arff", HAT),
//				buildTask("1,100,1,1,100,1,1.arff", HOEFFDING_ADWIN),
				
//				buildTask("normal.arff", VOL_ADAPTIVE_CLASSIFIER),
//				buildTask("normal.arff", HAT),
//				buildTask("normal.arff", HOEFFDING_ADWIN),
				};
		
//		tasks[0].call();
		for(Callable<Integer> task : tasks)
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
	 * @throws Exception 
	 */
	private static Callable<Integer> buildTask(String streamName, int classifierOption) throws Exception
	{
		
		File resultFolder = null;
		AbstractClassifier classifier = null;
		
		String pathname = Directory.root+"/Results/"+streamName;
		
		if(classifierOption==HOEFFDING_ADWIN)
		{
			resultFolder = new File(pathname+"/HOEFFDING_ADWIN");
			classifier = new HoeffdingTreeADWIN(new ADWIN(0.002));
			classifier.getOptions().resetToDefaults();
		}
		else if (classifierOption==HAT) 
		{
			resultFolder = new File(pathname+"/HAT");
			classifier = new HoeffdingAdaptiveTree();
			classifier.getOptions().resetToDefaults();
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(0.002));
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
			
			classifier = temp;
		}
		else 
		{
			throw new Exception("Wrong classifier option");
		}
		
		resultFolder.mkdirs();
		
		
		classifier.resetLearning();
		
		return new EvaluateAlgorithmTask(classifier, streamName, resultFolder.getAbsolutePath());
		
	}
	


}
