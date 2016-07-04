package a.evaluator;

import java.io.File;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.yahoo.labs.samoa.instances.Instance;
import a.tools.Directory;
import a.tools.MyEvaluatePrequential;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.a.HoeffdingTreeADWIN;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.core.Example;
import moa.core.TimingUtils;
import moa.streams.ArffFileStream;
import moa.streams.ExampleStream;
import moa.tasks.EvaluatePrequential;
import moa.tasks.StandardTaskMonitor;


public class EvaluateMain
{

	public static void main(String[] args)
	{
		String streamName = "200,10,200,10,200,10,200,10.arff";
//		HoeffdingTreeADWIN ht = new HoeffdingTreeADWIN();

		
		
		File resultFolder = new File(Directory.root+"/Results/"+streamName);
		resultFolder.mkdirs();
		VolatilityAdaptiveClassifer classifiers = new VolatilityAdaptiveClassifer();
		classifiers.getOptions().resetToDefaults();
		classifiers.currentVolatilityLevelWriterDumpFileOption.setValue(resultFolder.getPath()+"/currentVolatilityLevel.csv");
		classifiers.classifierChangePointDumpFileOption.setValue(resultFolder.getPath()+"/classifierChangePointDumpFile.csv");
		classifiers.resetLearning();
		
//		HoeffdingTreeADWIN classifiers = new HoeffdingTreeADWIN();
//		classifiers.getOptions().resetToDefaults();
//		classifiers.resetLearning();

		evaluate(classifiers, streamName, resultFolder.getPath());

	}
	
	public static void evaluate(AbstractClassifier classifier, String streamName, String resultFolder)
	{
		MyEvaluatePrequential evaluatePrequential = new MyEvaluatePrequential();
		evaluatePrequential.getOptions().resetToDefaults();
		evaluatePrequential.setLearner(classifier);
		evaluatePrequential.setStream(getStreamFromFile(streamName));
		evaluatePrequential.sampleFrequencyOption.setValue(100);
		evaluatePrequential.dumpFileOption.setValue(resultFolder+"/dump.csv");
		
		
		evaluatePrequential.doMainTask(new StandardTaskMonitor(), null);
	}
	
	public static void evaluate2(AbstractClassifier classifier, String streamName)
	{
		ExampleStream stream = getStreamFromFile(streamName);
        classifier.setModelContext(stream.getHeader());
        classifier.resetLearning();
        
        Evaluator overallCorrectRateEvaluator = new CorrectRateEvaluator();
        double totalTimeCost = 0;
        
        long instanceCount = 0;
        
		while(stream.hasMoreInstances())
		{
			System.out.println(instanceCount);
            Example trainInst = stream.nextInstance();
            Example testInst = trainInst; 
            
            //test overall
            double[] votes = classifier.getVotesForInstance((Instance)trainInst.getData());
            overallCorrectRateEvaluator.addResult((Instance)testInst.getData(), votes); 
            
            //TODO test window
            
            //train   
            // measure time
            long startTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            classifier.trainOnInstance(trainInst);
            long endTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            double timeCost = TimingUtils.nanoTimeToSeconds(endTime - startTime);
            totalTimeCost += timeCost;
            
            instanceCount++;
            //TODO measure node
            //TODO measure memory
            
		}
//		System.out.println(overallCorrectRateEvaluator.getOverallMeasurement());
//        System.out.println(totalTimeCost);
		
	}
	
	
	public static ArffFileStream getStreamFromFile(String streamName)
	{
		String path = Directory.root + "Streams/" + streamName + '/' + streamName;
		ArffFileStream stream = new ArffFileStream();
		stream.arffFileOption.setValue(path);
		stream.prepareForUse();
		
		return stream;
	}
	
	public static HoeffdingTreeADWIN getHT()
	{
		HoeffdingTreeADWIN ht = new HoeffdingTreeADWIN();
		ht.getOptions().resetToDefaults();
		ht.maxByteSizeOption.setValue(33000);
		
		return ht;
	}
}
