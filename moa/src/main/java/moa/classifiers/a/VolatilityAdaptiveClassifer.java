package moa.classifiers.a;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

	private CutPointDetector cutPointDetector;
	private int switchStartHeatingPeriod = 100000;
	
	
	// private BufferedWriter volatitlityDriftWriter;
	private BufferedWriter switchPointDescriptionWriter;
	private BufferedWriter volIntervalDescriptionWriter;
	private BufferedWriter currentVolatilityLevelDumpWriter;
	private CurrentVolatilityMeasure currentVolatilityMeasure;
	// currentVolatilityMeasure = new AverageCurrentDriftIntervalMeasure(50,
	// cutPointDetector);

	private MyAbstractClassifier activeClassifier;

	private ClassifierSelector classiferSelector;

	private int activeClassifierIndex;
	private int numInstance;
	private int noDriftCount;

	// private int decisionMode;

	private int intervalStart;

	// private ParameterInjector parameterInjector;

	static final boolean DEBUG_MODE = true;

	public VolatilityAdaptiveClassifer(CutPointDetector cutPointDetector)
	{
		this.cutPointDetector = cutPointDetector;
	}
	// public VolatilityAdaptiveClassifer(ParameterInjector p)
	// {
	// this.parameterInjector = p;
	// }

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

		if(false)
		{
			this.activeClassifier = new HoeffdingTreeADWIN();
			activeClassifier.getOptions().resetToDefaults();
			activeClassifier.resetLearning();
		}
		else
		{

			this.activeClassifier = new HoeffdingAdaptiveTree();
			activeClassifier.getOptions().resetToDefaults();
			activeClassifier.resetLearning();
			
		}


		// classifier 2
		// this.classifier2 = (AbstractClassifier)
		// getPreparedClassOption(this.classifier2Option);

		classiferSelector = new DoubleReservoirsClassifierSelector(10, 3000);
		// CUSUM cusum = new CUSUM(10);
		// cutPointDetector = cusum;

		currentVolatilityMeasure = new RelativeVolatilityDetectorMeasure(cutPointDetector, 32);
//		currentVolatilityMeasure = new SimpleCurrentVolatilityMeasure(0.002);
//		currentVolatilityMeasure = new RelativeVolatilityDetectorMeasureNoCutpointDect(32) ;
//		 currentVolatilityMeasure = new AverageCurrentDriftIntervalMeasure(10, cutPointDetector, 2000);

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
			switchPointDescriptionWriter.write("ClassifierChangePoint,ClassifierIndex\n");

			volIntervalDescriptionWriter = new BufferedWriter(new FileWriter(dir + "/volSwitchIntervalDesc.csv"));
			volIntervalDescriptionWriter.write("Head,Tail,Mode\n");

			currentVolatilityLevelDumpWriter = new BufferedWriter(new FileWriter(dir + "/currentVolLevelDesc.csv"));
			currentVolatilityLevelDumpWriter.write("Instance Index, CurrentVolatilityInterval\n");

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

		activeClassifierIndex = 1;

	}

	// EVAL
	// int time1 = 0;
	// int time2 = 0;
	// int time3 = 0;

	@Override
	public void trainOnInstanceImpl(Instance inst)
	{
		// // EVALUATE block 1 7300
		// long startTime = System.currentTimeMillis();
		int currentVolatilityLevel = currentVolatilityMeasure.setInput(correctlyClassifies(inst) ? 0.0 : 1.0);
		
		if (currentVolatilityMeasure.conceptDrift())
		{
			noDriftCount = 0;
			activeClassifier.notifyConceptDrift();
		} else
		{
			noDriftCount++;
		}
		// // EVALUATE
		//// time1 += System.currentTimeMillis() - startTime;
		//
		// // EVALUATE block 2 222
		//// startTime = System.currentTimeMillis();

		// // if there is a shift.
		if (numInstance>switchStartHeatingPeriod && currentVolatilityLevel != -1)
		{
			
			if (DEBUG_MODE)
			{
				writeToFile(currentVolatilityLevelDumpWriter, numInstance + "," + currentVolatilityLevel + "\n");
			}

			int decision = classiferSelector.makeDecision(currentVolatilityLevel);
//			 int decision = 2;

			if (activeClassifierIndex != decision)
			{
				if (decision == 1)
				{
					this.activeClassifier = new HoeffdingAdaptiveTree();
					activeClassifier.getOptions().resetToDefaults();
					activeClassifier.resetLearning();
				} else
				{
					this.activeClassifier = new HoeffdingTreeADWIN();
					activeClassifier.getOptions().resetToDefaults();
					activeClassifier.resetLearning();
				}

				int previousClassifierIndex = activeClassifierIndex;

				activeClassifierIndex = decision;
//				 activeClassifierIndex = 2;

				if (DEBUG_MODE)
				{
					// switch point dump
					writeToFile(switchPointDescriptionWriter, numInstance + "," + decision + "\n");
					// interval dump
					writeToFile(volIntervalDescriptionWriter,
							intervalStart + "," + numInstance + "," + previousClassifierIndex + "\n");
					intervalStart = numInstance + 1;
					
					
				}
				if (activeClassifierIndex == 2 && noDriftCount > (classiferSelector.getMeasure()))
				{
					this.activeClassifier = new HoeffdingTreeADWIN();
					activeClassifier.getOptions().resetToDefaults();
					activeClassifier.resetLearning();
					activeClassifierIndex = 1;
		
					if (DEBUG_MODE)
					{
						// switch point dump
						writeToFile(switchPointDescriptionWriter, numInstance + "," + 1 + "\n");
						// interval dump
						writeToFile(volIntervalDescriptionWriter, intervalStart + "," + numInstance + "," + 2 + "\n");
					}
				}
			}
		}
		
		numInstance++;

		// time2 += System.currentTimeMillis() - startTime;

		// EVALUATE block 3 28430
		// startTime = System.currentTimeMillis();
		activeClassifier.trainOnInstance(inst);
		// time3 += System.currentTimeMillis() - startTime;
		// totalTime += System.currentTimeMillis() - startTime;

		// System.out.println(((ADWIN)cutPointDetector).getEstimation());
	}
	
//	@Override
//	public void trainOnInstanceImpl(Instance inst)
//	{
//		// // EVALUATE block 1 7300
//		// long startTime = System.currentTimeMillis();
//		int currentVolatilityLevel = currentVolatilityMeasure.setInput(activeClassifier.getIsDrift());
//		
//		
////		if (currentVolatilityMeasure.conceptDrift())
////		{
////			noDriftCount = 0;
////			activeClassifier.notifyConceptDrift();
////		} else
////		{
////			noDriftCount++;
////		}
//		// // EVALUATE
//		//// time1 += System.currentTimeMillis() - startTime;
//		//
//		// // EVALUATE block 2 222
//		//// startTime = System.currentTimeMillis();
//
//		// // if there is a shift.
//		if (currentVolatilityLevel != -1)
//		{
//			if (DEBUG_MODE)
//			{
//				writeToFile(currentVolatilityLevelDumpWriter, numInstance + "," + currentVolatilityLevel + "\n");
//			}
//
////			int decision = classiferSelector.makeDecision(currentVolatilityLevel);
//			 int decision = 2;
//
//			if (activeClassifierIndex != decision)
//			{
//				if (decision == 1)
//				{
//					this.activeClassifier = new HoeffdingTreeADWIN();
//					activeClassifier.getOptions().resetToDefaults();
//					activeClassifier.resetLearning();
//				} else
//				{
//					this.activeClassifier = new HoeffdingAdaptiveTree();
//					activeClassifier.getOptions().resetToDefaults();
//					activeClassifier.resetLearning();
//				}
//
//				int previousClassifierIndex = activeClassifierIndex;
//
//				activeClassifierIndex = decision;
////				activeClassifierIndex = 2;
//
//				if (DEBUG_MODE)
//				{
//					// switch point dump
//					writeToFile(switchPointDescriptionWriter, numInstance + "," + decision + "\n");
//					// interval dump
//					writeToFile(volIntervalDescriptionWriter,
//							intervalStart + "," + numInstance + "," + previousClassifierIndex + "\n");
//					intervalStart = numInstance + 1;
//				}
//			}
//		}
//
//		// if there is no drift in long enough period, switch back to low
//		// volatility algorithm
//		if (activeClassifierIndex == 2 && noDriftCount > (classiferSelector.getMeasure()) * 10)
//		{
//			this.activeClassifier = new HoeffdingTreeADWIN();
//			activeClassifier.getOptions().resetToDefaults();
//			activeClassifier.resetLearning();
//			activeClassifierIndex = 1;
//
//			if (DEBUG_MODE)
//			{
//				// switch point dump
//				writeToFile(switchPointDescriptionWriter, numInstance + "," + 1 + "\n");
//				// interval dump
//				writeToFile(volIntervalDescriptionWriter, intervalStart + "," + numInstance + "," + 2 + "\n");
//			}
//		}
//		numInstance++;
//
//		// time2 += System.currentTimeMillis() - startTime;
//
//		// EVALUATE block 3 28430
//		// startTime = System.currentTimeMillis();
//		activeClassifier.trainOnInstance(inst);
//		// time3 += System.currentTimeMillis() - startTime;
//		// totalTime += System.currentTimeMillis() - startTime;
//
//		// System.out.println(((ADWIN)cutPointDetector).getEstimation());
//	}

	/**
	 * Call this method after training complete.
	 */
	@Override
	public void cleanup()
	{
		// EVAL
		// System.out.println("block1: "+time1);
		// System.out.println("block2: "+time2);
		// System.out.println("block3: "+time3);
		//
		// System.out.println("total: "+(time1+time2+time3));
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

		// EVALUATE
		// System.out.println(innerrun);
		// System.out.println(outterrun);
		// System.out.println(partialTime);
		// System.out.println(totalTime);
	}

	// in optimised version, this method should be removed.
	// private int getDecision(int currentVoaltilityLevel)
	// {
	// if(this.decisionMode==DecisionMode.AWALYS_1)
	// {
	// return 1;
	// }else if(this.decisionMode==DecisionMode.AWALYS_2)
	// {
	// return 2;
	// }
	// return classiferSelector.makeDecision(currentVoaltilityLevel);
	// }

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