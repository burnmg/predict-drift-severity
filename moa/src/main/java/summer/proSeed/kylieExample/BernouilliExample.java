package summer.proSeed.kylieExample;

import sizeof.agent.SizeOfAgent;
import summer.proSeed.DriftDetection.ADWINChangeDetector;
import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.BernoulliGenerator;
import summer.proSeed.PatternMining.IndexedPattern;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Network.ProbabilisticNetwork;
import summer.proSeed.PatternMining.Network.SeverityReservoirSampingEdge;
import summer.proSeed.PatternMining.Streams.ProbabilisticNetworkStream;
import summer.proSeed.VolatilityDetection.DriftPrediction;
import summer.proSeed.VolatilityDetection.RelativeVolatilityDetector;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.rosuda.JRI.Rengine;

import moa.classifiers.core.driftdetection.DDM;

// Currently testing for Bernoulli Abrupt

public class BernouilliExample {

	// volatiltiy detector parameters
	private double conf = 0.5;
	private int bufferSize = 32;
	
	// stream parameters
	private double networkNoise = 0.0;
	private double patternNoise = 0.0;
	private int volatility = 100; // stable period for each pattern
	private int changePoints = 500; // number of volatility change points
	private double patternNoiseFlag = 1; // noise flag
	
	private int trials = 1;
		
	// drift detector
	String[] detectorSettings = { "best" };
	private int detectorType = 0; // 0 = original seed, 1 = proseed simple , 2 = Combination, 3 ADWIN	
	private int learningPeriod = 0;
	
	// drift predictor parameters
	private double alpha = 0.05; // select from 0.05, 0.01, 0.1
	private int kFrequentNum = 100; // select from 10, 30, 100
	private int patternSize = 100;
	private boolean volDrift = false;

	private Rengine re; // R engine
	//private String path = "P:\\Robert\\Uni\\Eclipse workspace\\kylieExample\\Data\\output\\"; // output path	 
	//private String mainPath = "P:\\Robert\\Uni\\Eclipse workspace\\kylieExample\\Data\\";	
	private String path = "/Users/rl/Desktop/output/"; // output path	 
	private String mainPath = "/Users/rl/Desktop/data/";

	public static void main(String[] args) throws Exception {
		System.out.println(System.getProperty("java.library.path"));
		BernouilliExample test = new BernouilliExample(args);
	}

	public BernouilliExample(String[] args) throws Exception {
		initR(args); // initialize R engine
		
		Pattern.setRengine(re);		
		Pattern.ALPHA = alpha;	
		Pattern.ABRUPT = true; // abrupt stream
				
		testSmall(volatility, trials, 3, 0.75, 0.25);
			
		closeR(); // close R engine
	}

	
	private void initR(String[] args) {
		re = new Rengine(args, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
	}

	private void closeR() {
		re.end();
		System.out.println("R closed");
	}
	
	private ADWINChangeDetector createADWIN() {
		ADWINChangeDetector detector = new ADWINChangeDetector();
		detector.deltaAdwinOption.setValue(0.05);
		return detector;
	}
	
	private DDM createDDM() {
		DDM detector = new DDM();
		return detector;
	}

	private SeedDetector createSeed(String type) {	
		if (type.equals("best")) {
			return new SeedDetector(0.05, 32, 1, 1, 0.01, 0.8, 75); // ProSeed Best
		} else {
			return new SeedDetector(0.05, 32, 1, 1, 0.0025, 0.2, 75); // ProSeed Worst
		}
	}

	private summer.originalSeed.OriginalSeedDetector createOriginalSeed(String type) {
		if (type.equals("best")) {
			return new summer.originalSeed.OriginalSeedDetector(0.05, 32, 1, 1, 0.01, 0.8, 75); // Seed Best
		} else {
			return new summer.originalSeed.OriginalSeedDetector(0.05, 32, 1, 1, 0.0025, 0.2, 75); // Seed Worst
		} 
	}

	public void testSmall(int v, int trials, int k, double transHigh, double transLow) throws Exception {
		System.out.println("n3");

		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		Pattern[] states = { new Pattern(100, 100), new Pattern(200, 100), new Pattern(300, 100) };

		path = mainPath + detectorType + "\\";
		testNetwork("n3_" + transHigh + "_" + transLow  + "_N" + patternSize, networkTransitions, states, trials, volatility, k, conf, bufferSize, networkNoise, patternNoise, "" + detectorType);
	}

		
	public void testNetwork(String name, double[][] networkTransitions, Pattern[] states, int trials, int volatility,
			int mParameter, double conf, int bufferSize, double noise, double sd, String seedType) throws Exception {

		BufferedWriter streamNetworkWriter = new BufferedWriter(new FileWriter(path + "sd_" + sd + "_noise_" + noise
				+ "_stream_network_" + name + "_volatility_" + volatility + "alpha_" + alpha + ".txt"));
		BufferedWriter streamPatternWriter = new BufferedWriter(new FileWriter(path + "sd_" + sd + "_noise_" + noise
				+ "_stream_patterns_" + name + "_volatility_" + volatility + "alpha_" + alpha + ".txt"));
		BufferedWriter detectorSortedNetworkWriter = new BufferedWriter(
				new FileWriter(path + "sd_" + sd + "_noise_" + noise + "_detector_network_sorted_" + name
						+ "_volatility_" + volatility + "_conf_" + conf + "_buffer_" + bufferSize + "alpha_" + alpha + ".txt"));
		BufferedWriter detectorSortedPatternWriter = new BufferedWriter(
				new FileWriter(path + "sd_" + sd + "_noise_" + noise + "_detector_patterns_sorted_" + name
						+ "_volatility_" + volatility + "_conf_" + conf + "_buffer_" + bufferSize + "alpha_" + alpha + ".txt"));
		BufferedWriter writer = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/data.txt"));
		
		BufferedWriter driftWriter = new BufferedWriter(
				new FileWriter("/Users/rl/Desktop/drift.txt"));

		for (int seed = 0; seed < trials; seed++) {
			System.out.println("\nSeed\t" + seed + "\n" + "sd_" + sd + "_noise_" + noise + "_detector_network_sorted_"
					+ name + "_volatility_" + volatility + "_conf_" + conf + "_buffer_" + bufferSize);
				
			// set the network stream (training)
			Double[][] severityEdges = {{null, new Double(10), new Double(20)}, 
					{new Double(30), null, new Double(40)}, 
					{new Double(50), new Double(60), null}};
			ProbabilisticNetworkStream networkStream = new ProbabilisticNetworkStream(networkTransitions, states, seed, severityEdges); // Abrupt Volatility Change
			networkStream.networkNoise = noise; // percentage of transition noise
			networkStream.setStateTimeMean(volatility); // set volatility interval of stream
			networkStream.noiseStandardDeviation = sd;
			networkStream.intervalNoise = patternNoiseFlag;
			// set the network stream (testing)
			ProbabilisticNetworkStream trainingNetworkStream = new ProbabilisticNetworkStream(networkTransitions, states, trials + seed, severityEdges); // Abrupt Volatility Change
			trainingNetworkStream.networkNoise = noise; // percentage of transition noise
			trainingNetworkStream.setStateTimeMean(volatility); // set volatility interval of stream
			trainingNetworkStream.noiseStandardDeviation = sd;
			trainingNetworkStream.intervalNoise = patternNoiseFlag;

			// set the bernoulli stream (testing)
			BernoulliGenerator bernoulli = new BernoulliGenerator(0.2, seed);
			bernoulli.setNoise(0.0); // noise for error rate generator
			
			// set the bernoulli stream (training)
			BernoulliGenerator trainBernoulli = new BernoulliGenerator(0.2, trials + seed);
			trainBernoulli.setNoise(0.0); // noise for error rate generator

			//////////////////////////////////////
			// detector type
			//////////////////////////////////////
			CutPointDetector driftDetectorForVolDector = null;
			CutPointDetector detector = null;
			
			if (detectorType == 0) {
				driftDetectorForVolDector = createOriginalSeed(seedType); // Original SEED
				detector = createOriginalSeed(seedType); // Original SEED
			} else if (detectorType == 1) {
				driftDetectorForVolDector = createSeed(seedType); // ProSEED
				detector = createSeed(seedType); // ProSEED			
			} else if (detectorType == 2) {
				driftDetectorForVolDector = createOriginalSeed(seedType); // Original SEED
				detector = createSeed(seedType); // ProSEED
			} else {
				driftDetectorForVolDector = createADWIN();
				detector = createADWIN();
			}

			DriftPrediction driftPredictor = new DriftPrediction(mParameter, patternSize, alpha, kFrequentNum);
			RelativeVolatilityDetector volatilityDetector = new RelativeVolatilityDetector(driftDetectorForVolDector, bufferSize, conf, driftPredictor, new SeverityReservoirSampingEdge(10));

			int streamLength = learningPeriod +  networkStream.stateTimeLength * changePoints;
			int samples = 0;
			int intervalCount = 0;
			int tp = 0, fp = 0;
			double delay = 0;
	
			double totalSamples = 0;

			boolean driftOccurred = false; // ground truth
			DescriptiveStatistics delayStats = new DescriptiveStatistics();
			while (samples < streamLength) {
				int streamInterval  = 0;
				if (samples < learningPeriod) {
					streamInterval = trainingNetworkStream.generateNext();
				} else {
					streamInterval = networkStream.generateNext(); // generated interval
				}						
				
				for (int i = 0; i < streamInterval; i++) {
					intervalCount++; // added
					
					double bernoulliOutput = 0;
					if (samples < learningPeriod) {
						bernoulliOutput = trainBernoulli.generateNext();
					}else {
						bernoulliOutput = bernoulli.generateNext();
						totalSamples++;
					}
															
					double timestep = volatilityDetector.getTimeStamp() + 1.0;

					// detectors read input
					// train the network. The network is in volatilityDetector. 
					boolean drift = detector.setInputWithTraining(bernoulliOutput);			
					volDrift = volatilityDetector.setInputVarViaBuffer(bernoulliOutput);
					if(volDrift) driftWriter.write(samples +"\n");
					
					double[][] prediction = null;					
					if (samples >= learningPeriod) {						
						// get prediction from the volatility detector
						prediction = volatilityDetector.getPredictor().predictNextCI(volDrift, timestep);												
					}
			
					// set predictions
					detector.setPredictions(prediction);
					volatilityDetector.getDetector().setPredictions(prediction); 
							
					if (drift && samples > learningPeriod) {						
						if (driftOccurred) {
							tp++;
							driftOccurred = false;
							delayStats.addValue(delay);
						} else {
							fp++;
						}
						intervalCount = 0;
					}
					delay++;

				}

				if (samples < learningPeriod) { // learning period
					trainBernoulli.swapMean();
				} else {
					bernoulli.swapMean();	
				}
								
				driftOccurred = true;
				delay = 0;
				
				samples++;
			}
					
			// stream finished	

			int networkSize = volatilityDetector.getPredictor().getPatternReservoir().getSize();
			System.out.println("size = " + networkSize);

			volatilityDetector.getPredictor().getPatternReservoir().merge(); // MERGING at end to compare network

			
			//String actualNetwork = networkStream.getActualNetwork().getNetworkProbabilitiesString();
			String streamActualNetwork = networkStream.getActualNetwork().getNetworkString();
			streamNetworkWriter.write("Seed\t" + seed + "\t" + streamActualNetwork + "\n");
			String actualStates = networkStream.getStatesString();
			streamPatternWriter.write("Seed\t" + seed + "\t" + actualStates + "\n");

			// get network of the volatility detector. 
			ProbabilisticNetwork detectorSortedNetwork = new ProbabilisticNetwork(volatilityDetector.getPredictor().getPatternReservoir().getSortedNetwork());
			String sortedNetworkString = detectorSortedNetwork.getNetworkString();
			// String sortedNetworkString = detectorSortedNetwork.getNetworkProbabilitiesString();
			detectorSortedNetworkWriter.write("Seed\t" + seed + "\t" + sortedNetworkString + "\n");

			// get patterns of the volatility detector.
			IndexedPattern[] indexedPatterns = volatilityDetector.getPredictor().getPatternReservoir().getSortedPatterns();
			String sortedPatternsString = volatilityDetector.getPredictor().getPatternReservoir().getPatternStringOf(indexedPatterns);
			detectorSortedPatternWriter.write("Seed\t" + seed + "\t" + sortedPatternsString + "\n");

			int numErrors[] = networkStream.getActualNetwork().compareTo(detectorSortedNetwork);

		}

		streamNetworkWriter.close();
		streamPatternWriter.close();

		detectorSortedNetworkWriter.close();
		detectorSortedPatternWriter.close();

		driftWriter.close();
		writer.close();

	}



}
