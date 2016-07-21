package a.streams;

import java.io.File;
import java.util.concurrent.Callable;

import a.tools.Directory;
import moa.streams.VolatilityChangeStreamGenerator;
import moa.tasks.WriteStreamToARFFFile3;

public class StreamGenerateTask implements Callable<Integer>
{


	private int numAtt;
	private int numClass;
	private int blockLength;
	private int interleavedWindowSize;
	private int driftAttsNum;
	private int[] changes;
	private int randomSeedInt;
	private String fileAbsPath;
	


	public StreamGenerateTask(int numAtt, int numClass, int blockLength, int interleavedWindowSize,
			int driftAttsNum, int[] changes, int randomSeedInt, String fileAbsPath)
	{
		this.numAtt = numAtt;
		this.numClass = numClass;
		this.blockLength = blockLength;
		this.interleavedWindowSize = interleavedWindowSize;
		this.driftAttsNum = driftAttsNum;
		this.changes = changes;
		this.randomSeedInt = randomSeedInt;
		this.fileAbsPath = fileAbsPath;
	}

	@Override
	public Integer call() throws Exception
	{

		generateAbruptDriftData2();
		return 0;
	}
	
	public void generateAbruptDriftData2()
	{
		File file = new File(fileAbsPath);
		File dir = file.getParentFile();
		dir.mkdirs();

		VolatilityChangeStreamGenerator generator = new VolatilityChangeStreamGenerator(numAtt, numClass, changes,
				driftAttsNum, blockLength, interleavedWindowSize, randomSeedInt, 1, dir.getParentFile());
		generator.prepareForUse();

		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(generator, file);
		task.suppressHeaderOption.unset();
		task.concatenate.unset();
		task.prepareForUse();
		task.doTask();

	}

}
