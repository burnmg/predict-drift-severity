package summer.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import summer.proSeed.DriftDetection.ADWINChangeDetector;
import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.PatternMining.Streams.DoubleStream;

public class ExperimentConfidenceNSeverity
{

	public static void main(String[] args) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rl/Desktop/Experiments/ADWIN5"));
		writer.write("confidence,severity,truePositiveRate,falsePositiveRate,falseNegativeRate,delayAvg\n");
		/*
		double truePositiveRate = (double)numTruePostive/streamLength;
		double falsePositiveRate  = (double)numFalsePositive/streamLength;
		double falseNegativeRate = (double)numFalseNagative/streamLength;
		double delayAvg = (double)delay/numTruePostive; 
		 */
		double con = 0.01;
		double endCon = 0.25;
		double stepCon = 0.01;
		

		
		while(con<endCon)
		{
			double sev = 0.15;
			double endSev = 0.2;
			double stepSev = 0.01;
			while(sev<endSev)
			{
				double[] res = run(sev, new ADWINChangeDetector(con));
				writer.write(String.format("%.8f,%.8f,%.8f,%.8f,%.8f,%.8f\n", con, sev, res[0], res[1], res[2], res[3]));
				sev += stepSev;
			}
			con += stepCon;
		}
		
		System.out.println("Done");
		writer.close();

	}

	public static double[] run(double severity, CutPointDetector detector) throws IOException
	{
		// File dir = new File("/Users/rl/Desktop/Experiments/"+id);
		// dir.mkdirs();
		
		DoubleStream stream = new DoubleStream(1992, 0, 1, 1);
		int streamLength = 1000000;
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
		
		double truePositiveRate = (double)numTruePostive;
		double falsePositiveRate  = (double)numFalsePositive;
		double falseNegativeRate = (double)numFalseNagative;
		double delayAvg = (double)delay/numTruePostive; 
			
		
		return new double[]{truePositiveRate, falsePositiveRate, falseNegativeRate, delayAvg};
	}
}
