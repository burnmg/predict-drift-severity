package summer.main;

import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.StreamGenerator;
import summer.proSeed.PatternMining.Streams.DoubleStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;

public class SummerExperiment
{
	// detector setting TODO 
	CutPointDetector detector;
	double confidence; 
	double detectorConfidence;
	
	// stream setting
	ProbabilisticNetworkStream networkStream;
	StreamGenerator datastream;
	long trainingStreamBlockLength;
	long testingStreamBLockLength;

	
	public SummerExperiment(CutPointDetector detector, double detectorConfidence, StreamGenerator datastream,
			long trainingStreamLength, long testStreamLength, int seed, 
			double[][] networkTransitions, 	Pattern[] patterns, Double[][] severityEdges)
	{
		this.detector = detector;
		this.datastream = datastream;
		this.trainingStreamBlockLength = trainingStreamLength;
		this.testingStreamBLockLength = testStreamLength;
		this.detectorConfidence = detectorConfidence;
		
		// configure network stream 
		networkStream = new ProbabilisticNetworkStream(networkTransitions, patterns, seed, severityEdges); // Abrupt Volatility Change
		networkStream.networkNoise = 0; // percentage of transition noise
		networkStream.setStateTimeMean(100); // set volatility interval of stream
		networkStream.intervalNoise = 1; // patternNoiseFlag
		
	}
	
	public double[] run()
	{
		boolean positveDirft = false;
		
		// training
		long instanceCount = 0;
		long blockCount = 0;
		while(blockCount<trainingStreamBlockLength)
		{
			int blockLength = networkStream.generateNext();
			for(int i=0;i<blockLength;i++)
			{
				double value = datastream.generateNext();
				detector.setInput(value);
				
				instanceCount++;
				if(instanceCount>trainingStreamBlockLength) break;
			}
			
			blockCount++; 
			
			if(positveDirft)
			{
				datastream.addDrift(networkStream.getCurrentSeverity()); // create one drift
				positveDirft = false;
			}
			else
			{
				datastream.addDrift(-networkStream.getCurrentSeverity()); // create one drift
				positveDirft = true;
			}
			if(blockCount%100==0) System.out.println("training:"+instanceCount+"/"+trainingStreamBlockLength);
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
		
		while(instanceCount<testingStreamBLockLength)
		{
			int blockLength = networkStream.generateNext();
			boolean detectedTrueDrift = false;
			for(int i=0;i<blockLength;i++)
			{
				double value = datastream.generateNext();
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
				if(instanceCount>testingStreamBLockLength) break;
			}
			blockCount++; 
			actualDriftPoint = instanceCount;
			
			// create one drift
			if(positveDirft)
			{
				datastream.addDrift(networkStream.getCurrentSeverity()); 
				positveDirft = false;
			}
			else
			{
				datastream.addDrift(-networkStream.getCurrentSeverity()); 
				positveDirft = true;
			}
			if(blockCount%100==0) System.out.println("training:"+instanceCount+"/"+testingStreamBLockLength);
		}
		
		double fp = (double)numFalsePositive/testingStreamBLockLength;
		double fn = ((double)numDetectedDrift-numTrueDrift)/testingStreamBLockLength;
		double dl = (double)delay/numTrueDrift;
		
		// confidence, FP, FN, DL
		return new double[]{confidence, fp, fn, dl};
	}

}
