package moa.classifiers.a;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.WriteAbortedException;
import java.util.PrimitiveIterator.OfDouble;

import org.omg.CORBA.Current;

import com.github.javacliparser.FileOption;
import com.yahoo.labs.samoa.instances.Instance;

import a.tools.Directory;
import classifiers.selectors.AlwaysFirstClassifierSelector;
import classifiers.selectors.NaiveClassifierSelector;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.a.other.ClassifierSelector;
import moa.classifiers.a.other.CurrentVolatilityMeasure;
import moa.classifiers.a.other.DoubleReservoirsClassifierSelector;
import moa.classifiers.a.other.SimpleCurrentVolatilityMeasure;
import moa.classifiers.trees.HoeffdingAdaptiveTree;
import cutpointdetection.ADWIN;
import moa.core.Measurement;
import moa.options.ClassOption;
import volatilityevaluation.RelativeVolatilityDetector;

public class VolatilityAdaptiveClassifer extends AbstractClassifier
{

	private static final long serialVersionUID = -220640148754624744L;

	public ClassOption classifier1Option = new ClassOption("classifier1", 'a',
			"The classifier used in low volatility mode", Classifier.class, "moa.classifiers.a.HoeffdingTreeADWIN");
	public ClassOption classifier2Option = new ClassOption("classifier2", 'b',
			"The classifier used in high volatility mode", Classifier.class,
			"moa.classifiers.trees.HoeffdingAdaptiveTree");

	public FileOption volatitlityDriftDumpFileOption = new FileOption("volatitlityDriftDumpFile", 'v',
			"Destination csv file.", null, "csv", true);

	public FileOption classifierChangePointDumpFileOption = new FileOption("classifierChangePointDumpFileOption", 'c',
			"Destination csv file.", null, "csv", true);
	
	public FileOption currentVolatilityLevelWriterDumpFileOption = new FileOption("currentVolatilityLevelWriter", 'h',
			"Destination csv file.", null, "csv", true);

	private BufferedWriter volatitlityDriftWriter;
	private BufferedWriter classifierChangePointDumpWriter;
	private BufferedWriter currentVolatilityLevelDumpWriter;
	private CurrentVolatilityMeasure currentVolatilityMeasure;
	
	private AbstractClassifier classifier1;
	private AbstractClassifier classifier2;
	private AbstractClassifier activeClassifier;

	private ClassifierSelector classiferSelector; 
	
	private int activeClassifierIndex;
	private int instanceCount;
	
	
	public VolatilityAdaptiveClassifer() 
	{
		resetLearningImpl();
	}
	


	@Override
	public boolean isRandomizable()
	{
		return false;
	}

	@Override
	public void getModelDescription(StringBuilder arg0, int arg1)
	{

	}

	/** return the information of the current algorithm */
	@Override
	protected Measurement[] getModelMeasurementsImpl()
	{
		
		return activeClassifier.getModelMeasurements();
	}

	@Override
	public double[] getVotesForInstance(Instance inst)
	{
		return activeClassifier.getVotesForInstance(inst);
	}

	@Override
	public void resetLearningImpl()
	{
		
		initClassifiers();
		activeClassifier = classifier1;		
		classiferSelector = new DoubleReservoirsClassifierSelector(300, 0.0); 
		currentVolatilityMeasure = new SimpleCurrentVolatilityMeasure(0.00001);
		
		
		
		//set writers
		
		try
		{

			File volatitlityDriftDumpFile = volatitlityDriftDumpFileOption.getFile();
			if (volatitlityDriftDumpFile != null)
			{
				volatitlityDriftWriter = new BufferedWriter(new FileWriter(volatitlityDriftDumpFile));
				volatitlityDriftWriter.write("VolatilityDriftInstance,CurrentAvgIntervals\n");
			}
			
			File classifierChangePointDumpFile = classifierChangePointDumpFileOption.getFile();
			if(classifierChangePointDumpFile!=null)
			{
				classifierChangePointDumpWriter = new BufferedWriter(new FileWriter(classifierChangePointDumpFile));
				classifierChangePointDumpWriter.write("ClassifierChangePoint,ClassifierIndex\n");
			}
			
			File currentVolatilityLevelDumpFile = currentVolatilityLevelWriterDumpFileOption.getFile();
			if(currentVolatilityLevelDumpFile!=null)
			{
				currentVolatilityLevelDumpWriter = new BufferedWriter(new FileWriter(currentVolatilityLevelDumpFile));
				currentVolatilityLevelDumpWriter.write("Instance Index, CurrentVolatilityInterval\n");
			}

		} catch (IOException e)
		{

		}

//		volatilityDriftDetector = new RelativeVolatilityDetector(new ADWIN(0.0001), 32);
		instanceCount = 0;

		activeClassifierIndex = 1;
		activeClassifier = classifier1;

	}

	private void initClassifiers()
	{
		// classifier 1
//		this.classifier1 = (AbstractClassifier) getPreparedClassOption(this.classifier1Option);
		this.classifier1 = new HoeffdingTreeADWIN();
		classifier1.getOptions().resetToDefaults();
		classifier1.resetLearning();
		
		// classifier 2
//		this.classifier2 = (AbstractClassifier) getPreparedClassOption(this.classifier2Option);
		this.classifier2 = new HoeffdingAdaptiveTree();
		classifier2.getOptions().resetToDefaults();
		classifier2.resetLearning();
	}

	
	// Use volatility Drift
//	@Override
//	public void trainOnInstanceImpl(Instance inst)
//	{
//		// if there is a volatility shift.
//		if (volatilityDriftDetector.setInputVar(correctlyClassifies(inst) ? 0.0 : 1.0))
//		//if(false)
//		{
//
//			double avgInterval = volatilityDriftDetector.getBufferMean();
//			writeToFile(volatitlityDriftWriter, instanceCount+","+avgInterval+"\n");
//			
//			int decision = classiferSelector.makeDecision(avgInterval);
//
//			if (activeClassifierIndex != decision)
//			{	
//				activeClassifier = (decision == 1) ? classifier1 : classifier2;
//				activeClassifierIndex = decision;
//				writeToFile(classifierChangePointDumpWriter, instanceCount+","+decision+"\n");
//			}
//		}
//		instanceCount++;
//		activeClassifier.trainOnInstance(inst);
//
//	}
	
	
	public void trainOnInstanceImpl(Instance inst)
	{
		
		int currentVoaltilityLevel = currentVolatilityMeasure.setInput(correctlyClassifies(inst) ? 0.0 : 1.0);
		// if there is a concept shift.
		if (currentVoaltilityLevel!=-1)
		{
			
			// current volatility level dump
			writeToFile(currentVolatilityLevelDumpWriter, instanceCount+","+currentVoaltilityLevel +"\n");
			
			int decision = classiferSelector.makeDecision(currentVoaltilityLevel);

			if (activeClassifierIndex != decision)
			{	
				activeClassifier = (decision == 1) ? classifier1 : classifier2;
				activeClassifierIndex = decision;
				
				//classifier change point dump
				writeToFile(classifierChangePointDumpWriter, instanceCount+","+decision+"\n");

			}
		}
		instanceCount++;
		activeClassifier.trainOnInstance(inst);

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

}
