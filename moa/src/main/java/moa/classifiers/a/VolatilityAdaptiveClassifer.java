package moa.classifiers.a;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.github.javacliparser.FileOption;
import com.yahoo.labs.samoa.instances.Instance;
import a.tools.ParameterInjector;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.a.other.ClassifierSelector;
import moa.classifiers.a.other.CurrentVolatilityMeasure;
import moa.classifiers.a.other.DoubleReservoirsClassifierSelector;
import moa.classifiers.a.other.RelativeVolatilityDetectorMeasure;
import moa.classifiers.a.other.SimpleCurrentVolatilityMeasure;
import moa.classifiers.trees.HoeffdingAdaptiveTree;
import moa.core.Measurement;
import moa.options.ClassOption;
import volatilityevaluation.RelativeVolatilityDetector;
import weka.core.pmml.FieldMetaInfo.Interval;

public class VolatilityAdaptiveClassifer extends AbstractClassifier
{

	private static final long serialVersionUID = -220640148754624744L;

	public ClassOption classifier1Option = new ClassOption("classifier1", 'a',
			"The classifier used in low volatility mode", Classifier.class, "moa.classifiers.a.HoeffdingTreeADWIN");
	public ClassOption classifier2Option = new ClassOption("classifier2", 'b',
			"The classifier used in high volatility mode", Classifier.class,
			"moa.classifiers.trees.HoeffdingAdaptiveTree");

	public FileOption dumpFileDirOption = new FileOption("dumpFileDirOption", 'v',
			"Dir.", null, "csv", true);

	private BufferedWriter volatitlityDriftWriter;
	private BufferedWriter switchPointDescriptionWriter;
	private BufferedWriter volIntervalDescriptionWriter;
	private BufferedWriter currentVolatilityLevelDumpWriter;
	private CurrentVolatilityMeasure currentVolatilityMeasure;
	
	private AbstractClassifier classifier1;
	private AbstractClassifier classifier2;
	private AbstractClassifier activeClassifier;

	private ClassifierSelector classiferSelector; 
	
	private int activeClassifierIndex;
	private int numInstance;
	
	private int decisionMode;
	
	private int intervalStart;
	
	private ParameterInjector parameterInjector;
	

	public VolatilityAdaptiveClassifer()
	{
	}
	public VolatilityAdaptiveClassifer(ParameterInjector p)
	{
		this.parameterInjector = p;
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
		classiferSelector = new DoubleReservoirsClassifierSelector(10, 0.0); 
//		currentVolatilityMeasure = new SimpleCurrentVolatilityMeasure(0.0002);
		currentVolatilityMeasure = new RelativeVolatilityDetectorMeasure(0.005);
//		currentVolatilityMeasure = parameterInjector.getcurrentVolatilityMeasureObject();
		
//		decisionMode = parameterInjector.getDecisionMode();
		decisionMode = 1;
		
		//set writers
		
		try
		{
			File dir = dumpFileDirOption.getFile();
			if(!(dir.exists() && dir.isDirectory())) dir.mkdirs();

				volatitlityDriftWriter = new BufferedWriter(new FileWriter(dir+"/driftDess.csv"));
				volatitlityDriftWriter.write("VolatilityDriftInstance,CurrentAvgIntervals\n");
			
				switchPointDescriptionWriter = new BufferedWriter(new FileWriter(dir+"/switchPointDesc.csv"));
				switchPointDescriptionWriter.write("ClassifierChangePoint,ClassifierIndex\n");

				volIntervalDescriptionWriter = new BufferedWriter(new FileWriter(dir+"/volSwitchIntervalDesc.csv"));
				volIntervalDescriptionWriter.write("Head,Tail,Mode\n");
				
				currentVolatilityLevelDumpWriter = new BufferedWriter(new FileWriter(dir+"/currentVolLevelDesc.csv"));
				currentVolatilityLevelDumpWriter.write("Instance Index, CurrentVolatilityInterval\n");
				


				
				// 
			
			//volIntervalDescriptionWriter

		} catch (IOException e)
		{

		}

//		volatilityDriftDetector = new RelativeVolatilityDetector(new ADWIN(0.0001), 32);
		numInstance = 0;
		intervalStart = 0;
		
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
			// EVALUATE CODE
			// current volatility level dump
			writeToFile(currentVolatilityLevelDumpWriter, numInstance+","+currentVoaltilityLevel +"\n");
			
			int decision = classiferSelector.makeDecision(currentVoaltilityLevel);
//			int decision = 1;
			
			
			if (activeClassifierIndex != decision)
			{	
				activeClassifier = (decision == 1) ? classifier1 : classifier2;
				int previousClassifierIndex = activeClassifierIndex;
				activeClassifierIndex = decision;
				
				// EVALUATE CODE
				// switch point dump
				writeToFile(switchPointDescriptionWriter, numInstance+","+decision+"\n");
				
				// EVALUATE CODE
				// interval dump
				writeToFile(volIntervalDescriptionWriter, intervalStart+","+numInstance+","+previousClassifierIndex+"\n");
				intervalStart = numInstance + 1;
			}
		}
		numInstance++;
		activeClassifier.trainOnInstance(inst);

	}
	
	/**
	 * Call this method after training complete. 
	 */
	@Override
	public void cleanup()
	{
		writeToFile(volIntervalDescriptionWriter, intervalStart+","+numInstance+","+activeClassifierIndex+"\n");
		intervalStart = numInstance + 1;
	}
	
	// in optimised version, this method should be removed. FIXME
	private int getDecision(int currentVoaltilityLevel)
	{
		if(this.decisionMode==DecisionMode.AWALYS_1)
		{
			return 1;
		}else if(this.decisionMode==DecisionMode.AWALYS_2)
		{
			return 2;
		}
		return classiferSelector.makeDecision(currentVoaltilityLevel);
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

class DecisionMode
{
	public static final int AWALYS_1 = 1;
	public static final int AWALYS_2 = 2;
	public static final int REAL_DECISION = 3;
	
}