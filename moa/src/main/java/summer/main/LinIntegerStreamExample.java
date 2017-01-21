package summer.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Random;

import org.apache.commons.math3.analysis.function.Abs;
import org.rosuda.JRI.Rengine;

import com.google.common.collect.AsynchronousComputationException;

import cutpointdetection.ADWIN;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.BernoulliGenerator;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Network.ProbabilisticNetwork;
import summer.proSeed.PatternMining.Network.SeveritySamplingEdgeInterface;
import summer.proSeed.PatternMining.Streams.DoubleStream;
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
		SeedDetector VDSeedDetector =new SeedDetector(0.02, 0.1, 32, 1, 1, 0.01, 0.8, 75, 32, 200);
		ProSeed2 proSeed = new ProSeed2(3, 100, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 0, 1000);
		
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
		
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data.txt"));
		
		BufferedWriter driftWriter = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/drift.txt"));
	

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
		
		Pattern[] states = { new Pattern(1000, 100), new Pattern(2500, 100), new Pattern(3000, 100)};
		int seed = 1024;
		/*
		Double[][] severityEdges = {{null, new Double(100), new Double(200)}, 
									{new Double(300), null, new Double(400)}, 
									{new Double(500), new Double(600), null}
		};
		*/
		Double[][] severityEdges = {{null, new Double(1000), new Double(2000)}, 
				{new Double(3000), null, new Double(4000)}, 
				{new Double(5000), new Double(6000), null}
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
		DoubleStream trainingStream = new DoubleStream(1024, 0, 500, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		int instanceCount = 0;
		boolean positveDirft = false;
		
		while(numBlocks < streamLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				
				boolean drift = proSeed.setInput(output);
				boolean voldrift = proSeed.getVolatilityDetector().getVolatilityDriftFound();
				if (drift) writer.write(Math.abs(proSeed.getSeverity())+"\n");
				if(voldrift) driftWriter.write(proSeed.getVolatilityDetector().getCurrentBufferMean()+"\n");
				
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
		
		proSeed.mergeNetwork();
		int networkSize = proSeed.getNetwork().getNumberOfPatterns();
		SeveritySamplingEdgeInterface[][] edges = proSeed.getNetwork().getEdges();;
		for(int i=0;i<networkSize;i++)
		{
			for(int j=0;j<networkSize;j++)
			{
				System.out.print((float)edges[i][j].getMean() + "\t");
			}
			System.out.println();
		}
		
		
		String networkString = new ProbabilisticNetwork(proSeed.getPatternReservoir().getSortedNetwork()).getNetworkString();
		RelativeVolatilityDetector vold = proSeed.getVolatilityDetector();
		String actualNetworkString = new ProbabilisticNetwork(trainingNetworkStream.getActualNetwork().getNetwork()).getNetworkString();
		
		/*
		System.out.println("ProSeed Results:");
		System.out.println(networkString);
		System.out.print(proSeed.getPatternReservoir().getPatternsString());
		*/
		
		//System.out.println(actualNetworkString);
		//System.out.println(trainingNetworkStream.getStatesString());
		// test the result of edges
		
		/*
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
		*/
		writer.close();
		driftWriter.close();
		System.out.println("Done");
		re.end();
	}
}
