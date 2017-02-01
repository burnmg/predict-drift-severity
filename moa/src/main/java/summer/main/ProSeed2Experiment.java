package summer.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;

import org.rosuda.JRI.Rengine;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Streams.DoubleStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;
import summer.proSeed.kylieExample.TextConsole;
import testers.FalsePositiveTester;

public class ProSeed2Experiment
{
	public static void main(String[] args) throws FileNotFoundException, IOException
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
		Pattern[] states = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		Double[][] severityEdges = {{null, new Double(0.5), new Double(1)}, 
				{new Double(1.5), null, new Double(2)}, 
				{new Double(3), new Double(4), null}
		};
		run(0.01,states, networkTransitions, severityEdges);
		run(0.1,states, networkTransitions, severityEdges);
		run(0.25,states, networkTransitions, severityEdges);
		run(0.3,states, networkTransitions, severityEdges);
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
		
		BufferedWriter dataWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/data.txt"));
		BufferedWriter truedriftWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/truedrift.txt"));
		BufferedWriter falsedriftWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/falsedrift.txt"));
		
		int seed = 1024;
		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, patterns, trials + seed, severityEdges); // Abrupt Volatility Change
		trainingNetworkStream.networkNoise = networkNoise; // percentage of transition noise
		trainingNetworkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		trainingNetworkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag

		// set the bernoulli stream (testing)
		// bernoulli.setNoise(0.0); // noise for error rate generator
		
		// int streamLength = 100*trainingNetworkStream.getStateTimeMean();
		int streamLength = 5;
		
		// set the bernoulli stream (training)
		DoubleStream trainingStream = new DoubleStream(1024, 0, 1, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		int instanceCount = 0;
		int driftCount = 0;
		boolean positveDirft = false;
		
		/*
		 * START variables for test 
		 */
		
		int lastActualDriftPoint = -1;
		final int TRUE_POSITIVE_WINDOW_SIZE = 100;
		int numFalsePositive = 0;
		double fpRate = 0;
		
		/*
		 * END variables for test
		 */
		
		while(numBlocks < streamLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				
				boolean drift = proSeed2.setInput(output);
				boolean voldrift = proSeed2.getVolatilityDetector().getVolatilityDriftFound();
				if (drift)
				{
					if(lastActualDriftPoint!=-1 && instanceCount>lastActualDriftPoint+TRUE_POSITIVE_WINDOW_SIZE)
					{
						numFalsePositive++;
						falsedriftWriter.write(instanceCount+"\n");
					}
					else
					{
						truedriftWriter.write(instanceCount+"\n");
					}
				}
				dataWriter.write(output+"\n");
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
			lastActualDriftPoint = instanceCount;
		}
		
		
		
		dataWriter.close();
		falsedriftWriter.close();
		truedriftWriter.close();
	}
}

