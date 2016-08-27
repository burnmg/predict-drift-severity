package moa.classifiers.a;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.security.PrivilegedActionException;

import org.omg.CORBA.PRIVATE_MEMBER;

import com.github.javacliparser.FileOption;
import com.yahoo.labs.samoa.instances.Instance;
import a.tools.ParameterInjector;
import cutpointdetection.ADWIN;
import cutpointdetection.CUSUM;
import cutpointdetection.CutPointDetector;
import moa.classifiers.AbstractClassifier;
import moa.classifiers.Classifier;
import moa.classifiers.MyAbstractClassifier;
import moa.classifiers.a.VAC.other.AverageCurrentDriftIntervalMeasure;
import moa.classifiers.a.VAC.other.ClassifierSelector;
import moa.classifiers.a.VAC.other.CurrentVolatilityMeasure;
import moa.classifiers.a.VAC.other.DoubleReservoirsClassifierSelector;
import moa.classifiers.a.VAC.other.DoubleReservoirsHighForHighLowForLow;
import moa.classifiers.a.VAC.other.RelativeVolatilityDetectorMeasure;
import moa.classifiers.a.VAC.other.RelativeVolatilityDetectorMeasureNoCutpointDect;
import moa.classifiers.a.VAC.other.SimpleCurrentVolatilityMeasure;
import moa.classifiers.multilabel.trees.ISOUPTree.InnerNode;
import moa.classifiers.trees.HoeffdingAdaptiveTree;
import moa.core.Measurement;
import moa.options.ClassOption;

public class VolatilityAdaptiveClassifer extends AbstractClassifier
{

	private static final long serialVersionUID = -220640148754624744L;

	public ClassOption classifier1Option = new ClassOption("classifier1", 'a',
			"The classifier used in low volatility mode", Classifier.class, "moa.classifiers.a.HoeffdingTreeADWIN");
	public ClassOption classifier2Option = new ClassOption("classifier2", 'b',
			"The classifier used in high volatility mode", Classifier.class,
			"moa.classifiers.trees.HoeffdingAdaptiveTree");

	public FileOption dumpFileDirOption = new FileOption("dumpFileDirOption", 'v', "Dir.", null, "csv", true);

	// private CutPointDetector cutPointDetector;
	private int switchStartHeatingPeriod = 0;
	
	
	// private BufferedWriter volatitlityDriftWriter;
	private BufferedWriter switchPointDescriptionWriter;
	private BufferedWriter volIntervalDescriptionWriter;
	private BufferedWriter currentVolatilityLevelDumpWriter;
	private CurrentVolatilityMeasure currentVolatilityMeasure;
	// currentVolatilityMeasure = new AverageCurrentDriftIntervalMeasure(50,
	// cutPointDetector);

	private MyAbstractClassifier activeClassifier;
	private MyAbstractClassifier classifier1;
	private MyAbstractClassifier classifier2;

	private ClassifierSelector classiferSelector;

	private int activeClassifierIndex;
	private long numInstance;
	private int noDriftCount;

	// private int decisionMode;

	private long intervalStart;

	private int allowableVolFluctuation;

	private int measurePeriod;

	// private ParameterInjector parameterInjector;

	static final boolean DEBUG_MODE = true;

	public VolatilityAdaptiveClassifer(CutPointDetector cutPointDetector, CurrentVolatilityMeasure currentVolatilityMeasure, int allowableVolFluctuation, int measurePeriod)
	{
		// this.cutPointDetector = cutPointDetector;
		this.allowableVolFluctuation = allowableVolFluctuation;
		this.currentVolatilityMeasure = currentVolatilityMeasure;
		this.measurePeriod = measurePeriod;
		// currentVolatilityMeasure = new AverageCurrentDriftIntervalMeasure(10, cutPointDetector, 2000);
//		currentVolatilityMeasure = new RelativeVolatilityDetectorMeasure(cutPointDetector, 32);
//		currentVolatilityMeasure = new SimpleCurrentVolatilityMeasure(0.002);
//		currentVolatilityMeasure = new RelativeVolatilityDetectorMeasureNoCutpointDect(32) ;
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
		
		classifier1 = new HoeffdingTreeADWIN();
		classifier1.getOptions().resetToDefaults();
		classifier1.resetLearning();
		
		classifier2 = new HoeffdingAdaptiveTree();
		classifier2.getOptions().resetToDefaults();
		classifier2.resetLearning();
		if(true)
		{
			activeClassifier = classifier1;
			activeClassifierIndex = 1;
		}
		else
		{

			activeClassifier = classifier1;
			activeClassifierIndex = 2;
			
		}


		// classifier 2
		// this.classifier2 = (AbstractClassifier)
		// getPreparedClassOption(this.classifier2Option);

//		classiferSelector = new DoubleReservoirsClassifierSelector(10, allowableVolFluctuation);
		classiferSelector = new DoubleReservoirsHighForHighLowForLow(200, allowableVolFluctuation);
		// CUSUM cusum = new CUSUM(10);
		// cutPointDetector = cusum;


		 

		// set writers

		try
		{
			File dir = dumpFileDirOption.getFile();
			if (!(dir.exists() && dir.isDirectory()))
				dir.mkdirs();

			// volatitlityDriftWriter = new BufferedWriter(new
			// FileWriter(dir+"/driftDess.csv"));
			// volatitlityDriftWriter.write("VolatilityDriftInstance,CurrentAvgIntervals\n");

			switchPointDescriptionWriter = new BufferedWriter(new FileWriter(dir + "/switchPointDesc.csv"));
			switchPointDescriptionWriter.write("ClassifierChangePoint,ClassifierIndex,IsForceSwitch\n");

			volIntervalDescriptionWriter = new BufferedWriter(new FileWriter(dir + "/volSwitchIntervalDesc.csv"));
			volIntervalDescriptionWriter.write("Head,Tail,Mode\n");

			currentVolatilityLevelDumpWriter = new BufferedWriter(new FileWriter(dir + "/currentVolLevelDesc.csv"));
			currentVolatilityLevelDumpWriter.write("Instance Index, CurrentVolatilityInterval,Classifier Selector Threshold,Input\n");
			
			//

			// volIntervalDescriptionWriter

		} catch (IOException e)
		{
			System.out.println(e.getMessage());
		}

		// volatilityDriftDetector = new RelativeVolatilityDetector(new
		// ADWIN(0.0001), 32);
		numInstance = 0;
		intervalStart = 0;

	}

	// EVAL
	// int time1 = 0;
	// int time2 = 0;
	// int time3 = 0;

	@Override
	public void trainOnInstanceImpl(Instance inst)
	{
		int currentVolatilityLevel = currentVolatilityMeasure.setInput(correctlyClassifies(inst) ? 0.0 : 1.0);
		
		if (currentVolatilityMeasure.conceptDrift())
		{
			activeClassifier.notifyConceptDrift();
		} else
		{
			noDriftCount++;
		}

		// if there is a shift.
		if (numInstance > switchStartHeatingPeriod && (currentVolatilityMeasure.conceptDrift() || numInstance % measurePeriod==0))
		{	

			if (DEBUG_MODE)
			{
				if(currentVolatilityMeasure.conceptDrift())
				{
					writeToFile(currentVolatilityLevelDumpWriter, numInstance + "," + currentVolatilityLevel + "," + classiferSelector.getThreshold() + ",TRUE" + "\n");
				}
				else
				{
					writeToFile(currentVolatilityLevelDumpWriter, numInstance + "," + currentVolatilityLevel + "," + classiferSelector.getThreshold() + ",FALSE" + "\n");
				}
				
			}

//			int decision = currentVolatilityMeasure.conceptDrift() ? classiferSelector.input(currentVolatilityLevel) : classiferSelector.getDecision(currentVolatilityLevel);
			int decision = classiferSelector.input(currentVolatilityLevel);


			if (activeClassifierIndex != decision && classiferSelector.getIsActive())
			{
				if (decision == 1)
				{
					classifier2.resetLearning();
					this.activeClassifier = classifier1;
				} 
				else
				{
					classifier1.resetLearning();
					this.activeClassifier = classifier2;
				}
				
				int previousClassifierIndex = activeClassifierIndex;

				activeClassifierIndex = decision;
//				 activeClassifierIndex = 2;

				if (DEBUG_MODE)
				{
					// switch point dump
					writeToFile(switchPointDescriptionWriter, numInstance + "," + decision + "," +"FALSE\n");
					// interval dump
					writeToFile(volIntervalDescriptionWriter,
							intervalStart + "," + numInstance + "," + previousClassifierIndex + "\n");
					intervalStart = numInstance + 1;
					
					
				}
				noDriftCount = 0;
			}
//			else if (activeClassifierIndex == 2 && classiferSelector.getThreshold() > 0.00001 && noDriftCount > (currentVolatilityMeasure.getMaxWindowSize() / classiferSelector.getThreshold()))
//			{
//				classifier2.resetLearning();
//				this.activeClassifier = classifier1;
//				activeClassifierIndex = 1;
//				
//	
//				if (DEBUG_MODE)
//				{
//					// switch point dump
//					writeToFile(switchPointDescriptionWriter, numInstance + "," + 1 + "," + "TRUE\n");
//					// interval dump
//					writeToFile(volIntervalDescriptionWriter, intervalStart + "," + numInstance + "," + 2 + "\n" );
//				}
//			}
			
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
		writeToFile(volIntervalDescriptionWriter,
				intervalStart + "," + numInstance + "," + activeClassifierIndex + "\n");
		intervalStart = numInstance + 1;

		try
		{
			// volatitlityDriftWriter.close();
			switchPointDescriptionWriter.close();
			volIntervalDescriptionWriter.close();
			currentVolatilityLevelDumpWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}


	private void writeToFile(BufferedWriter bw, String str) 
	{
		if (bw != null)
		{
			try
			{
				bw.write(str);
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