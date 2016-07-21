package a.evaluator;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import a.tools.Directory;
import cutpointdetection.ADWIN;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.a.HoeffdingTreeADWIN;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.classifiers.trees.HoeffdingAdaptiveTree;

/**
 * 
 * @author rl
 * Use sample pools
 */
public class EvaluateMain2
{

	final static int VOL_ADAPTIVE_CLASSIFIER = 0;
	final static int HOEFFDING_ADWIN = 1;
	final static int HAT = 2;

	public static void main(String[] args) throws Exception
	{
		EvaluateTaskPool[] pool = {
				buildTaskPool("5,50.arff", HAT)	
		};
		
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		
		
		
//		pool[0].get(1).call();
//		executorService.submit(pool[0].get(1));
		for(int i=0;i<pool.length;i++)
		{
			for(int j=0;j<pool[i].size();j++)
			{
				executorService.submit(pool[i].get(j));

			}
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
	
	
	private static EvaluateTaskPool buildTaskPool(String streamName, int classifierOption) throws Exception
	{

		File resultFolder = null;
		AbstractClassifier classifier = null;

		String pathname = Directory.root+"/Results/"+streamName;

		if(classifierOption==HOEFFDING_ADWIN)
		{
			resultFolder = new File(pathname+"/HOEFFDING_ADWIN");
			classifier = new HoeffdingTreeADWIN(new ADWIN());
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
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN());
			temp.dumpFileDirOption.setValue(resultFolder.getPath());

			classifier = temp;
		}
		else 
		{
			throw new Exception("Wrong classifier option");
		}

		resultFolder.mkdirs();


		classifier.resetLearning();

		return new EvaluateTaskPool(classifier, streamName, resultFolder.getAbsolutePath());

	}

}
