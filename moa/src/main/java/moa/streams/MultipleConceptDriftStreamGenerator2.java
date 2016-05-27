package moa.streams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.stream.BaseStream;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.core.Example;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.options.ClassOption;
import moa.streams.generators.HyperplaneGenerator;
import moa.tasks.TaskMonitor;

public class MultipleConceptDriftStreamGenerator2 extends AbstractOptionHandler implements InstanceStream
{
	private static final long serialVersionUID = 1L;

    private BufferedWriter bw;

    protected int numberInstance;
    
    protected int switchPoint; 
    
    protected int driftPosition;
    
    protected int previousSwitchPoint;
    
    protected Random driftRandom;
    
    protected HyperplaneGenerator hyperplaneGenerator;
    
    public IntOption numDriftsOption = new IntOption("numdrifts", 'd', 
    		"Number of Drifts in the Stream. Must be greater than 1", 1, 1, Integer.MAX_VALUE);
    
    public IntOption streamLengthOption = new IntOption("streamlen", 'l', 
    		"Length of the stream", 1000000);
	
    public FileOption driftDescriptionDumpFileOption = new FileOption("driftDescriptionDumpFile", 'f',
            "Destination Dump file.", null, "csv", true);
    
    public ClassOption streamOption = new ClassOption("stream", 's',
            "Stream to add concept drift.", ExampleStream.class,
            "generators.HyperplaneGenerator");
	
 
    public String getPurposeString() {
        return "Adds multiple Concept Drift to examples in a stream. (Use generators.HyperplaneGenerator as the base generator)";
    }
    
	@Override
	public InstancesHeader getHeader()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long estimatedRemainingInstances()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasMoreInstances()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Example<Instance> nextInstance()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRestartable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void restart()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getDescription(StringBuilder sb, int indent)
	{
		// TODO Auto-generated method stub
		
	}
	
	public int computeNextDriftPosition(int switchPoint, int previousSwitchPoint)
	{
			int mean = (switchPoint - previousSwitchPoint) / 2;
			int blockCentrePosition = previousSwitchPoint + mean;
			int newDriftPosition = blockCentrePosition + (int)(mean * gaussianRandom); 
			return newDriftPosition;
		}

	@Override
	protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository)
	{
		
		switchPoint = streamLengthOption.getValue() / numDriftsOption.getValue();
		previousSwitchPoint = 0;
		driftPosition = computeNextDriftPosition(switchPoint, previousSwitchPoint);
		hyperplaneGenerator = (HyperplaneGenerator) getPreparedClassOption(streamOption);
		
		File dumpFile = driftDescriptionDumpFileOption.getFile();
		if(dumpFile!=null)
		{
			try
			{
				bw = new BufferedWriter(new FileWriter(dumpFile));
				bw.write("dirft point\n");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}

}
