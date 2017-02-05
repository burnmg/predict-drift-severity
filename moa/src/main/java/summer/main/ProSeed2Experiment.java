package summer.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.Buffer;

import org.rosuda.JRI.Rengine;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.IndexedPattern;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Network.SeveritySamplingEdgeInterface;
import summer.proSeed.PatternMining.Streams.DoubleStream;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;
import summer.proSeed.VolatilityDetection.DriftPrediction;
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
		
		//put the experiment code here
		
		Pattern[] states = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		
		Double[][] severityEdges = {{null, new Double(0.5), new Double(1)}, 
				{new Double(1.5), null, new Double(2)}, 
				{new Double(3), new Double(4), null}
		};
		
		
		/*
		Double[][] severityEdges = {{null, new Double(8), new Double(4)}, 
				{new Double(2), null, new Double(1)}, 
				{new Double(0.5), new Double(0.25), null}
		};
		*/
		
		
		/*
		double[][] networkTransitions = {{0,1}, {1,0}};
		Pattern[] states = { new Pattern(1000, 100), new Pattern(2000, 100)};
		Double[][] severityEdges = {{null, new Double(8)},
				{new Double(2), null}};
		*/
		
		double[] res = run(1992,0.01 ,states, networkTransitions, severityEdges, true, 100);
		re.end();
	}
	
	public static void testRelationshipOfDelayAndConfidence() throws IOException
	{
		Pattern[] states = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		Double[][] severityEdges = {{null, new Double(0.5), new Double(1)}, 
				{new Double(1.5), null, new Double(2)}, 
				{new Double(3), new Double(4), null}
		};
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rl/Desktop/res.txt")); 
		
		
		double confidence = 0.01;
		while(confidence<0.5)
		{
			double[] res = run(53141,confidence ,states, networkTransitions, severityEdges, true, 50);
			writer.write(res[0]+","+res[1]+","+res[2]+"\n");
			confidence += 0.01;
		}
		
		writer.close();
	}
	
	
	/**
	 * The method of experiment (IMPORTANT)
	 * @param detectorConfidence
	 * @param patterns
	 * @param networkTransitions
	 * @param actualSeverityEdges
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static double[] run(int seed, double detectorConfidence, Pattern[] patterns, double[][] networkTransitions
			, Double[][] actualSeverityEdges, boolean useSimpleRiskMethod, int streamLength) throws FileNotFoundException, IOException
	{
		/*
		 * START ProSeed Parameters 
		 */
		SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 0.1, 5, 1, 1, 0.01, 0.8, 75, 32, 200);
		ProSeed2 proSeed2 = new ProSeed2(3, 80, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 10, 2000, useSimpleRiskMethod);
		
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
		BufferedWriter edgeWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/sortedEdge.txt"));
		BufferedWriter actualEdgeWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/actualsortedEdge.txt"));
		BufferedWriter riskWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/risk.txt"));
		BufferedWriter patternWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/patterns.txt"));
		
		int printRate = 100;

		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, patterns, trials + seed, actualSeverityEdges); // Abrupt Volatility Change
		trainingNetworkStream.networkNoise = networkNoise; // percentage of transition noise
		trainingNetworkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		trainingNetworkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag
		
		// set the bernoulli stream (training)
		DoubleStream trainingStream = new DoubleStream(1024, 0, 1, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		int instanceCount = 0;
		int driftCount = 0;
		boolean positveDirft = false;
		

		int trainingStreamLength = streamLength*trainingNetworkStream.getStateTimeMean();
		// Trianing Phase
		while(numBlocks < trainingStreamLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				
				boolean drift = proSeed2.setInputWithTraining(output);
				boolean voldrift = proSeed2.getVolatilityDetector().getVolatilityDriftFound();
				
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
			// actualDriftPoint = instanceCount;
			
			if(numBlocks%printRate==0) System.out.println("Training:"+numBlocks+"/"+trainingStreamLength);
		}
		
		/*
		 * S Some Print Test
		 */
		SeveritySamplingEdgeInterface[][] edges = proSeed2.getVolatilityDetector().getPredictor().getPatternReservoir().getSortEdges();
		double[][] network = proSeed2.getVolatilityDetector().getPredictor().getPatternReservoir().getSortedNetwork();
		
		for(int i=0;i<edges.length;i++)
		{
			for(int j=0;j<edges[0].length;j++)
			{
				if(i!=j && network[i][j]>0.0001)System.out.printf("%30f:%f\n", (float)network[i][j], (float)edges[i][j].getMean());
				edgeWriter.write((float)network[i][j]+","+(float)edges[i][j].getMean()+"\n");
			}
		}
		
		/*
		 * E Some Print Test
		 */
		
		
		/*
		 * START variables for test 
		 */
		int numDetectedDrift = 0;
		int numTrueDrift = 0;
		
		int actualDriftPoint = -1;
		final int TRUE_POSITIVE_WINDOW_SIZE = 100;
		int numFalsePositive = 0;
		double fpRate = 0;
		
		int delay = 0;
		/*
		 * END variables for test
		 */
		
		// Testing Phase
		numBlocks = 0;
		instanceCount = 0;

		int testingStreamLength = (int)1*trainingStreamLength;
		while(numBlocks < testingStreamLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				
				boolean drift = proSeed2.setInput(output);
				boolean voldrift = proSeed2.getVolatilityDetector().getVolatilityDriftFound();
				
				// experiment with false postive and delay
				if (drift)
				{
					// if it is false drift
					if(actualDriftPoint!=-1 && instanceCount>actualDriftPoint+TRUE_POSITIVE_WINDOW_SIZE)
					{
						numFalsePositive++;
						falsedriftWriter.write(instanceCount+"\n");
					}
					else
					{
						truedriftWriter.write(instanceCount+"\n");
						delay += instanceCount - actualDriftPoint;
						numTrueDrift++;
					}
					
					numDetectedDrift++;
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
			actualDriftPoint = instanceCount;
			
			if(numBlocks%(printRate)==0) 
			{
				System.out.println("Testing:"+numBlocks+"/"+testingStreamLength);
			}
			
			DriftPrediction predictor = proSeed2.getVolatilityDetector().getPredictor();
			riskWriter.write(Math.abs(trainingNetworkStream.getCurrentSeverity())+","+predictor.getCurrentRisk()+"\n");
		}
		/*
		// write generated edges
		int networkSize = proSeed2.getNetwork().getNumberOfPatterns();
		SeveritySamplingEdgeInterface[][] generatedEdges = proSeed2.getPatternReservoir().getSortEdges();
		
		for(int i=0;i<networkSize;i++)
		{
			for(int j=0;j<networkSize;j++)
			{
				double mean = (float)generatedEdges[i][j].getMean();
				if(mean<100000000) edgeWriter.write(mean+"\n");
			}
		}
		
		// write actual edges
		for(int i=0;i<actualSeverityEdges.length;i++)
		{
			for(int j=0;j<actualSeverityEdges[0].length;j++)
			{
				
				if(actualSeverityEdges[i][j]!=null)
				{
					double mean = actualSeverityEdges[i][j];
					if(mean<100000000) actualEdgeWriter.write(mean+"\n");
				}

			}
		}
		*/
		
		IndexedPattern[] indexedPatterns = proSeed2.getVolatilityDetector().getPredictor().getPatternReservoir().getSortedPatterns();
		String sortedPatternsString = proSeed2.getVolatilityDetector().getPredictor().getPatternReservoir().getPatternStringOf(indexedPatterns);
		patternWriter.write(sortedPatternsString);
		
		dataWriter.close();
		falsedriftWriter.close();
		truedriftWriter.close();
		edgeWriter.close();
		actualEdgeWriter.close();
		riskWriter.close();
		patternWriter.close();
		
		return new double[]{detectorConfidence, (double)numFalsePositive/instanceCount, (double)delay/numTrueDrift};
	}
}

