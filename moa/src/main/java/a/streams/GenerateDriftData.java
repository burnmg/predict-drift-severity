package a.streams;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.experimental.theories.Theories;

import a.tools.Directory;
import java_cup.internal_error;
import moa.DoTask;
import moa.clusterers.outliers.AbstractC.StreamObj;
import moa.streams.VolatilityChangeStreamGenerator;
import moa.streams.VolatilityChangeStreamGeneratorGradual;
import moa.streams.generators.CircleGenerator;
import moa.streams.generators.HyperplaneGenerator;
import moa.tasks.WriteStreamToARFFFile3;
import weka.gui.HierarchyPropertyParser;

public class GenerateDriftData
{

	public static void main(String[] args)
	{
		doWork();
	}

	public static void doWork()
	{
		// int[] numDrifts = {10,100,10,100,10,100,10,100,10,100};
		// generateAbruptDriftData2(8, 2, 500000, 50, 5, numDrifts, 2314,
		// "10,100,10,100,10,100,10,100,10,100.arff");
		//
		// int[] numDrifts = {10,100,10,100,10,100,10,100,10,100};
		// generateAbruptDriftData2(8, 4, 500000, 50, 5, numDrifts, 2314,
		// "4class_10,100,10,100,10,100,10,100,10,100.arff");
		//
		// int[] numDrifts = {10,200,10,200,10,200};
		// generateAbruptDriftData2(8, 4, 500000, 500, 5, numDrifts, 2314,
		// "10,200,10,200,10,200.arff");

//		 int[] numDrifts = {10,200,10,200,10,200};
//		 generateAbruptDriftData2(10, 3, 500000, 10, 5, numDrifts, 2314,
		// "3of10drifting,10window_10,200,10,200,10,200.arff");

		// int[] numDrifts = {10,100,10,100,10,100};
		// generateAbruptDriftData2(10, 5, 1000000, 1000, 5, numDrifts, 2314,
		// "10,100,10,100,10,100.arff");

//		int[] numDrifts =
//		{ };
//		generateAbruptDriftData2(10, 2, 100000, 10, 5, numDrifts, 4324, "3.arff");
//		generateNormalData(200000, "rotating.arff");
		

//		generateData(10, 20, 2, 500000, 100, 10, numDrifts, 3691, "10,100,10,100,10,100.arff");
//		generateData(10,n)
//		generateData(10, 10, 2, 500000, 100, 7, numDrifts, 76, Directory.streamsPath, "test.arff");
		
//		int[] numDrifts = {1,500,1,500,1,500};
//		generateAbruptDriftData2(20, 2, 500000, 10, 10, numDrifts, 3691, "1,500,1,500,1,500.arff");
		
		int[] numDrifts = {10,100,10,100};
		generateAbruptDriftData2(20, 2, 500000, 10, 10, numDrifts, 3691, "10,100,10,100.arff");
		
//		int[] numDrifts = {1};
//		generateAbruptDriftData2(20, 2, 500000, 1000, 10, numDrifts, 3691, "1.arff");
		
//		int[] numDrifts = {1,100,1,1,1,100};
//		generateAbruptDriftData2(20, 2, 500000, 10, 10, numDrifts, 3691, "1,100,1,1,1,100.arff");
		
//		int[] numDrifts = {10,100,10,10,10,100,10,10};
//		generateAbruptDriftData2(20, 2, 500000, 10, 10, numDrifts, 3691, "10,100,10,10,10,100,10,10.arff");
		
//		int[] numDrifts = {100,100,10,100,100,100,100,10,100};
//		generateAbruptDriftData2(20, 2, 500000, 10, 10, numDrifts, 3691, "100,100,10,100,100,100,100,10,100.arff");

//		int[] numDrifts = {1};
//		generateAbruptDriftData2(20, 2, 100000, 10, 10, numDrifts, 3691, "short.arff");
		
//		circleData("circle.arff");
	}
	
	public static void circleData(String fileName)
	{
		File dir = new File(Directory.root + "Streams/" + fileName);
		dir.mkdirs();
		File destFile = new File(dir.getAbsolutePath() + '/' + fileName);

		CircleGenerator circleGenerator = new CircleGenerator(12, 3213);

		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(circleGenerator, destFile);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();
	}

	public static void generateData(int numSamples, int numAtt, int numClass, int blockLength, int interleavedWindowSize,
			int driftAttsNum, int[] changes, int randomSeedInt, String streamPath, String fileName)
	{
	
		ExecutorService executorService = Executors.newFixedThreadPool(8);
		StreamGeneratorTaskSamplePool list = new StreamGeneratorTaskSamplePool(numSamples,numAtt, numClass, blockLength, interleavedWindowSize, driftAttsNum, changes, randomSeedInt, streamPath, fileName);
		
		for(int i=0;i<list.size();i++)

		{
			executorService.submit(list.get(i));
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
	
	public static void generateAbruptDriftData2(int numAtt, int numClass, int blockLength, int interleavedWindowSize,
			int driftAttsNum, int[] changes, int randomSeedInt, String fileName)
	{
		File dir = new File(Directory.root + "Streams/" + fileName);
		dir.mkdirs();
		File destFile = new File(dir.getAbsolutePath() + '/' + fileName);

		VolatilityChangeStreamGenerator generator = new VolatilityChangeStreamGenerator(numAtt, numClass, changes,
				driftAttsNum, blockLength, interleavedWindowSize, randomSeedInt, 1, dir);
		generator.prepareForUse();

		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(generator, destFile);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();

	}
	
	public static void generateGradualDriftData(int blockLength, int interleavedWindowSize, int driftAttsNum,
			int[] changes, int randomSeedInt, String fileName, double mag, int sigma)
	{
		File dir = new File(Directory.root + "Streams/" + fileName);
		dir.mkdirs();
		File destFile = new File(dir.getAbsolutePath() + '/' + fileName);

		VolatilityChangeStreamGeneratorGradual generator = new VolatilityChangeStreamGeneratorGradual(changes,
				driftAttsNum, blockLength, interleavedWindowSize, randomSeedInt, 1, dir, mag, sigma);
		generator.prepareForUse();

		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(generator, destFile);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();
	}

	public static void generateNormalData(int length, String fileName)
	{
		File dir = new File(Directory.root + "Streams/" + fileName);
		dir.mkdirs();
		File destFile = new File(dir.getAbsolutePath() + '/' + fileName);

		HyperplaneGenerator hyperplaneGenerator = new HyperplaneGenerator();
		hyperplaneGenerator.getOptions().resetToDefaults();
		hyperplaneGenerator.magChangeOption.setValue(1);
		hyperplaneGenerator.numDriftAttsOption.setValue(3);
		hyperplaneGenerator.prepareForUse();

		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(hyperplaneGenerator, destFile);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();

	}

	/**
	 * 
	 * @param blockLength
	 * @param interleavedWindowSize
	 * @param numDrifts:
	 *            number of drifts in one block
	 * @param fileName:
	 *            examlpe "stream.arff"
	 */
	public static void generateAbruptDriftData(int blockLength, int interleavedWindowSize, int[] numDrifts,
			String fileName)
	{
		System.out.print("Total stream length: " + numDrifts.length * blockLength);
		// if(fileName==null)
		// {
		// for(int i=0; i<numDrifts.length;i++)
		// {
		// fileName += numDrifts[i]+ "_";
		// }
		// }

		File dir = new File(Directory.root + "Streams/" + fileName);
		dir.mkdir();

		String taskhead = "WriteStreamToARFFFile2 -s " + "(MultipleConceptDriftStreamGenerator" + " -l " + blockLength
				+ " -d " + numDrifts[0] + " -c 4 -w " + interleavedWindowSize + " -f " + dir
				+ "/streamDescription.csv) " + "-f " + dir + '/' + fileName;
		// first write
		String[] ft =
		{ taskhead };
		DoTask.main(ft);

		// further write
		for (int i = 1; i < numDrifts.length; i++)
		{
			String furtherTaskhead = "WriteStreamToARFFFile2 -s " + "(MultipleConceptDriftStreamGenerator" + " -l "
					+ blockLength + " -d " + numDrifts[i] + " -c 4 -w " + interleavedWindowSize + " -f " + dir
					+ "/streamDescription.csv) " + "-f " + dir + '/' + fileName + " -h -c";

			String[] t =
			{ furtherTaskhead };
			DoTask.main(t);
		}

		System.out.print("Done");
	}

}
