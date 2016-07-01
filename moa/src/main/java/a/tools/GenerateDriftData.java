package a.tools;

import java.io.File;

import moa.DoTask;

public class GenerateDriftData
{

	public static void main(String[] args)
	{
//		int[] numDrifts = {10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000};
//		int[] numDrifts = {10, 1000, 10, 1000, 10};
//		generateAbruptDriftData(200000, 1000, numDrifts, "regularchangstream.arff");
//		int[] numDrifts = {10,3000,10,3000,10,3000};
//		generateAbruptDriftData(1000000, 1000, numDrifts, "10,3000,10,3000,10,3000.arff");
		
//		int[] numDrifts = {30000,10};
//		generateAbruptDriftData(1000000, 1000, numDrifts, "30000,10.arff");
		
		int[] numDrifts = {1000,10};
		generateAbruptDriftData(500000, 1, numDrifts, "1000,10.arff");
		
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
		if(fileName==null)
		{
			for(int i=0; i<numDrifts.length;i++)
			{
				fileName += numDrifts[i]+ "_";
			}
		}
		
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
