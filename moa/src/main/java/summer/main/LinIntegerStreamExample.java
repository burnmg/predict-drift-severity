package summer.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.rosuda.JRI.Rengine;

import summer.proSeed.DriftDetection.ProSeed;
import summer.proSeed.PatternMining.BernoulliGenerator;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Network.SeveritySamplingEdgeInterface;
import summer.proSeed.PatternMining.Streams.IntegerStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;
import summer.proSeed.VolatilityDetection.RelativeVolatilityDetector;
import summer.proSeed.kylieExample.TextConsole;

public class LinIntegerStreamExample
{

	public static void main(String args[]) throws FileNotFoundException, IOException
	{
		/*
		 * START ProSeed Parameters 
		 */
		summer.originalSeed.SeedDetector VDSeedDetector =new summer.originalSeed.SeedDetector(0.5, 32, 1, 1, 0.01, 0.8, 75);
		ProSeed proSeed = new ProSeed(3, 100, 0.05, 100, 
				VDSeedDetector, 32, 0.5, 0);
		
		/*
		 * END ProSeed Parameters 
		 */
		
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
		
		BufferedWriter streamNetworkWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data2/streamNetworkWriter.txt"));
		BufferedWriter streamPatternWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data2/streamPatternWriter.txt"));
		BufferedWriter detectorSortedNetworkWriter = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data2/detectorSortedNetworkWriter.txt"));
		BufferedWriter detectorSortedPatternWriter = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data2/detectorSortedPatternWriter.txt"));
	

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
		
		/*
		// TODO set network edges and fromIndex
		ProbabilisticNetworkStream networkStream = new ProbabilisticNetworkStream(networkTransitions, states, seed, null, 0); // Abrupt Volatility Change
		networkStream.networkNoise = networkNoise; // percentage of transition noise
		networkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		networkStream.noiseStandardDeviation = networkNoiseStandardDeviation;// pattern noise 
		networkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag
		*/
		
		// set the network stream (testing)
		// TODO set network edges and fromIndex
		
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		
		Pattern[] states = { new Pattern(100, 100), new Pattern(200, 100), new Pattern(300, 100)};
		int seed = 1024;
		Double[][] severityEdges = {{null, new Double(10), new Double(20)}, 
									{new Double(30), null, new Double(40)}, 
									{new Double(50), new Double(60), null}
		};
		
		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, states, trials + seed, severityEdges); // Abrupt Volatility Change
		trainingNetworkStream.networkNoise = networkNoise; // percentage of transition noise
		trainingNetworkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		trainingNetworkStream.noiseStandardDeviation = networkNoiseStandardDeviation;// pattern noise 
		trainingNetworkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag

		// set the bernoulli stream (testing)
		// bernoulli.setNoise(0.0); // noise for error rate generator
		
		int streamLength = 100*trainingNetworkStream.getStateTimeMean();
		
		// set the bernoulli stream (training)
		IntegerStream trainingStream = new IntegerStream(1024, 100, 1, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		while(numBlocks < streamLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double bernoulliOutput = trainingStream.generateNext();
				proSeed.setTraining(bernoulliOutput);
				// System.out.println(bernoulliOutput);
				
				
			}
			numBlocks++;
			trainingStream.addDrift(trainingNetworkStream.getCurrentSeverity()); // create one drift
		}
		proSeed.mergeNetwork();
		String network = proSeed.getNetwork().getNetworkString();
		RelativeVolatilityDetector vold = proSeed.getVolatilityDetector();
		String networkString = trainingNetworkStream.getActualNetwork().getNetworkString();
		
		// test the result of edges
		SeveritySamplingEdgeInterface[][] edges = vold.getDriftPrediction().getNetworkEdges();
		
		
		for(int i=0;i<edges.length;i++)
		{
			for(int j=0;j<edges[0].length;j++)
			{
				if(edges[i][j]!=null)
				{
					System.out.println(edges[i][j].getSamples()[0]);
				}
			}
		}
		
		
		System.out.println("Done");

	}
}
