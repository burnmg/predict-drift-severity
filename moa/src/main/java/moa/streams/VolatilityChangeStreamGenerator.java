package moa.streams;

import java.io.File;
import java.util.Random;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;
import moa.core.Example;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

public class VolatilityChangeStreamGenerator extends AbstractOptionHandler implements InstanceStream
{

	// input parameters
	private int changes[];
	private Random random;
	
	private int currentBlockCount;
	private int numberInstance;
	private int maxInstancesCount;
	private MultipleConceptDriftStreamGenerator3 currentBlock;
	
	
	private static final long serialVersionUID = 7628833159490333423L;

	public void setChanges(int[] changes)
	{
		this.changes = changes;
	}
	
	public VolatilityChangeStreamGenerator(int[] changes, int driftAttsNum, int blockLength, int interleavedWindowSize, 
			int randomSeedInt)
	{
		this.currentBlockCount = 0;
		this.numberInstance = 0;
		
		
		this.changes = changes;
		// first block
		currentBlock = new MultipleConceptDriftStreamGenerator3();
		currentBlock.getOptions().resetToDefaults();
		currentBlock.streamLengthOption.setValue(blockLength);
		currentBlock.numDriftsOption.setValue(changes[currentBlockCount]);
		currentBlock.widthOption.setValue(interleavedWindowSize);
		currentBlock.numDriftAttsOption.setValue(driftAttsNum);
		currentBlock.driftRandom = random;
		
		//special for first block
		currentBlock.initStream1AndStream2();
		
		currentBlock.prepareForUse();
		
//		// further blocks
//		for(int i=1; i < changes.length;i++)
//		{
//			streams[i] = new MultipleConceptDriftStreamGenerator3();
//			streams[i].getOptions().resetToDefaults();
//			streams[i].streamLengthOption.setValue(blockLength);
//			streams[i].numDriftsOption.setValue(changes[i]);
//			streams[i].widthOption.setValue(interleavedWindowSize);
//			streams[i].numDriftAttsOption.setValue(driftAttsNum);
//			streams[i].driftRandom = random;
//			
//			//special for further blocks
//			streams[i].setStream1(streams[i-1].getStream2());
//			
//			streams[i].prepareForUse();
//		}
		
		// compute max instances count. Assume each block has same lengths. TODO
		maxInstancesCount = blockLength * changes.length;
		
	}
	
	@Override
	public InstancesHeader getHeader()
	{
		return currentBlock.getHeader();
	}

	@Override
	public long estimatedRemainingInstances()
	{
		return maxInstancesCount - numberInstance;
	}

	@Override
	public boolean hasMoreInstances()
	{	
		return numberInstance < maxInstancesCount; 
	}

	@Override
	public Example nextInstance()
	{
		numberInstance++;
		if(currentBlock.hasMoreInstances())
		{
			return currentBlock.nextInstance();
		}
		else
		{
			// assign stream2 of earlier block to stream 1 of the new block.
//			currentBlockCount++;
//			currentBlock		
//			streams[i].prepareForUse();
			// TODO
			

			currentBlockCount++;
			currentBlock.setStream1(currentBlock.getStream2());
			currentBlock.numDriftsOption.setValue(changes[currentBlockCount]);
			currentBlock.restartOnlyParameters();
			
			return currentBlock.nextInstance();
		}


	}

	@Override
	public boolean isRestartable()
	{
		return false;
	}

	@Override
	public void restart()
	{
		
	}

	@Override
	public void getDescription(StringBuilder sb, int indent)
	{
		
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository)
	{
		
	}
	

}
