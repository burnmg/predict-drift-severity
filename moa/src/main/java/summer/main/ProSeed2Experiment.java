package summer.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.rosuda.JRI.Rengine;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Streams.DoubleStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;
import summer.proSeed.kylieExample.TextConsole;

public class ProSeed2Experiment
{
	public static void main(String[] args)
	{
		/*
		 * START Rengine
		 */
		String[] reArgs = new String[]{"--save"};
		Rengine re = new Rengine(reArgs, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		
		Pattern.setRengine(re);
		/*
		 * END Rengine
		 */
		
		
		run(0.02)
		
		re.end();
	}
	
	
	public static void run(double detectorConfidence, Pattern[] patterns, double[][] networkTransitions
			, Double[][] severityEdges) throws FileNotFoundException, IOException
	{
		/*
		 * START ProSeed Parameters 
		 */
		SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 0.1, 5, 1, 1, 0.01, 0.8, 75, 32, 200);
		ProSeed2 proSeed2 = new ProSeed2(3, 20, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 0, 2000);
		
		/*
		 * END ProSeed Parameters 
		 */

		/*
		 * START Network Stream Generator Parameters 
		 */
		int trials = 10;
		
		double networkNoise = 0;
		int stateTimeMean = 100;
		double networkNoiseStandardDeviation = 0;
		double patternNoiseFlag = 1;
		/*
		 * END Network Stream Generator Parameters 
		 */
	
		int seed = 1024;
		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, patterns, trials + seed, severityEdges); // Abrupt Volatility Change
		trainingNetworkStream.networkNoise = networkNoise; // percentage of transition noise
		trainingNetworkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		trainingNetworkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag

		// set the bernoulli stream (testing)
		// bernoulli.setNoise(0.0); // noise for error rate generator
		
		int streamLength = 100*trainingNetworkStream.getStateTimeMean();
		
		// set the bernoulli stream (training)
		DoubleStream trainingStream = new DoubleStream(1024, 0, 1, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		int instanceCount = 0;
		int driftCount = 0;
		boolean positveDirft = false;
		
		while(numBlocks < streamLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				
				boolean drift = proSeed2.setInput(output);
				boolean voldrift = proSeed2.getVolatilityDetector().getVolatilityDriftFound();
				if (drift) driftCount++;
				// if(voldrift) driftWriter.write(proSeed2.getVolatilityDetector().getCurrentBufferMean()+"\n");
				
				instanceCount++;
			}
			numBlocks++;
			
			if(positveDirft)
			{
				trainingStream.addDrift(trainingNetworkStream.getCurrentSeverity()); // create one drift
				positveDirft = false;
			}
			else
			{
				trainingStream.addDrift(-trainingNetworkStream.getCurrentSeverity()); // create one drift
				positveDirft = true;
			}
			
		}
		
	}
}

