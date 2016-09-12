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
		ExecutorService executorService = Executors.newFixedThreadPool(40);

		
		ArrayList<Callable<Integer>> list = new ArrayList<Callable<Integer>>();
		
		/** 
		 * Parameters: 
		 * paras[0] measure window size
		 * paras[1] reservoir size
		 */
		
		
		
//		list.addAll(buildTasksList("5reservoirSize_", "100wblock_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", 
//				VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 5}));
//		list.addAll(buildTasksList("10reservoirSize_", "100wblock_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", 
//				VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 10}));
//		
//		list.addAll(buildTasksList("1000reservoirSize_", "100wblock_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", 
//				VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 1000}));
//		
//		list.addAll(buildTasksList("10000reservoirSize_", "100wblock_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5", 
//				VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 10000}));
		
		
		
		list.addAll(buildTasksList("", "100wblock_5noise_100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5", 
		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 5, 0, new double[]{300000, 200}));
		list.addAll(buildTasksList("", "100wblock_5noise_100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5", 
		HAT, 5, 0, new double[]{300000, 200}));
		list.addAll(buildTasksList("", "100wblock_5noise_100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5,100,100,100,100,100,5,5", 
		HOEFFDING_ADWIN, 5, 0, new double[]{300000, 200}));
		
		/**
		 * SEA long high vol period
		 */
//		list.addAll(buildTasksList("", "sea_50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5", 
//		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5", 
//		HAT, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5", 
//		HOEFFDING_ADWIN, 20, 0, new double[]{300000, 200}));

		/**
		 * SEA small high vol period
		 */
//		list.addAll(buildTasksList("", "sea_50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5", 
//		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5", 
//		HAT, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5", 
//		HOEFFDING_ADWIN, 20, 0, new double[]{300000, 200}));
		
		/**
		 * SEA regular
		 */
//		list.addAll(buildTasksList("", "sea_5noise_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", 
//		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_5noise_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", 
//		HAT, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_5noise_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", 
//		HOEFFDING_ADWIN, 20, 0, new double[]{300000, 200}));
		
		
		/**
		 * SEA composed
		 */
//		list.addAll(buildTasksList("", "sea_composed_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", 
//		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_composed_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", 
//		HAT, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "sea_composed_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", 
//		HOEFFDING_ADWIN, 20, 0, new double[]{300000, 200}));
	
		/**
		 * large interleaved window
		 */
//		list.addAll(buildTasksList("", "window_1000_sea_5noise_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", 
//		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "window_1000_sea_5noise_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", 
//		HAT, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "window_1000_sea_5noise_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", 
//		HOEFFDING_ADWIN, 20, 0, new double[]{300000, 200}));
//
//		list.addAll(buildTasksList("", "window_10000_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", 
//		VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "window_10000_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", 
//		HAT, 20, 0, new double[]{300000, 200}));
//		list.addAll(buildTasksList("", "window_10000_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", 
//		HOEFFDING_ADWIN, 20, 0, new double[]{300000, 200}));
		
		
		
		/**
		 * full drift, regular
		 */
//		list.addAll(buildTasksList("", "fullD_100window_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "fullD_100window_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", HAT, 20, 0));
//		list.addAll(buildTasksList("", "fullD_100window_50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5,50,50,5,5", HOEFFDING_ADWIN, 20, 0));
		/**
		 * full drift, large low vol
		 */
//		list.addAll(buildTasksList("", "fullD_100window_5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "fullD_100window_5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50", HAT, 20, 0));
//		list.addAll(buildTasksList("", "fullD_100window_5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50,5,5,5,5,5,50,50", HOEFFDING_ADWIN, 20, 0));

		/**
		 * full drift, large high vol
		 */
//		list.addAll(buildTasksList("", "fullD_100window_5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "fullD_100window_5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50", HAT, 20, 0));
//		list.addAll(buildTasksList("", "fullD_100window_5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50,5,5,50,50,50,50,50", HOEFFDING_ADWIN, 20, 0));

		/**
		 * full drift, composed
		 */
//		list.addAll(buildTasksList("", "fullD_composed_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure, 20, 0));
//		list.addAll(buildTasksList("", "fullD_composed_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", HAT, 20, 0));
//		list.addAll(buildTasksList("", "fullD_composed_5noise_50,50,5,5,5,5,5,50,50,5,5,5,5,5,5,5,50,50,50,50,50,5,5,50,50,50,50,50", HOEFFDING_ADWIN, 20, 0));

		
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

	private static ArrayList<Callable<Integer>> buildTasksList(String resultPathPrefix, String streamPrefix, int classifierOption, int numSamples, int startIndex, double[] paras)
	{
		ArrayList<Callable<Integer>> list = new ArrayList<Callable<Integer>>(numSamples);
		
		for(int i=0;i<numSamples;i++)
		{
			try
			{
				list.add(buildTask(resultPathPrefix, streamPrefix+"_"+(i+startIndex)+".arff", classifierOption, paras));
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return list;
	}

	/**
	 * 
	 * @param paras 
	 * @param streamName: give a streamName, it will generate a evaluation task for this stream.
	 * @return
	 * @throws Exception 
	 */
	private static Callable<Integer> buildTask(String resultPathPrefix, String streamName, int classifierOption, double[] paras) throws Exception
	{

		File resultFolder = null;
		AbstractClassifier classifier = null;

		String pathname = Directory.root+"/Results/"+ resultPathPrefix + streamName;

		if(classifierOption==HOEFFDING_ADWIN)
		{
			resultFolder = new File(pathname+"/HOEFFDING_ADWIN");
			classifier = new HoeffdingTreeADWIN(new ADWIN());
			classifier.getOptions().resetToDefaults();
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure");
			
			// default setting
			// VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentIntervalTimeStampMeasure(300000, new ADWIN(), 2000), 20, 10000, 200);
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentIntervalTimeStampMeasure((int)paras[0], new ADWIN(), 2000), 10, 10000, (int)paras[1]);
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
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
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentDriftIntervalMeasure(30, new ADWIN(), 2000), 20, 10000, 200);
			temp.dumpFileDirOption.setValue(resultFolder.getPath());

			classifier = temp;
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_RelativeVolatilityDetectorMeasureNoCutpointDect)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_RelativeVolatilityDetectorMeasureNoCutpointDect");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new RelativeVolatilityDetectorMeasureNoCutpointDect(32), 10000, 10000, 200);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_MaxCurrentDriftInterfvalMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_MaxCurrentDriftInterfvalMeasuret");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new MaxCurrentDriftInterfvalMeasure(20, new ADWIN(), 2000), 10000, 10000, 200);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_SimpleCurrentVolatilityMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_SimpleCurrentVolatilityMeasure");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new SimpleCurrentVolatilityMeasure(5, new ADWIN(), 2000), 10000, 10000, 200);
			
			classifier = temp;
			temp.dumpFileDirOption.setValue(resultFolder.getPath());
		}
		else if(classifierOption==VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalInstanceWindowMeasure)
		{
			resultFolder = new File(pathname+"/VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalInstanceWindowMeasure");
			VolatilityAdaptiveClassifer temp = new VolatilityAdaptiveClassifer(new ADWIN(), new AverageCurrentIntervalInstanceWindowMeasure(500000, new ADWIN(), 2000), 0, 10000, 200);
			
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
