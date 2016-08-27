package a.evaluator;

import java.io.File;
import java.util.ArrayList;
//import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jfree.chart.renderer.category.StatisticalBarRenderer;

import a.tools.Directory;
import cutpointdetection.ADWIN;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.a.HoeffdingTreeADWIN;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.classifiers.a.VAC.other.AverageCurrentDriftIntervalMeasure;
import moa.classifiers.a.VAC.other.AverageCurrentIntervalInstanceWindowMeasure;
import moa.classifiers.a.VAC.other.AverageCurrentIntervalTimeStampMeasure;
import moa.classifiers.a.VAC.other.MaxCurrentDriftInterfvalMeasure;
import moa.classifiers.a.VAC.other.RelativeVolatilityDetectorMeasureNoCutpointDect;
import moa.classifiers.a.VAC.other.SimpleCurrentVolatilityMeasure;
import moa.classifiers.trees.HoeffdingAdaptiveTree;

public class EvaluateMain
{

	final static int VOL_ADAPTIVE_CLASSIFIER_AverageCurrentDriftIntervalMeasure = 0;
	final static int VOL_ADAPTIVE_CLASSIFIER_RelativeVolatilityDetectorMeasureNoCutpointDect = -1;
	final static int VOL_ADAPTIVE_CLASSIFIER_MaxCurrentDriftInterfvalMeasure = -2;
	final static int VOL_ADAPTIVE_CLASSIFIER_SimpleCurrentVolatilityMeasure = -3;
	final static int VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalInstanceWindowMeasure = -4;
	final static int VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure = -5;
	final static int HOEFFDING_ADWIN = 1;
	final static int HAT = 2;

	public static void main(String[] args) throws Exception
	{
		long start = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(12);

		
		ArrayList<Callable<Integer>> list = new ArrayList<Callable<Integer>>();
		


//		/**
//		 * These two full drifts data are re-generated. 
//		 */
//		list.addAll(buildTasksList("", "fullD_block_5noise_5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "fullD_block_5noise_5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100", HAT, 20, 0));
//		list.addAll(buildTasksList("", "fullD_block_5noise_5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100", HOEFFDING_ADWIN, 20, 0));
//
//		list.addAll(buildTasksList("", "fullD_block_5noise_5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "fullD_block_5noise_5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100", HAT, 20, 0));
//		list.addAll(buildTasksList("", "fullD_block_5noise_5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100", HOEFFDING_ADWIN, 20, 0));
//		
//		list.addAll(buildTasksList("", "5,50,100", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "5,50,100", HAT, 20, 0));
//		list.addAll(buildTasksList("", "5,50,100", HOEFFDING_ADWIN, 20, 0));
//		
//		list.addAll(buildTasksList("", "100,50,5", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "100,50,5", HAT, 20, 0));
//		list.addAll(buildTasksList("", "100,50,5", HOEFFDING_ADWIN, 20, 0));
//		
//		list.addAll(buildTasksList("", "1000interleaved_size_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "1000interleaved_size_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", HAT, 20, 0));
//		list.addAll(buildTasksList("", "1000interleaved_size_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", HOEFFDING_ADWIN, 20, 0));

		
		
		/**
		 * full drift, regular
		 */
		list.addAll(buildTasksList("", "fullD_100window_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
		list.addAll(buildTasksList("", "fullD_100window_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", HAT, 20, 0));
		list.addAll(buildTasksList("", "fullD_100window_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", HOEFFDING_ADWIN, 20, 0));
		
		

		
//		list.get(0).call();
		for(Callable<Integer> task : list)
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
		
		System.out.println("EvaluateMain End!");
		
		long elapsedTimeMillis = System.currentTimeMillis()-start;
		
		System.out.print("Time Cost in millis: "+elapsedTimeMillis);;
		
	}

	private static ArrayList<Callable<Integer>> buildTasksList(String resultPathPrefix, String streamPrefix, int classifierOption, int numSamples, int startIndex)
	{
		ArrayList<Callable<Integer>> list = new ArrayList<Callable<Integer>>(numSamples);
		
		for(int i=0;i<numSamples;i++)
		{
			try
			{
				list.add(buildTask(resultPathPrefix, streamPrefix+"_"+(i+startIndex)+".arff", classifierOption));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return list;
	}

	/**
	 * 
	 * @param streamName: give a streamName, it will generate a evaluation task for this stream.
	 * @return
	 * @throws Exception 
	 */
	private static Callable<Integer> buildTask(String resultPathPrefix, String streamName, int classifierOption) throws Exception
	{

		File resultFolder = null;
		AbstractClassifier classifier = null;

		String pathname = Directory.root+"/Results/"+ resultPathPrefix + streamName;

		if(classifierOption==HOEFFDING_ADWIN)
		{
			resultFolder = new File(pathname+"/HOEFFDING_ADWIN");
			classifier = new HoeffdingTreeADWIN(new ADWIN());
			//			classifier = new HoeffdingTreeADWIN(new SlidingWindowMonitor(1000, 0.15, 5000));
			classifier.getOptions().resetToDefaults();
		}
		else if (classifierOption==HAT) 
		{
			resultFolder = new File(pathname+"/HAT");
			classifier = new HoeffdingAdaptiveTree();
			classifier.getOptions().resetToDefaults();
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_AverageCurrentDriftIntervalMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_AverageCurrentDriftIntervalMeasure");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentDriftIntervalMeasure(30, new ADWIN(), 2000), 20, 10000);
			temp.dumpFileDirOption.setValue(resultFolder.getPath());

			classifier = temp;
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_RelativeVolatilityDetectorMeasureNoCutpointDect)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_RelativeVolatilityDetectorMeasureNoCutpointDect");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new RelativeVolatilityDetectorMeasureNoCutpointDect(32), 10000, 10000);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_MaxCurrentDriftInterfvalMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_MaxCurrentDriftInterfvalMeasuret");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new MaxCurrentDriftInterfvalMeasure(20, new ADWIN(), 2000), 10000, 10000);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_SimpleCurrentVolatilityMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_SimpleCurrentVolatilityMeasure");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new SimpleCurrentVolatilityMeasure(5, new ADWIN(), 2000), 10000, 10000);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalInstanceWindowMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalInstanceWindowMeasure");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentIntervalInstanceWindowMeasure(500000, new ADWIN(), 2000), 0, 10000);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentIntervalTimeStampMeasure(300000, new ADWIN(), 2000), 20, 10000);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else
		{
			throw new Exception("Wrong classifier option");
		}

		resultFolder.mkdirs();


		classifier.resetLearning();

		return new EvaluateTask(classifier, streamName, resultFolder.getAbsolutePath());

	}



}
