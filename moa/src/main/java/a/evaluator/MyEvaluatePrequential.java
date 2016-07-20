/*
 *    EvaluatePrequential.java
 *    Copyright (C) 2007 University of Waikato, Hamilton, New Zealand
 *    @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 *    @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 *    
 */
package a.evaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import moa.core.Example;
import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.core.TimingUtils;
import moa.evaluation.WindowClassificationPerformanceEvaluator;
import moa.evaluation.EWMAClassificationPerformanceEvaluator;
import moa.evaluation.FadingFactorClassificationPerformanceEvaluator;
import moa.evaluation.LearningCurve;
import moa.evaluation.LearningEvaluation;
import moa.evaluation.LearningPerformanceEvaluator;
import moa.evaluation.UnslidingWindowClassificationPerformanceEvaluator;
import moa.learners.Learner;
import moa.options.ClassOption;

import com.github.javacliparser.FileOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import moa.streams.ExampleStream;
import moa.tasks.MainTask;
import moa.tasks.TaskMonitor;

import com.yahoo.labs.samoa.instances.Instance;
import moa.core.Utils;

/**
 * Task for evaluating a classifier on a stream by testing then training with each example in sequence.
 *
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class MyEvaluatePrequential extends MainTask {

    @Override
    public String getPurposeString() {
        return "Evaluates a classifier on a stream by testing then training with each example in sequence.";
    }

    private static final long serialVersionUID = 1L;

//    public ClassOption learnerOption = new ClassOption("learner", 'l',
//            "Learner to train.", Classifier.class, "moa.classifiers.bayes.NaiveBayes");

//    public ClassOption streamOption = new ClassOption("stream", 's',
//            "Stream to learn from.", ExampleStream.class,
//            "generators.RandomTreeGenerator");

//    public ClassOption evaluatorOption = new ClassOption("evaluator", 'e',
//            "Classification performance evaluation method.",
//            LearningPerformanceEvaluator.class,
//            "WindowClassificationPerformanceEvaluator");

    public IntOption instanceLimitOption = new IntOption("instanceLimit", 'i',
            "Maximum number of instances to test/train on  (-1 = no limit).",
            100000000, -1, Integer.MAX_VALUE);

    public IntOption timeLimitOption = new IntOption("timeLimit", 't',
            "Maximum number of seconds to test/train for (-1 = no limit).", -1,
            -1, Integer.MAX_VALUE);

    public IntOption sampleFrequencyOption = new IntOption("sampleFrequency",
            'f',
            "How many instances between samples of the learning performance.",
            100, 0, Integer.MAX_VALUE);

    public IntOption memCheckFrequencyOption = new IntOption(
            "memCheckFrequency", 'q',
            "How many instances between memory bound checks.", 1000000, 0,
            Integer.MAX_VALUE);

    public FileOption outputPredictionFileOption = new FileOption("outputPredictionFile", 'o',
            "File to append output predictions to.", null, "pred", true);

    //New for prequential method DEPRECATED
    public IntOption widthOption = new IntOption("width",
            'w', "Size of Window", 1000);

    public FloatOption alphaOption = new FloatOption("alpha",
            'a', "Fading factor or exponential smoothing factor", .01);
    //End New for prequential methods
    
    
    private Learner learner;
    private ExampleStream stream;
    private String streamPath;
    private String resultFolderPath;
    private int driftWidth;
    private BufferedReader driftReader;
    private BufferedWriter driftPeriodPerformanceWriter;
    private BufferedWriter criticalPointWriter;
    
    private int criticalCount;
    private int meanAccuracy;
    private int meanMemory;
    private int maxMemory;
    private double evaluateTime;
  
    
    public MyEvaluatePrequential(Learner learner, ExampleStream stream, String streamPath, String resultFolderPath, int driftWidth)
    {
    	this.learner = learner;
    	this.stream = stream;
    	this.streamPath = streamPath;
    	this.driftWidth = driftWidth;
    	this.resultFolderPath = resultFolderPath;
    	this.criticalCount = 0;
    	
    	try
		{
			driftReader = new BufferedReader(new FileReader(this.streamPath+"/driftDescription.csv"));
			driftPeriodPerformanceWriter = new BufferedWriter(new FileWriter(this.resultFolderPath+"/driftPeriodPerformance.csv"));
			criticalPointWriter = new BufferedWriter(new FileWriter(this.resultFolderPath+"/criticalPoints.csv"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
    }
    
	public void setLearner(Learner learner)
	{
		this.learner = learner;
	}
	
	public void setStream(ExampleStream stream)
	{
		this.stream = stream;
	}
    
    
    @Override
    public Class<?> getTaskResultType() {
        return LearningCurve.class;
    }
    
    @Override
    public Object doMainTask(TaskMonitor monitor, ObjectRepository repository) {
    	
    	WindowClassificationPerformanceEvaluator windowClassificationPerformanceEvaluator = new WindowClassificationPerformanceEvaluator();
//    	UnslidingWindowClassificationPerformanceEvaluator windowClassificationPerformanceEvaluator = new UnslidingWindowClassificationPerformanceEvaluator();
    	windowClassificationPerformanceEvaluator.widthOption.setValue(1000);
    	
//    	FadingFactorClassificationPerformanceEvaluator fadingFactorClassificationPerformanceEvaluator = new FadingFactorClassificationPerformanceEvaluator();
        LearningPerformanceEvaluator evaluator = windowClassificationPerformanceEvaluator;

        LearningCurve learningCurve = new LearningCurve(
                "learning evaluation instances");
        


        //New for prequential methods
        if (evaluator instanceof WindowClassificationPerformanceEvaluator) {
            //((WindowClassificationPerformanceEvaluator) evaluator).setWindowWidth(widthOption.getValue());
            if (widthOption.getValue() != 1000) {
                System.out.println("DEPRECATED! Use EvaluatePrequential -e (WindowClassificationPerformanceEvaluator -w " + widthOption.getValue() + ")");
                 return learningCurve;
            }
        }
        if (evaluator instanceof EWMAClassificationPerformanceEvaluator) {
            //((EWMAClassificationPerformanceEvaluator) evaluator).setalpha(alphaOption.getValue());
            if (alphaOption.getValue() != .01) {
                System.out.println("DEPRECATED! Use EvaluatePrequential -e (EWMAClassificationPerformanceEvaluator -a " + alphaOption.getValue() + ")");
                return learningCurve;
            }
        }
        if (evaluator instanceof FadingFactorClassificationPerformanceEvaluator) {
            //((FadingFactorClassificationPerformanceEvaluator) evaluator).setalpha(alphaOption.getValue());
            if (alphaOption.getValue() != .01) {
                System.out.println("DEPRECATED! Use EvaluatePrequential -e (FadingFactorClassificationPerformanceEvaluator -a " + alphaOption.getValue() + ")");
                return learningCurve;
            }
        }
        //End New for prequential methods

        learner.setModelContext(stream.getHeader());
        int maxInstances = this.instanceLimitOption.getValue();
        long instancesProcessed = 0;
        int maxSeconds = this.timeLimitOption.getValue();
        int secondsElapsed = 0;
        monitor.setCurrentActivity("Evaluating learner...", -1.0);

        File dumpFile = new File(this.resultFolderPath+"/dump.csv");
        PrintStream immediateResultStream = null;
        if (dumpFile != null) {
            try {
                if (dumpFile.exists()) {
                    immediateResultStream = new PrintStream(
                            new FileOutputStream(dumpFile, false), true);
                } else {
                    immediateResultStream = new PrintStream(
                            new FileOutputStream(dumpFile), true);
                }
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Unable to open immediate result file: " + dumpFile, ex);
            }
        }
        //File for output predictions
        File outputPredictionFile = this.outputPredictionFileOption.getFile();
        PrintStream outputPredictionResultStream = null;
        if (outputPredictionFile != null) {
            try {
                if (outputPredictionFile.exists()) {
                    outputPredictionResultStream = new PrintStream(
                            new FileOutputStream(outputPredictionFile, true), true);
                } else {
                    outputPredictionResultStream = new PrintStream(
                            new FileOutputStream(outputPredictionFile), true);
                }
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Unable to open prediction result file: " + outputPredictionFile, ex);
            }
        }
        boolean firstDump = true;
		boolean firstDriftPeriodWrite = true;
		boolean firstCriticalPointWrite = true;
        boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
        long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
        long totalTime = 0;
        long lastEvaluateStartTime = evaluateStartTime;
        double RAMHours = 0.0;
        
        int driftCentre = 0;
        try
		{
			driftReader.readLine();
			driftCentre =Integer.parseInt(driftReader.readLine());
		} catch (IOException e)
		{
			e.printStackTrace();
		}

        int numSamples = 0;
        int sumAcc = 0;
        int sumMemory = 0;
        
        while (stream.hasMoreInstances()
                && ((maxInstances < 0) || (instancesProcessed < maxInstances))
                && ((maxSeconds < 0) || (secondsElapsed < maxSeconds))) {
        	
        	if(instancesProcessed%50000==0) System.out.println("Thread#"+Thread.currentThread().getId()+": "+(float)instancesProcessed*100/(stream.estimatedRemainingInstances()+instancesProcessed)+"%");
        	
            Example trainInst = stream.nextInstance();
            Example testInst = (Example) trainInst; //.copy();
            //testInst.setClassMissing();
            double[] prediction = learner.getVotesForInstance(testInst);
            // Output prediction
            if (outputPredictionFile != null) {
                int trueClass = (int) ((Instance) trainInst.getData()).classValue();
                outputPredictionResultStream.println(Utils.maxIndex(prediction) + "," + (
                 ((Instance) testInst.getData()).classIsMissing() == true ? " ? " : trueClass));
            }

            //evaluator.addClassificationAttempt(trueClass, prediction, testInst.weight());
            evaluator.addResult(testInst, prediction)
            ;
            long trainStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
            learner.trainOnInstance(trainInst);
            totalTime += TimingUtils.getNanoCPUTimeOfCurrentThread() - trainStartTime;
            
            


            
            instancesProcessed++;
            if (instancesProcessed % this.sampleFrequencyOption.getValue() == 0
                    || stream.hasMoreInstances() == false) {
            	
            	numSamples++;
            	
                long evaluateTime = TimingUtils.getNanoCPUTimeOfCurrentThread();
//                double time = TimingUtils.nanoTimeToSeconds(evaluateTime - evaluateStartTime);
                double time = TimingUtils.nanoTimeToSeconds(totalTime);
                double timeIncrement = TimingUtils.nanoTimeToSeconds(evaluateTime - lastEvaluateStartTime);
                int learnerSize = learner.measureByteSize();
				double RAMHoursIncrement = learnerSize / (1024.0 * 1024.0 * 1024.0); //GBs
                RAMHoursIncrement *= (timeIncrement / 3600.0); //Hours
                RAMHours += RAMHoursIncrement;
                lastEvaluateStartTime = evaluateTime;
                learningCurve.insertEntry(new LearningEvaluation(
                        new Measurement[]{
                            new Measurement(
                            "learning evaluation instances",
                            instancesProcessed),
                            new Measurement(
                            "evaluation time ("
                            + (preciseCPUTiming ? "cpu "
                            : "") + "seconds)",
                            time),
                            new Measurement(
                            "model cost (RAM-Hours)",
                            RAMHours)
                        },
                        evaluator, learner));
                
                // update statistics
                this.evaluateTime = time;
                this.maxMemory = learnerSize > maxMemory? learnerSize : maxMemory;
                sumMemory += learnerSize;
                

                if (immediateResultStream != null) {
                    if (firstDump) {
                        immediateResultStream.println(learningCurve.headerToString());
                        firstDump = false;
                    }
                    immediateResultStream.println(learningCurve.entryToString(learningCurve.numEntries() - 1));
                    immediateResultStream.flush();
                }
                
                //collect accuracy
            	Measurement[] measurements = evaluator.getPerformanceMeasurements();
            	Measurement measurement = null;
            	for(int i=0; i<measurements.length;i++)
            	{
            		if(measurements[i].getName().equals("classifications correct (percent)"))
            		{
            			measurement = measurements[i];
            			 // update statistics
            			sumAcc += measurement.getValue();
            			break;
            		}
            		
            	}

            	if(measurement.getValue()<80)
            	{
            		try{
            			if(firstCriticalPointWrite)
            			{
            				criticalPointWriter.write(learningCurve.headerToString()+"\n");
            				firstCriticalPointWrite = false;
            			}
            			criticalPointWriter.write(learningCurve.entryToString(learningCurve.numEntries() - 1)+"\n");
            			
            			criticalCount++;
            		}
            		catch(IOException e)
            		{

            		}

            	}
                
                // output performance in drift environment
                if(instancesProcessed >= driftCentre && instancesProcessed <= driftCentre + driftWidth)
                {
                	
                	try
    				{

    					if(firstDriftPeriodWrite)
                		{
                			driftPeriodPerformanceWriter.write(learningCurve.headerToString()+"\n");
                			firstDriftPeriodWrite = false;
                		}
//    					driftPeriodPerformanceWriter.write(instancesProcessed+","+measurement.getValue()+"\n");
                		driftPeriodPerformanceWriter.write(learningCurve.entryToString(learningCurve.numEntries() - 1)+"\n");
    				} catch (IOException e)
    				{
    					e.printStackTrace();
    				}
                }
                String line = null;
                try
                {
                	if(instancesProcessed > driftCentre + driftWidth
                			&& (line = driftReader.readLine())!=null)
                	{
                		driftCentre =Integer.parseInt(line);
                	}
                } catch (IOException e)
                {
                	e.printStackTrace();
                }
                

              

                
            }
            if (instancesProcessed % INSTANCES_BETWEEN_MONITOR_UPDATES == 0) {
                if (monitor.taskShouldAbort()) {
                    return null;
                }
                long estimatedRemainingInstances = stream.estimatedRemainingInstances();
                if (maxInstances > 0) {
                    long maxRemaining = maxInstances - instancesProcessed;
                    if ((estimatedRemainingInstances < 0)
                            || (maxRemaining < estimatedRemainingInstances)) {
                        estimatedRemainingInstances = maxRemaining;
                    }
                }
                monitor.setCurrentActivityFractionComplete(estimatedRemainingInstances < 0 ? -1.0
                        : (double) instancesProcessed
                        / (double) (instancesProcessed + estimatedRemainingInstances));
                if (monitor.resultPreviewRequested()) {
                    monitor.setLatestResultPreview(learningCurve.copy());
                }
                secondsElapsed = (int) TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()
                        - evaluateStartTime);
            }
            

        }
        
        // do after training work
        learner.cleanup();
        meanAccuracy = sumAcc/numSamples;
        meanMemory = sumMemory/numSamples;
        
        // close
        try
		{
            driftPeriodPerformanceWriter.close();
			driftReader.close();
			criticalPointWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

        
        if (immediateResultStream != null) {
            immediateResultStream.close();
        }
        if (outputPredictionResultStream != null) {
            outputPredictionResultStream.close();
        }
        
        System.out.println("Thread#"+Thread.currentThread().getId()+": "+"Done.");
        
        return learningCurve;
        

    }

	public double getMeanAcc()
	{
		return meanAccuracy;
	}

	public double getMeanMemory()
	{
		return meanMemory;
	}

	public double getMaxMemory()
	{
		return maxMemory;
	}

	public double getTime()
	{
		return evaluateTime;
	}
	
	public int getCriticalCount()
	{
		return criticalCount;
	}
    
//	private void writeToFile(BufferedWriter bw, String str)
//	{
//		if (bw != null)
//		{
//			try
//			{
//				bw.write(str);
//			} catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}

}
