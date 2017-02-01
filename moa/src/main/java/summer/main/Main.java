
package summer.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.rosuda.JRI.Rengine;

import summer.magSeed.MagSeed;
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

public class Main
{

	public static void main(String[] args) throws IOException
	{


		String[] reArgs = new String[]{"--save"};
		Rengine re = new Rengine(reArgs, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		Pattern.setRengine(re);
		
		// Set R Engine END 
		confidenceAndNumberOfDriftTest(0.01);
		confidenceAndNumberOfDriftTest(0.05);
		confidenceAndNumberOfDriftTest(0.1);
		
		re.end();
	}
	
	public static void confidenceAndNumberOfDriftTest(double confidence) throws IOException
	{

		/*
		 * START ProSeed Parameters 
		 */
		SeedDetector VDSeedDetector = new SeedDetector(confidence, 0.1, 5, 1, 1, 0.01, 0.8, 75, 32, 200);
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
		
		Pattern[] states = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		int seed = 1024;
		
		/*
		Double[][] severityEdges = {{null, new Double(100), new Double(200)}, 
									{new Double(300), null, new Double(400)}, 
									{new Double(500), new Double(800), null}
		};
		*/
		/*
		Double[][] severityEdges = {{null, new Double(50), new Double(100)}, 
				{new Double(150), null, new Double(250)}, 
				{new Double(300), new Double(350), null}
		};
		*/
		Double[][] severityEdges = {{null, new Double(1), new Double(2)}, 
				{new Double(3), null, new Double(4)}, 
				{new Double(5), new Double(6), null}
		};
		
		
		/*
		Double[][] severityEdges = {{null, new Double(100), new Double(500)}, 
				{new Double(2500), null, new Double(5000)}, 
				{new Double(8000), new Double(9000), null}
				};
		*/
		/*
		Double[][] severityEdges = {{null, new Double(100), new Double(500)}, 
				{new Double(2500), null, new Double(5000)}, 
				{new Double(8000), new Double(9000), null}
				};
		*/
		/*
		Double[][] severityEdges = {{null, new Double(100), new Double(1000)}, 
				{new Double(10000), null, new Double(50000)}, 
				{new Double(100000), new Double(200000), null}
				};
		*/
		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, states, trials + seed, severityEdges); // Abrupt Volatility Change
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
				
				boolean drift = proSeed2.setInputWithTraining(output);
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
		
		System.out.println(confidence+":"+driftCount);
		System.out.println("Done");

	}
	public static void testProSeedWithIntegerStream() throws FileNotFoundException, IOException
	{
		// 0.01 0.02 0.1
		SeedDetector VDSeedDetector =new SeedDetector(0.1, 0.1, 32, 1, 1, 0.01, 0.8, 75, 32, 200);
		ProSeed2 proSeed = new ProSeed2(3, 100, 0.05, 100, 
				VDSeedDetector, 32, 0.02, 0, 100);
		
		DoubleStream s = new DoubleStream(3213213, 0, 100, 1);
		
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data.txt"));
		
		double severity = 100;
		int detectedDrift = 0;
		int actualDrift = 0;
		int dataLength = 200000;
		int driftNum = 10;
		
		for(int i=0;i<dataLength;i++)
		{
			double data = s.generateNext();
			boolean drift = proSeed.setInputWithTraining(data);
			boolean volDrift = proSeed.getVolatilityDetector().getVolatilityDriftFound();
			if(drift)
			{
				// System.out.println(i+" severity:"+proSeed.getSeverity());
				System.out.println(proSeed.getVolatilityDetector().getCurrentBufferMean());
				writer.write(proSeed.getSeverity()+"\n");
				detectedDrift++;
			}
			
			if(i%(dataLength/(driftNum+1))==0)
			{
				actualDrift++;
				s.addDrift(severity);
				severity += 1;
				
			}
			
		}
		System.out.println("detected drift:"+detectedDrift);
		System.out.println("actual drift:"+actualDrift);

		
		writer.close();
	
		
	}
	public static void testIntegerStreamWithNetwork2Patterns() throws IOException
	{


		/*
		 * START ProSeed Parameters 
		 */
		
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
	

		BufferedWriter dataWrtier = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data1.txt"));
		
		/*
		 * START Network Stream Generator Parameters 
		 */

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
		int trials = 10;
		double networkNoise = 0;
		int stateTimeMean = 20;
		double networkNoiseStandardDeviation = 0;
		double patternNoiseFlag = 1;
		double[][] networkTransitions = { {0, 1}, {1, 0}};
		
		Pattern[] states = { new Pattern(100, 100), new Pattern(600, 100)};
		int seed = 1024;
		Double[][] severityEdges = {{null, new Double(36)}, {new Double(72), null}};
		
		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, states, trials + seed, severityEdges); // Abrupt Volatility Change
		trainingNetworkStream.networkNoise = networkNoise; // percentage of transition noise
		trainingNetworkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		trainingNetworkStream.noiseStandardDeviation = networkNoiseStandardDeviation;// pattern noise 
		trainingNetworkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag

		// set the bernoulli stream (testing)
		// bernoulli.setNoise(0.0); // noise for error rate generator
		
		int blockLength = 1000;
		
		// set the bernoulli stream (training)
		DoubleStream trainingStream = new DoubleStream(1024, 100, 1, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		while(numBlocks < blockLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				dataWrtier.write(output+"");
				dataWrtier.newLine();
				
			}
			numBlocks++;
			trainingStream.addDrift(trainingNetworkStream.getCurrentSeverity()); // create one drift
		}
		String networkString = trainingNetworkStream.getActualNetwork().getNetworkString();

		
		dataWrtier.close();
		System.out.println("Done");
	}
	public static void testIntegerStreamWithNetwork() throws FileNotFoundException, IOException
	{

		/*
		 * START ProSeed Parameters 
		 */
		
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
	

		BufferedWriter dataWrtier = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data1.txt"));
		
		/*
		 * START Network Stream Generator Parameters 
		 */

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
		int trials = 10;
		double networkNoise = 0;
		int stateTimeMean = 20;
		double networkNoiseStandardDeviation = 0;
		double patternNoiseFlag = 1;
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		
		Pattern[] states = { new Pattern(100, 100), new Pattern(200, 100), new Pattern(300, 100)};
		int seed = 1024;
		Double[][] severityEdges = {{null, new Double(5), new Double(10)}, 
									{new Double(100), null, new Double(50)}, 
									{new Double(1000), new Double(500), null}
		};
		
		ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, states, trials + seed, severityEdges); // Abrupt Volatility Change
		trainingNetworkStream.networkNoise = networkNoise; // percentage of transition noise
		trainingNetworkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		trainingNetworkStream.noiseStandardDeviation = networkNoiseStandardDeviation;// pattern noise 
		trainingNetworkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag

		// set the bernoulli stream (testing)
		// bernoulli.setNoise(0.0); // noise for error rate generator
		
		int blockLength = 100;
		
		// set the bernoulli stream (training)
		DoubleStream trainingStream = new DoubleStream(1024, 100, 1, 1);
		// BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
		int numBlocks = 0;
		while(numBlocks < blockLength)
		{
			int streamInterval = trainingNetworkStream.generateNext();
			// training 
			for (int i = 0; i < streamInterval; i++) 
			{
				double output = trainingStream.generateNext();
				dataWrtier.write(output+"");
				dataWrtier.newLine();
				
			}
			numBlocks++;
			trainingStream.addDrift(trainingNetworkStream.getCurrentSeverity()); // create one drift
		}
		String networkString = trainingNetworkStream.getActualNetwork().getNetworkString();

		
		dataWrtier.close();
		System.out.println("Done");
	}
	

	
	public static void testIntegerStreamGenerator()
	{
		DoubleStream stream = new DoubleStream(78, 100, 1.0, 1.0);
		Random random = new Random();
		
		for(int i=0;i<1000;i++)
		{
			if(i%100==0) stream.addDrift(random.nextInt(100));
			System.out.println(stream.generateNext());
		}
	}
	
	public static void testNetworkWithSeverityEdges3Patterns()
	{
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		
		Pattern[] states = { new Pattern(100, 100), new Pattern(200, 100), new Pattern(300, 100)};
		int seed = 1024;
		Double[][] severityEdges = {{null, new Double(10), new Double(20)}, 
									{new Double(30), null, new Double(40)}, 
									{new Double(50), new Double(60), null}
		};
		
		ProbabilisticNetworkStream networkStream = new ProbabilisticNetworkStream(networkTransitions, states, seed, severityEdges);
		
		double networkNoise = 0;
		int stateTimeMean = 100;
		double networkNoiseStandardDeviation = 0;
		double patternNoiseFlag = 1;
		
		networkStream.networkNoise = networkNoise; // percentage of transition noise
		networkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		networkStream.noiseStandardDeviation = networkNoiseStandardDeviation;// pattern noise 
		networkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag
		
		int i=0;
		while(i<5000)
		{
			networkStream.generateNext();
			System.out.println(networkStream.getCurrentState()+","+networkStream.getCurrentSeverity());
			i++;
		}
	}
	
	public static void testNetworkWithSeverityEdges2Patterns()
	{
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = {{0,1}, {1, 0}};
		
		Pattern[] states = { new Pattern(100, 100), new Pattern(200, 100)};
		int seed = 1024;
		Double[][] severityEdges = {{null, new Double(36)}, {new Double(72), null}};
		
		ProbabilisticNetworkStream networkStream = new ProbabilisticNetworkStream(networkTransitions, states, seed, severityEdges);
		
		double networkNoise = 0;
		int stateTimeMean = 100;
		double networkNoiseStandardDeviation = 0;
		double patternNoiseFlag = 1;
		
		networkStream.networkNoise = networkNoise; // percentage of transition noise
		networkStream.setStateTimeMean(stateTimeMean); // set volatility interval of stream
		networkStream.noiseStandardDeviation = networkNoiseStandardDeviation;// pattern noise 
		networkStream.intervalNoise = patternNoiseFlag; // patternNoiseFlag
		
		int i=0;
		while(i<1000)
		{
			networkStream.generateNext();
			System.out.println(networkStream.getCurrentSeverity());
			i++;
		}
		
	}
	
	


	public static void testMagSeed()
	{
		int blockSize = 1000;
		BernoulliGenerator generator;
		double[] pattern = new double[]
		{ 0.1, 0.5, 0.3, 0.4 };

		ArrayList<Double> data = new ArrayList<Double>(pattern.length * blockSize);

		for (int i = 0; i < pattern.length; i++)
		{
			generator = new BernoulliGenerator(pattern[i]);
			for (int j = 0; j < blockSize; j++)
			{
				data.add((double) generator.generateNext());
			}
		}

		MagSeed magSeed = new MagSeed(0.01, 0.1, 32, 32, 1000.0);

		int i = 0;
		for (double item : data)
		{
			if (magSeed.setInput(item))
			{
				System.out.println(i + "," + magSeed.getSeverity() + "," + magSeed.getWindowSize());

			}
			i++;
		}

	}
	


	public static void fluTrendServerity() throws IOException
	{
		FileReader reader = new FileReader("/Users/rl/Desktop/data/interpolated_flutrend.csv");
		CSVFormat csvFileFormat = CSVFormat.RFC4180.withFirstRecordAsHeader();
		CSVParser parser = new CSVParser(reader, csvFileFormat);
		List<CSVRecord> records = parser.getRecords();

		ArrayList<Double> data = new ArrayList<Double>(records.size());

		for (int i = 0; i < records.size(); i++)
		{
			data.add(Double.parseDouble(records.get(i).get("New.Zealand")));
		}

		parser.close();

		// public MagSeed(double delta, int blockSize, int decayMode, int
		// compressionMode, double epsilonHat, double alpha,
		// int term, int preWarningBufferSize) // Lin's new constructor

		MagSeed magSeed = new MagSeed(0.05, 0.1, 32, 32, 1.0 / 15);

		int i = 0;
		for (double item : data)
		{
			if (magSeed.setInput(item))
			{
				System.out.println(i + "," + magSeed.getSeverity() + "," + magSeed.getWindowSize());

			}
			i++;
		}
	}
	
	private summer.originalSeed.SeedDetector createOriginalSeed(String type) {
		if (type.equals("best")) {
			return new summer.originalSeed.SeedDetector(0.05, 32, 1, 1, 0.01, 0.8, 75); // Seed Best
		} else {
			return new summer.originalSeed.SeedDetector(0.05, 32, 1, 1, 0.0025, 0.2, 75); // Seed Worst
		} 
	}

}
