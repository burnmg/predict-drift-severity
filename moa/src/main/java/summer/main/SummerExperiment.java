package summer.main;

import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Streams.DoubleStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;

public class SummerExperiment
{
	// detector setting TODO 
	CutPointDetector detector;
	double confidence; 
	
	// stream setting
	ProbabilisticNetworkStream networkstream;
	DoubleStream doubleStream;
	int trainingStreamLength;
	int testingStreamLength;

	
	public SummerExperiment(CutPointDetector detector, DoubleStream doubleStream,
			int trainingStreamLength, int testStreamLength, int seed, 
			double[][] networkTransitions, 	Pattern[] patterns, Double[][] severityEdges)
	{
		this.detector = detector;
		this.doubleStream = doubleStream;
		this.trainingStreamLength = trainingStreamLength;
		this.testingStreamLength = testStreamLength;
		
		// configure network stream 
		networkstream = new ProbabilisticNetworkStream(networkTransitions, patterns, seed, severityEdges); // Abrupt Volatility Change
		networkstream.networkNoise = 0; // percentage of transition noise
		networkstream.setStateTimeMean(100); // set volatility interval of stream
		networkstream.intervalNoise = 1; // patternNoiseFlag
		
	}
	
	public double[] run()
	{
		boolean positveDirft = false;
		
		// training
		long instanceCount = 0;
		long blockCount = 0;
		while(instanceCount<trainingStreamLength)
		{
			int blockLength = networkStream.generateNext();
			for(int i=0;i<blockLength;i++)
			{
				double value = doubleStream.generateNext();
				detector.setInput(value);
				
				instanceCount++;
				if(instanceCount>trainingStreamLength) break;
			}
			
			blockCount++; 
			
			if(positveDirft)
			{
				doubleStream.addDrift(networkStream.getCurrentSeverity()); // create one drift
				positveDirft = false;
			}
			else
			{
				doubleStream.addDrift(-networkStream.getCurrentSeverity()); // create one drift
				positveDirft = true;
			}
			if(blockCount%100==0) System.out.println("training:"+instanceCount+"/"+trainingStreamLength);
		}
		
		// testing
		instanceCount = 0;
		blockCount = 0;
		
		long numDetectedDrift = 0;
		long numTrueDrift = 0;
		long actualDriftPoint = -1;
		final int TRUE_POSITIVE_WINDOW_SIZE = 100;
		long numFalsePositive = 0;
		long delay = 0;
		
		while(instanceCount<testingStreamLength)
		{
			int blockLength = networkStream.generateNext();
			boolean detectedTrueDrift = false;
			for(int i=0;i<blockLength;i++)
			{
				double value = doubleStream.generateNext();
				boolean drift = detector.setInput(value);
				
				if (drift)
				{
					// if it is false drift
					if(actualDriftPoint!=-1 && instanceCount>actualDriftPoint+TRUE_POSITIVE_WINDOW_SIZE)
					{
						numFalsePositive++;
					}
					else if(!detectedTrueDrift)
					{
						delay += instanceCount - actualDriftPoint;
						numTrueDrift++;
						detectedTrueDrift = true;
					} 
					
					numDetectedDrift++;
				}
				
				instanceCount++;
				if(instanceCount>testingStreamLength) break;
			}
			blockCount++; 
			actualDriftPoint = instanceCount;
			
			// create one drift
			if(positveDirft)
			{
				doubleStream.addDrift(networkStream.getCurrentSeverity()); 
				positveDirft = false;
			}
			else
			{
				doubleStream.addDrift(-networkStream.getCurrentSeverity()); 
				positveDirft = true;
			}
			if(blockCount%100==0) System.out.println("training:"+instanceCount+"/"+testingStreamLength);
		}
		
		double fp = (double)numFalsePositive/testingStreamLength;
		double fn = ((double)numDetectedDrift-numTrueDrift)/testingStreamLength;
		double dl = (double)delay/numTrueDrift;
		
		// confidence, FP, FN, DL
		return new double[]{confidence, fp, fn, dl};
	}

}
