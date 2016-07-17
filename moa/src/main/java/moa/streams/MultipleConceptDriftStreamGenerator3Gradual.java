package moa.streams;

import java.util.Random;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.InstancesHeader;

import Jama.examples.MagicSquareExample;
import moa.core.Example;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.streams.generators.DriftingHyperplaneGenerator;
import moa.tasks.TaskMonitor;


/**
 * This version uses DriftingHyperplaneGenerator
 * @author rl
 *
 */
public class MultipleConceptDriftStreamGenerator3Gradual extends AbstractOptionHandler implements
InstanceStream{

	private static final long serialVersionUID = 1L;
	
    @Override
    public String getPurposeString() {
        return "Adds multiple Concept Drift to examples in a stream. (Use generators.HyperplaneGenerator as the base generator)";
    }
	@Override
	public void getDescription(StringBuilder sb, int indent) {

	}
    
    public IntOption streamLengthOption = new IntOption("streamlen", 'l', 
    		"Length of the stream", 1000000);
    
    public IntOption numDriftsOption = new IntOption("numdrifts", 'd', 
    		"Number of Drifts in the Stream. Must be greater than 1", 1, 1, Integer.MAX_VALUE);
    
    public IntOption widthOption = new IntOption("width",
            'w', "Width of concept drift change.", 1000);
    
    public FloatOption standardDev = new FloatOption("standardDeviation", 'S', "Standard deviation for the gaussian random number used to decide the location of each drift point.", 0.0, 0.0, 2.0);
    
    public IntOption drifIntOptiontRandomSeedOption = new IntOption("driftRandomSeed", 'r',
            "Seed for generating drift streams", 1);

    
    // Options for Hyperplane Generator. In current version, the Hyperplane cannot rotate.
    public IntOption numClassesOption = new IntOption("numClasses", 'c',
            "The number of classes to generate.", 2, 2, Integer.MAX_VALUE);
    
    public IntOption numAttsOption = new IntOption("numAtts", 't',
            "The number of attributes to generate.", 5, 0, Integer.MAX_VALUE);
    
    public IntOption noisePercentageOption = new IntOption("noisePercentage",
            'n', "Percentage of noise to add to the data.", 5, 0, 100);
    
    // Options for partial drifting
    public IntOption numDriftAttsOption = new IntOption("numDriftAtts", 'p',
            "Number of drifting atts", 3);
    

    protected int numberInstance;
    
    private DriftingHyperplaneGenerator stream1;
    
    protected int switchPoint; 
    
    protected int driftPosition;
    
    protected int previousSwitchPoint;
    
    protected Random driftRandom;
    
    private boolean switching;
    
    private int sigma;
    private double mag;
    
    public MultipleConceptDriftStreamGenerator3Gradual()
    {
    	
    }
    
    public void setSigma(int sigma)
	{
		this.sigma = sigma;
	}
    public void setMag(double mag)
	{
		this.mag = mag;
	}
    
	@Override
	public InstancesHeader getHeader() {
		return this.stream1.getHeader();
	}
	
	@Override
	protected void prepareForUseImpl(TaskMonitor monitor,
			ObjectRepository repository) {
		
		driftRandom = new Random(drifIntOptiontRandomSeedOption.getValue());
		switchPoint = streamLengthOption.getValue() / numDriftsOption.getValue();
		previousSwitchPoint = 0;
		driftPosition = computeNextDriftPosition(switchPoint, previousSwitchPoint);
		
		// set stream options. 
//		stream1 = getInitStream();
//		stream2 = getEvolvedStream(stream1);
	}
	
	
	private DriftingHyperplaneGenerator getInitStream()
	{
		
		DriftingHyperplaneGenerator newStream = new DriftingHyperplaneGenerator();
		newStream.getOptions().resetToDefaults();
		newStream.numClassesOption = this.numClassesOption;
		newStream.numAttsOption = this.numAttsOption;
		newStream.instanceRandomSeedOption.setValue(drifIntOptiontRandomSeedOption.getValue());
		newStream.noisePercentageOption = this.noisePercentageOption;
		newStream.sigmaPercentageOption.setValue(sigma);
		newStream.magChangeOption.setValue(mag);
		newStream.prepareForUse();
		
		return newStream;
	}
	

	public void initStream1AndStream2()
	{
		this.stream1 = getInitStream();
	}

	public void setStream1(DriftingHyperplaneGenerator newStream1)
	{
		this.stream1 = newStream1;
	}

	public DriftingHyperplaneGenerator getStream2()
	{
		return stream1;
	}
	
	@Override
	public long estimatedRemainingInstances() {
		return streamLengthOption.getValue() - numberInstance;
	}

	@Override
	public boolean hasMoreInstances() {
		return numberInstance < streamLengthOption.getValue();
	}
	
	
	@Override
	public Example nextInstance() {
		
		numberInstance++;
		
		
		/*
		 * One block contains one drift in the middle. 
		 * Switch is performed at the end of each block. 
		 */
		// perform the switch
		
		if(numberInstance >= switchPoint){
			previousSwitchPoint = switchPoint;
			switchPoint += streamLengthOption.getValue() / numDriftsOption.getValue();
			driftPosition = computeNextDriftPosition(switchPoint, previousSwitchPoint);

			switching = true;
		}
		else
		{
			switching = false;
		}
		
		if(numberInstance< driftPosition + widthOption.getValue()/2.0 && numberInstance > driftPosition - widthOption.getValue()/2.0)
		{
			stream1.setGradualDrift(true);
		}
		else
		{
			stream1.setGradualDrift(false);
		}
		
		return stream1.nextInstance();
	}
	
	private int computeNextDriftPosition(int switchPoint, int previousSwitchPoint){
		
		double gaussianRandom = driftRandom.nextGaussian()*standardDev.getValue();
		
		
		// limit the range of gaussianRandom within [-0.9, 0.9]. 
		if (gaussianRandom > 0.9)
		{
			gaussianRandom = 0.9;
		}else if (gaussianRandom < -0.9)
		{
			gaussianRandom = -0.9;
		}
		
		
		int mean = (switchPoint - previousSwitchPoint) / 2;
		int blockCentrePosition = previousSwitchPoint + mean;
		int newDriftPosition = blockCentrePosition + (int)(mean * gaussianRandom); 
		//int newDriftpos = driftPositionMean + (int)(blockCentrePoint); 
		return newDriftPosition;
	}

	@Override
	public boolean isRestartable() {
		return this.stream1.isRestartable();
	}

	@Override
	public void restart() {
		this.stream1.restart();
		
		numberInstance = 0;
		switchPoint = streamLengthOption.getValue() / numDriftsOption.getValue();
		previousSwitchPoint = 0;
		
		
	}

	public void restartOnlyParameters()
	{
		numberInstance = 0;
		switchPoint = streamLengthOption.getValue() / numDriftsOption.getValue();
		previousSwitchPoint = 0;
	}

	public boolean hasNewDriftPosition()
	{
		return switching;
	}

	public int getDriftPosition()
	{
		return this.driftPosition;
	}
}
