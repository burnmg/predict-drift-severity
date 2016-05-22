package a;

import moa.DoTask;

public class GenerateDriftData
{

	public static void main(String[] args)
	{
		int[] numDrifts = {10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000, 10, 1000};
		generateAbruptDriftData(200000, 1000, numDrifts, null);
	}
	
	public static void generateAbruptDriftData(int blockLength, int interleavedWindowSize, int[] numDrifts, String file)
	{
		System.out.print("Total stream length: " + numDrifts.length*blockLength);
		if(file==null)
		{
			for(int i=0; i<numDrifts.length;i++)
			{
				file += numDrifts[i]+ "_";
			}
		}
		//first write
		String[] ft =  {
				"WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l "+blockLength+" -d "+numDrifts[0]+" -c 4 -w "+interleavedWindowSize+") -f /Users/rl/789/Streams/"+file+".arff" 
		};
		DoTask.main(ft);
		//further write
		for(int i=1;i<numDrifts.length;i++)
		{
			String[] t =  {
					"WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l "+blockLength+" -d "+numDrifts[i]+" -c 4 -w "+interleavedWindowSize+") -f /Users/rl/789/Streams/"+file+".arff -h -c" 
			};
			DoTask.main(t);
		}
		
		System.out.print("Done");
	}

}
