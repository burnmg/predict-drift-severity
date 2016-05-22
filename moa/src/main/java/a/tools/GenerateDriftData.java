package a.tools;

import java.io.File;

import moa.DoTask;

public class GenerateDriftData
{

	public static void main(String[] args)
	{
		int[] numDrifts = {10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000};
		generateAbruptDriftData(200000, 1000, numDrifts, "regularchangstream.arff");
	}
	
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
		
		File dir = new File("/Users/rl/789/Streams/"+fileName); 
		dir.mkdir();
		
		String taskhead = "WriteStreamToARFFFile2 -s "
				+ "(MultipleConceptDriftStreamGenerator -l "+blockLength+" -d "+numDrifts[0]+" -c 4 -w "+interleavedWindowSize+") "
						+ "-f "+dir+'/'+fileName; 
		//first write
		String[] ft =  {
				taskhead
		};
		DoTask.main(ft);
		//further write
		for(int i=1;i<numDrifts.length;i++)
		{
			String[] t =  {
					taskhead+" -h -c" 
			};
			DoTask.main(t);
		}
		
		System.out.print("Done");
	}

}
