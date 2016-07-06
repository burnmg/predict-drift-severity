package moa.streams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.classifiers.core.driftdetection.SeqDrift2ChangeDetector.Block;
import moa.core.Example;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

public class VolatilityChangeStreamGenerator extends AbstractOptionHandler implements InstanceStream
{

	// input parameters
	private int changes[];
	private Random random;
	
	//file and writers
	private File driftDesciptionFile;
	private File switchDescriptionFile;
	private BufferedWriter driftDesciptionWriter;
	private BufferedWriter switchDescriptionWriter;
	
	private int currentBlockCount;
	private int numberInstance;
	private int maxInstancesCount;
	private MultipleConceptDriftStreamGenerator3 currentBlock;
	
	// state the index of current expected algorithm
	private int currentAlgorithmIndex;
	
	
	private static final long serialVersionUID = 7628833159490333423L;

	public void setChanges(int[] changes)
	{
		this.changes = changes;
	}
	
	public VolatilityChangeStreamGenerator(int[] changes, int driftAttsNum, int blockLength, int interleavedWindowSize, 
			int randomSeedInt, int startClassifier, File descriptionFileDir)
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
		
		// compute max instances count. Assume each block has same lengths. 
		maxInstancesCount = blockLength * changes.length;
		
		// drift Description
		driftDesciptionFile = new File(descriptionFileDir.getAbsolutePath() + "/driftDescription.csv");
		try
		{
			driftDesciptionWriter = new BufferedWriter(new FileWriter(driftDesciptionFile));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		writeToFile(driftDesciptionWriter, "DriftInstanceIndex\n"); 
		
		
		// expected switch Description TODO 
		switchDescriptionFile = new File(descriptionFileDir.getAbsolutePath() + "/switchDescription.csv");
		try
		{
			switchDescriptionWriter = new BufferedWriter(new FileWriter(switchDescriptionFile));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		writeToFile(switchDescriptionWriter, "ExpectedSwitchIndex, SwitchTo\n");
		
		currentAlgorithmIndex = startClassifier;
	}
	
	private void writeToFile(BufferedWriter bw, String str)
	{
		if (bw != null)
		{
			try
			{
				bw.write(str);
				bw.flush();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
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

		if(currentBlock.hasMoreInstances())
		{
			Example inst = currentBlock.nextInstance();
			numberInstance++;
			
			if(currentBlock.isDrifting()){
				writeToFile(driftDesciptionWriter, numberInstance+"\n");
			}
			
			return inst;
		}
		else
		{
			
			currentBlockCount++;
			currentBlock.setStream1(currentBlock.getStream2());
			currentBlock.numDriftsOption.setValue(changes[currentBlockCount]);
			currentBlock.restartOnlyParameters();
			
			Example inst = currentBlock.nextInstance();
			numberInstance++;
			
//			TODO
			// if current block has a greater value than previous one, it means current block has higher volatility level, so switch
			int switchTo = changes[currentBlockCount] > changes[currentBlockCount - 1] ? 2:1;
			if(switchTo!=currentAlgorithmIndex)
			{
				writeToFile(switchDescriptionWriter, numberInstance+","+switchTo +"\n");
				currentAlgorithmIndex = switchTo;
			}
			
			
			
			return inst;
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
