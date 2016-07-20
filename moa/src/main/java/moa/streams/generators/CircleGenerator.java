
package moa.streams.generators;

import java.util.Random;

import javax.annotation.Untainted;

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import moa.core.FastVector;
import moa.core.InstanceExample;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;
import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.InstancesHeader;


public class CircleGenerator extends AbstractOptionHandler implements
        InstanceStream {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public String getPurposeString() {
        return "";
    }

	private int numAtts; 
	private Random random;
	private double[] circleCentre;
	private double radius;
	private double outterRadius; 
	private InstancesHeader streamHeader;

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        monitor.setCurrentActivity("Preparing hyperplane...", -1.0);
        generateHeader();
        restart();
    }
    
	public CircleGenerator(int numAtts, int randomSeed)
	{
		this.numAtts = numAtts;
		
		this.outterRadius = Math.sqrt(radius) - radius;
		
		random = new Random(randomSeed);
		circleCentre = new double[numAtts];
		for(int i=0;i<numAtts;i++)
		{
			circleCentre[i] = random.nextDouble();
		}
		radius = random.nextDouble();
		generateHeader();
		
	}

    protected void generateHeader() {
        FastVector attributes = new FastVector();
        for (int i = 0; i < this.numAtts; i++) {
            attributes.addElement(new Attribute("att" + (i + 1)));
        }

        FastVector classLabels = new FastVector();
        for (int i = 0; i < 2; i++) {
            classLabels.addElement("class" + (i + 1));
        }
        attributes.addElement(new Attribute("class", classLabels));
        this.streamHeader = new InstancesHeader(new Instances(
                getCLICreationString(InstanceStream.class), attributes, 0));
        this.streamHeader.setClassIndex(this.streamHeader.numAttributes() - 1);
    }

    @Override
    public long estimatedRemainingInstances() {
        return -1;
    }

    @Override
    public InstancesHeader getHeader() {
        return this.streamHeader;
    }

    @Override
    public boolean hasMoreInstances() {
        return true;
    }

    @Override
    public boolean isRestartable() {
        return true;
    }

    @Override
    public InstanceExample nextInstance() {
        double[] attVals = new double[numAtts + 1];
        
        double squareSum = 0;
        
        for (int i = 0; i < numAtts; i++) {
            attVals[i] = this.random.nextDouble(); 
            squareSum += Math.pow(attVals[i] - circleCentre[i], 2);
            
        }
        double distance = Math.sqrt(squareSum);
        
        int classLabel;
        if(distance<radius)
        {
        	classLabel = 0;
        }
        else{
        	classLabel = 1;
        }
        Instance inst = new DenseInstance(this.streamHeader.numAttributes());
        for(int i=0;i<attVals.length;i++)
        {
        	inst.setValue(i, attVals[i]);
        }
        inst.setDataset(getHeader());
        inst.setClassValue(classLabel);
        
		
		return new InstanceExample(inst);

    }


    


    @Override
    public void restart() {

    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }
}
