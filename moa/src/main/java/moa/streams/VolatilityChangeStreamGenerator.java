package moa.streams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import java_cup.internal_error;
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
	private BufferedWriter driftDesciptionWriter;
	private BufferedWriter switchPointDescriptionWriter;
	private BufferedWriter volatilityIntervalDescriptionWriter;
	

	
	private int currentBlockIndex;
	private int numberInstance;
	private int maxInstancesCount;
	private MultipleConceptDriftStreamGenerator3 currentBlock;
	
	// state the index of current expected algorithm
	private int currentAlgorithmIndex;
	
	// denote the expected head index of the interval of a volatility mode. 
	private int intervalHead; 
	private int blockLength;
	
	private static final long serialVersionUID = 7628833159490333423L;

	public void setChanges(int[] changes)
	{
		this.changes = changes;
	}
	
	public VolatilityChangeStreamGenerator(int numAtt, int numClass, int[] changes, int driftAttsNum, int blockLength, int interleavedWindowSize, 
			int randomSeedInt, int startClassifier, File descriptionFileDir, int noisePercentage)
	{
		this.currentBlockIndex = 0;
		this.numberInstance = 0;
		
		this.blockLength = blockLength;
		this.changes = changes;
		random = new Random(randomSeedInt);
		
		// first block
//		currentBlock = new MultipleConceptDriftStreamGenerator3();
		currentBlock = new MultipleConceptDriftStreamGenerator3();
		currentBlock.getOptions().resetToDefaults();
		currentBlock.numAttsOption.setValue(numAtt);
		currentBlock.numClassesOption.setValue(numClass);
		currentBlock.streamLengthOption.setValue(blockLength);
		currentBlock.numDriftsOption.setValue(changes[currentBlockIndex]);
		currentBlock.widthOption.setValue(interleavedWindowSize);
		currentBlock.numDriftAttsOption.setValue(driftAttsNum);
		currentBlock.drifIntOptiontRandomSeedOption.setValue(randomSeedInt);
		currentBlock.driftRandom = random;
		currentBlock.noisePercentageOption.setValue(noisePercentage);
		
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
		writeToFile(driftDesciptionWriter, currentBlock.getDriftPosition()+"\n");
		
		
		// expected switch point Description 
		try
		{
			switchPointDescriptionWriter = new BufferedWriter(new FileWriter(
					new File(descriptionFileDir.getAbsolutePath() + "/switchDescription.csv")));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		writeToFile(switchPointDescriptionWriter, "ExpectedSwitchIndex, SwitchTo\n");
		
		// expected volatility internval Description 
		try
		{
			volatilityIntervalDescriptionWriter = new BufferedWriter(new FileWriter(
					new File(descriptionFileDir.getAbsolutePath() + "/volExpectedIntervalDescription.csv")));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		writeToFile(volatilityIntervalDescriptionWriter, "Head, Tail, Mode\n");
		
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
			
			int instancesNumInPreviousBlocks = currentBlockIndex*this.blockLength;
			
			if(currentBlock.hasNewDriftPosition()){
				writeToFile(driftDesciptionWriter, instancesNumInPreviousBlocks+currentBlock.getDriftPosition()+"\n");
			}
			
			
			// in end of the stream, output the last interval. 
			if(numberInstance==maxInstancesCount)
			{
				writeToFile(volatilityIntervalDescriptionWriter, intervalHead + "," + (numberInstance - 1) + "," + currentAlgorithmIndex + "\n");
			}
			
			return inst;
		}
		else
		{
			
			currentBlockIndex++;
			currentBlock.setStream1(currentBlock.getStream2());
			currentBlock.numDriftsOption.setValue(changes[currentBlockIndex]);
			currentBlock.restartOnlyParameters();
			
			Example inst = currentBlock.nextInstance();
			numberInstance++;
				
			int switchTo;
//			int switchTo = changes[currentBlockCount] > changes[currentBlockCount - 1] ? 2:1;
			if(changes[currentBlockIndex] > changes[currentBlockIndex - 1])
			{
				switchTo = 2;
			}
			else if (changes[currentBlockIndex] < changes[currentBlockIndex - 1]){
				switchTo = 1;
			}
			else{
				switchTo = currentAlgorithmIndex;
			}
			
			if(switchTo!=currentAlgorithmIndex)
			{
				// output expected interval TODO
				writeToFile(volatilityIntervalDescriptionWriter, intervalHead + "," + (numberInstance - 1) + "," + currentAlgorithmIndex + "\n");
				intervalHead = numberInstance;

				
				// output expected switch point
				writeToFile(switchPointDescriptionWriter, numberInstance+","+switchTo +"\n");
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
	{}

}
