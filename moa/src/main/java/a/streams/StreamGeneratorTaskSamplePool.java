package a.streams;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

public class StreamGeneratorTaskSamplePool
{

	
	private ArrayList<Callable<Integer>> list;
	
	public StreamGeneratorTaskSamplePool(int numSamples, int numAtt, int numClass, int blockLength, int interleavedWindowSize,
			int driftAttsNum, int[] changes, int randomSeedInt, String streamPath, String fileName)
	{
		
		this.list = new ArrayList<Callable<Integer>>(numSamples);
		Random random = new Random(randomSeedInt);
		
		for(int i=0;i<numSamples;i++)
		{
			String fileAbsPath = streamPath + "/" + fileName + "/" + i + ".arff";
			StreamGenerateTask task = new StreamGenerateTask(numAtt, numClass, blockLength, interleavedWindowSize, driftAttsNum, changes, random.nextInt(), fileAbsPath);
			this.list.add(task);
		}
		

	}

	public Callable<Integer> get(int i)
	{
		return this.list.get(i);
	}

	public int size()
	{
		return this.list.size();
	}
	
}
