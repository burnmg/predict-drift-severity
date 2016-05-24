package a.evaluator;


import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.AbstractClassifier;
import moa.core.Example;
import moa.streams.InstanceStream;

public class LinEvaluator
{
	
	public InstanceStream stream;
	public AbstractClassifier classifier;
	
	private double correctSum = 0;
	private int instanceCount = 0;
	
	public void evaluate()
	{
		while(stream.hasMoreInstances())
		{
            Example trainInst = stream.nextInstance();
            Example testInst = (Example) trainInst; 

            correctSum += classifier.correctlyClassifies((Instance)testInst.getData())?1.0:0.0;
            
            classifier.trainOnInstance(trainInst);
            instanceCount++;
			System.out.println(correctSum/instanceCount);
		}
		// System.out.println(correctSum/instanceCount);
	}
	
	
}
