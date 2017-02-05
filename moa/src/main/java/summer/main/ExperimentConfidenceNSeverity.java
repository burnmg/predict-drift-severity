package summer.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.PatternMining.Streams.DoubleStream;

public class ExperimentConfidenceNSeverity
{

	public static void main(String[] args) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rl/Desktop/Experiments/ADWIN_confidenceV"));
		/*
		double truePositiveRate = (double)numTruePostive/streamLength;
		double falsePositiveRate  = (double)numFalsePositive/streamLength;
		double falseNegativeRate = (double)numFalseNagative/streamLength;
		double delayAvg = (double)delay/numTruePostive; 
		 */
		double con = 0.01;
		double endCon = 0.25;
		double stepCon = 0.04;
		
		double sev = 0.1;
		double endSev = 1;
		double stepSev = 0.1;
		
		while(con<endCon)
		{
			while(sev<endSev)
			{
				
			}
		}
		
		writer.write("confidence,severity,truePositiveRate,falsePositiveRate,falseNegativeRate,delayAvg");
		

	}

	public static double[] run(double severity, CutPointDetector detector, String id) throws IOException
	{
		// File dir = new File("/Users/rl/Desktop/Experiments/"+id);
		// dir.mkdirs();
		

		
		
		DoubleStream stream = new DoubleStream(1992, 0, 1, 1);
		int streamLength = 100000;
		int numDrift = 100;
		int driftInterval = streamLength/numDrift;
		int TRUE_POSITIVE_WINDOW_SIZE = 1000;
		int actualDriftPoint = -1;
		int numFalsePositive = 0;
		int numTruePostive = 0;
		int numDetectedDrift = 0;
		int delay = 0;
	
		boolean detectedTruePostive = false;
		for(int i=0;i<streamLength;i++)
		{
			double value = stream.generateNext();
			boolean drift = detector.setInput(value);
			
			// experiment with false postive and delay
			
			if (drift)
			{
				// if it is false drift
				if(actualDriftPoint!=-1 && i>actualDriftPoint+TRUE_POSITIVE_WINDOW_SIZE)
				{
					numFalsePositive++;
				}
				else
				{
					if(!detectedTruePostive)
					{
						numTruePostive++;
						delay += i - actualDriftPoint;
						detectedTruePostive = true;
					}
										
				}
				
				numDetectedDrift++;
			}
			
			if(i%driftInterval==0)
			{
				actualDriftPoint = i;
				detectedTruePostive = false;
				stream.addDrift(severity);
			}
		}
		
		int numFalseNagative = numDrift - numTruePostive; 
		
		double truePositiveRate = (double)numTruePostive/streamLength;
		double falsePositiveRate  = (double)numFalsePositive/streamLength;
		double falseNegativeRate = (double)numFalseNagative/streamLength;
		double delayAvg = (double)delay/numTruePostive; 
			
		
		return new double[]{truePositiveRate, falsePositiveRate, falseNegativeRate, delayAvg};
	}
}
