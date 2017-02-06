package summer.main;

import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.Streams.DoubleStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;

public class SummerExperimentMain
{

	public static void main(String[] args)
	{
		double detectorConfidence = 0.1;
		SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 0.1, 5, 1, 1, 0.01, 0.8, 75, 32, 200);
		ProSeed2 proSeed2 = new ProSeed2(3, 80, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 10, 2000, false, 0.3);
		
		
		DoubleStream trainingStream = new DoubleStream(1024, 0, 1, 1);
		
		
		SummerExperiment e1 = new SummerExperiment(detector, networkStream, doubleStream, trainingStreamLength, testStreamLength)

	}

}
