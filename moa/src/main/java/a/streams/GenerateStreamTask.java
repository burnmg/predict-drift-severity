package a.streams;

import java.io.File;
import java.util.concurrent.Callable;

import com.google.common.primitives.UnsignedBytes;

import a.tools.Directory;
import moa.streams.VolatilityChangeStreamGenerator;
import moa.tasks.WriteStreamToARFFFile3;

public class GenerateStreamTask implements Callable<Integer>
{


	private int numAtt;
	private int numClass;
	private int blockLength;
	private int interleavedWindowSize;
	private int driftAttsNum;
	private int[] changes;
	private int randomSeedInt;
	private String fileAbsPath;
	private int noisePercentage;
	private boolean isFullDrift;
	


	public GenerateStreamTask(int numAtt, int numClass, int blockLength, int interleavedWindowSize,
			int driftAttsNum, int[] changes, int randomSeedInt, String fileAbsPath, int noisePercentage, boolean isFullDrift)
	{
		this.numAtt = numAtt;
		this.numClass = numClass;
		this.blockLength = blockLength;
		this.interleavedWindowSize = interleavedWindowSize;
		this.driftAttsNum = driftAttsNum;
		this.changes = changes;
		this.randomSeedInt = randomSeedInt;
		this.fileAbsPath = fileAbsPath;
		this.noisePercentage = noisePercentage;
		this.isFullDrift = isFullDrift;
	}

	@Override
	public Integer call() throws Exception
	{

		generateAbruptDriftData();
		return 0;
	}
	
	public void generateAbruptDriftData()
	{
		File file = new File(fileAbsPath);
		File dir = file.getParentFile();
		dir.mkdirs();
		
		int startChange = changes[0];
		boolean startIsLow = true;
		for(int i=1;i<changes.length; i++)
		{
			if(changes[i] < startChange) startIsLow = false;
		}
		
		VolatilityChangeStreamGenerator generator = new VolatilityChangeStreamGenerator(numAtt, numClass, changes,
				driftAttsNum, blockLength, interleavedWindowSize, randomSeedInt, startIsLow?1:2, dir, noisePercentage, this.isFullDrift);
		generator.prepareForUse();

		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(generator, file);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();

	}

}
