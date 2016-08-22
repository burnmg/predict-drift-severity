package a.evaluator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import a.tools.Directory;

import moa.classifiers.AbstractClassifier;
import moa.classifiers.a.VolatilityAdaptiveClassifer;
import moa.streams.ExampleStream;
import moa.tasks.StandardTaskMonitor;

public class EvaluateTask implements Callable<Integer>
{
	private AbstractClassifier classifier;
	private String resultFolderPath;
	private MyEvaluatePrequential evaluatePrequential;
	private String streamName;


	//	public EvaluateAlgorithmTask(String streamName, String resultFolderPath)
	//	{
	//		this.streamName = streamName;
	//		this.resultFolderPath = resultFolderPath;
	//	}

	public EvaluateTask(AbstractClassifier classifier, String streamName , String sampleResultFolderPath)
	{
		this.classifier = classifier;
		ExampleStream stream = Directory.getStreamFromFileByName(streamName);
		this.streamName = streamName;
		this.resultFolderPath = sampleResultFolderPath;

		evaluatePrequential = new MyEvaluatePrequential(this.classifier, stream, Directory.streamsPath+'/'+streamName, this.resultFolderPath, 20000);
		evaluatePrequential.getOptions().resetToDefaults();
		evaluatePrequential.sampleFrequencyOption.setValue(100);

	}


	@Override
	public Integer call()
	{
		
		try{
			
			evaluatePrequential.doMainTask(new StandardTaskMonitor(), null);
			double[] coverageEvaluateResults = evaluateVolIntervalCoverage();
			double overallCovergage =1;
			double highCoverage = -1;
			double lowCoverage = -1;
			double simpleEvaluateCoverage = -1;

			if(coverageEvaluateResults!=null)
			{
				overallCovergage = coverageEvaluateResults[0];
				lowCoverage = coverageEvaluateResults[1];
				highCoverage = coverageEvaluateResults[2];
				simpleEvaluateCoverage = coverageEvaluateResults[3];
			}
			
			double[] lowModeAndHighModePercentage = lowModeAndHighModePercentage();
			
			double lowModePercentage = -1;
			double highModePercentage = -1;
			
			if(lowModeAndHighModePercentage!=null)
			{
				lowModePercentage = lowModeAndHighModePercentage[0];
				highModePercentage = lowModeAndHighModePercentage[1];
			}

			double meanAcc = evaluatePrequential.getMeanAcc();
			double meanMemory = evaluatePrequential.getMeanMemory();
			double maxMemory = evaluatePrequential.getMaxMemory();
			double time = evaluatePrequential.getTime();
			double meanAccInDrifts = evaluatePrequential.getMeanAccInDrifts();
			int criticalPointCount = evaluatePrequential.getCriticalCount();
			
			// count numbers of switch points. 
			String switchPointDescFile = this.resultFolderPath+"/switchPointDesc.csv";
			int numSwitchPoints = -1;
			if(new File(switchPointDescFile).exists())
			{
				numSwitchPoints = countLines(switchPointDescFile) - 1;
			}
			

			// output the result to summary file. 
			try
			{
				BufferedWriter writer = new BufferedWriter(new FileWriter(this.resultFolderPath+"/summary.txt", false));
				writer.write("mean accuracy:"+meanAcc);
				writer.newLine();
				writer.write("mean accuracy in drift periods:"+meanAccInDrifts);
				writer.newLine();
				writer.write("mean memory:"+meanMemory);
				writer.newLine();
				writer.write("max memory:"+maxMemory);
				writer.newLine();
				writer.write("time:"+time);
				writer.newLine();
				writer.write("Critical Point Counts:"+criticalPointCount);
				writer.newLine();
				writer.write("Overall Correct Coverage:"+overallCovergage);
				writer.newLine();
				writer.write("Simple Evaluate:"+simpleEvaluateCoverage);
				writer.newLine();
				writer.write("Low Correct Coverage:"+lowCoverage);
				writer.newLine();
				writer.write("High Correct Coverage:"+highCoverage);
				writer.newLine();
				writer.write("Low Vol Mode Percentage:"+lowModePercentage);
				writer.newLine();
				writer.write("High Vol Mode Percentage:"+highModePercentage);
				writer.newLine();
				writer.write("Number of Switch Points:"+numSwitchPoints);
				writer.newLine();
				
				
				writer.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		return 0;
	}
	/**
	 * 
	 * @return [0] percentage of low. [1] percentage of high
	 */
	public double[] lowModeAndHighModePercentage()
	{
		if(!this.classifier.getClass().isAssignableFrom(VolatilityAdaptiveClassifer.class)) return null;
		
		int streamLength = 0;
		int sumLowVol = 0;
		int sumHighVol = 0;
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(this.resultFolderPath+"/"+"volSwitchIntervalDesc.csv"));
			String line = null;
			reader.readLine();
			
			while((line = reader.readLine())!=null)
			{
				String[] strs = line.split(",");
				int start = Integer.parseInt(strs[0]);
				int end = Integer.parseInt(strs[1]);
				int mode = Integer.parseInt(strs[2]);
				
				if(mode==1)
				{
					sumLowVol += end-start+1;
				}
				else
				{
					sumHighVol += end-start+1;
				}
			}
			streamLength = sumLowVol + sumHighVol;
			
			reader.close();
			
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return new double[]{(double)sumLowVol/streamLength, (double)sumHighVol/streamLength};
	}
	
	/**
	 * @return [0] overall measure [1] low measures [2] high measure
	 */ 
	public double[] evaluateVolIntervalCoverage()
	{
		if(!this.classifier.getClass().isAssignableFrom(VolatilityAdaptiveClassifer.class)) return null;

		//	evaluate the volatility interval coverage
		BufferedReader readActual;
		BufferedReader readExpected;
		int[][] actual = null;
		int[][] expected = null;
		try
		{
			//load actual
			readActual = new BufferedReader(new FileReader(this.resultFolderPath+"/"+"volSwitchIntervalDesc.csv"));
			actual = load2DArray(readActual);

			//load expected
			readExpected = new BufferedReader(new FileReader(
					new File(Directory.streamsPath +"/"+this.streamName+"/"+"volExpectedIntervalDescription.csv")));
			expected = load2DArray(readExpected);

			readActual.close();
			readExpected.close();

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		CorreteModeCoverageEvaluator evaluateCorreteModeCoverage = new CorreteModeCoverageEvaluator();
		double result1 = evaluateCorreteModeCoverage.evalutate(expected, actual);
		double[] result23 = evaluateCorreteModeCoverage.evalutateLowAndHigh(expected, actual);
		double simpleEvaluateResult = evaluateCorreteModeCoverage.simpleEvalutate(expected, actual);

		return new double[]{result1, result23[0], result23[1], simpleEvaluateResult};
	}

	private int[][] load2DArray(BufferedReader br) throws IOException
	{
		String test = br.readLine();

		String line = null;
		ArrayList<int[]> list = new ArrayList<int[]>();
		while((line = br.readLine())!=null)
		{
			String[] es = line.split(",");
			int[] a = new int[3];
			for(int i=0;i<es.length;i++)
			{
				a[i] = Integer.parseInt(es[i]);
			}
			list.add(a);
		}
		int[][] result = new int[list.size()][list.get(0).length];
		for(int i=0;i<result.length;i++)
		{
			result[i] = list.get(i);

		}
		return result;
	}
	
	private int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}


}
