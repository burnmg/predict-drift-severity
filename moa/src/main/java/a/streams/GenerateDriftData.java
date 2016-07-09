package a.streams;

import java.io.File;

import a.tools.Directory;
import moa.DoTask;
import moa.streams.VolatilityChangeStreamGenerator;
import moa.tasks.WriteStreamToARFFFile3;

public class GenerateDriftData
{

	public static void main(String[] args)
	{
		doWork();
	}
	
	public static void doWork()
	{
//		int[] numDrifts = {10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000};
//		int[] numDrifts = {10, 1000, 10, 1000, 10};
//		generateAbruptDriftData(200000, 1000, numDrifts, "regularchangstream.arff");
//		int[] numDrifts = {10,3000,10,3000,10,3000};
//		generateAbruptDriftData(1000000, 1000, numDrifts, "10,3000,10,3000,10,3000.arff");
		
//		int[] numDrifts = {30000,10};
//		generateAbruptDriftData(1000000, 1000, numDrifts, "30000,10.arff");
		
//		int[] numDrifts = {1};
//		generateAbruptDriftData(500000, 1, numDrifts, "1.arff");
		
//		int[] numDrifts = {200,10,200,10,200,10,200,10};
//		generateAbruptDriftData2(500000, 10, 3, numDrifts, 2314, "200,10,200,10,200,10,200,10.arff");
		
		int[] numDrifts = {10,100,10,100};
		generateAbruptDriftData2(500000, 10, 3, numDrifts, 2314, "10,100,10,100.arff");
	}
	
	public static void generateAbruptDriftData2(int blockLength, int interleavedWindowSize, int driftAttsNum, int[] changes, int randomSeedInt, String fileName)
	{
		File dir = new File(Directory.root+"Streams/"+fileName); 
		dir.mkdirs();
		File destFile = new File(dir.getAbsolutePath() + '/' + fileName);
		
		
		VolatilityChangeStreamGenerator generator = new VolatilityChangeStreamGenerator(changes, driftAttsNum, blockLength, interleavedWindowSize, randomSeedInt, 1, dir);
		generator.prepareForUse();
		
		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(generator, destFile);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();
		
	}
	
	/**
	 * 
	 * @param blockLength
	 * @param interleavedWindowSize
	 * @param numDrifts: number of drifts in one block
	 * @param fileName: examlpe "stream.arff"
	 */
	public static void generateAbruptDriftData(int blockLength, int interleavedWindowSize, int[] numDrifts, String fileName)
	{
		System.out.print("Total stream length: " + numDrifts.length*blockLength);
//		if(fileName==null)
//		{
//			for(int i=0; i<numDrifts.length;i++)
//			{
//				fileName += numDrifts[i]+ "_";
//			}
//		}
		
		File dir = new File(Directory.root+"Streams/"+fileName); 
		dir.mkdir();
		
		String taskhead = "WriteStreamToARFFFile2 -s "
				+ "(MultipleConceptDriftStreamGenerator"
				+ " -l "+blockLength+" -d "+numDrifts[0]+" -c 4 -w "+interleavedWindowSize+" -f "+dir+"/streamDescription.csv) "
						+ "-f "+dir+'/'+fileName; 
		//first write
		String[] ft =  {
				taskhead
		};
		DoTask.main(ft);
		
		//further write
		for(int i=1;i<numDrifts.length;i++)
		{
			String furtherTaskhead = "WriteStreamToARFFFile2 -s "
					+ "(MultipleConceptDriftStreamGenerator"
					+ " -l "+blockLength+" -d "+numDrifts[i]+" -c 4 -w "+interleavedWindowSize+" -f "+dir+"/streamDescription.csv) "
							+ "-f "+dir+'/'+fileName + " -h -c" ; 
			
			String[] t =  {
					furtherTaskhead
			};
			DoTask.main(t);
		}
		
		System.out.print("Done");
	}

}
