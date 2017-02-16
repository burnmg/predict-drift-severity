package summer.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import summer.originalSeed.OriginalSeedDetector;
import summer.proSeed.DriftDetection.ADWINChangeDetector;
import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.BinomialGenerator;
import summer.proSeed.PatternMining.CumulativeBernuoliGenerator;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.StreamGenerator;
import summer.proSeed.PatternMining.Streams.GradualDoubleStream;


public class SummerExperimentThreadUnit implements Callable<Integer>
{

	private double[] confidences;
	private double[] betas;
	private int seed;
	private String detectorName;
	private int repeatTime;
	private String fileName;
	private Pattern[] patterns;
	double[][] networkTransitions;
	Double[][] severityEdges;
	
	public SummerExperimentThreadUnit(double[] confidences, double[] betas, int seed, int repeatTime, String detectorName, String fileName)
	{
		this.confidences = confidences;
		this.seed = seed;
		this.detectorName = detectorName;
		this.repeatTime = repeatTime;
		this.fileName = fileName;
		this.betas = betas;
	}
	
	public SummerExperimentThreadUnit(double[] confidences, double[] betas, int seed, int repeatTime, String detectorName, String fileName, 
			Pattern[] patterns, double[][] network, Double[][] severity)
	{
		this.confidences = confidences;
		this.seed = seed;
		this.detectorName = detectorName;
		this.repeatTime = repeatTime;
		this.fileName = fileName;
		this.betas = betas;
		this.patterns = patterns;
		this.networkTransitions = network;
		this.severityEdges = severity;
	}

	@Override
	public Integer call() throws Exception
	{

		
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rl/Desktop/experiments/"+fileName+".csv"));
		// confidence, FP, FN, DL
		writer.write("detectorName,repeatID,beta,confidence,FP,FN,DL,TD,COUNT\n");

		// set network
		/**
		 * 3 patterns
		 */
		
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		
		Double[][] severityEdges = {{null, new Double(1), new Double(2)}, 
				{new Double(3), null, new Double(4)}, 
				{new Double(5), new Double(6), null}
		};
		
		
		/**
		 * 5 patterns
		 */
		/*
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100), new Pattern(4000, 100), new Pattern(5000, 100)};
		double[][] networkTransitions = PatternGenerator.generateNetworkProb(new double[]{0.2, 0.2, 0.4, 0.2});
		Double[][] severityEdges = PatternGenerator.generateEdges(1, 0.5, 5);
		*/
		
		/**
		 * 10 patterns
		 */
		/*
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(1500, 100), new Pattern(2000, 100), new Pattern(2500, 100), new Pattern(3000, 100), 
				new Pattern(3500, 100), new Pattern(4000, 100), new Pattern(4500, 100), new Pattern(5000, 100), new Pattern(5500, 100)
		};
		
		
		double[][] networkTransitions = PatternGenerator.generateNetworkProb(new double[]{0.1, 0.1, 0.15, 0.1, 0.15, 0.1, 0.1, 0.1, 0.1});
		Double[][] severityEdges = PatternGenerator.generateEdges(1, 0.25, 10);
		*/
		
		/**
		 * 3 patterns Bernuoli
		 */
		/*
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };
		
		Double[][] severityEdges = {{null, new Double(0.2), new Double(0.3)}, 
				{new Double(0.4), null, new Double(0.5)}, 
				{new Double(0.6), new Double(0.7), null}
		};
		*/
		
		
		/**
		 * 5 patterns Bernuoli
		 */
		
		/**
		 * 10 patterns Bernuoli
		 */
		
		
		for(int i=0;i<confidences.length;i++)
		{
			for(int j=0;j<betas.length;j++)
			runWithOneConfidence(confidences[i], betas[j], patterns, networkTransitions, severityEdges, writer);
		}
		

		writer.close();
	
		return null;
	}
	
	public void runWithOneConfidence(double confidence, double beta, Pattern[] patterns, double[][] networkTransitions, Double[][] severityEdges, BufferedWriter writer) throws FileNotFoundException, IOException
	{
		Random random = new Random(seed);
		int blockLength = 50000;
		
		// loop start here
		for(int i=0;i<this.repeatTime;i++)
		{
			System.out.println(detectorName+":"+i);
			seed = random.nextInt();
			
			//StreamGenerator dataStream = new DoubleStream(random.nextInt(), 0.05, 1, 1);
			StreamGenerator dataStream = new GradualDoubleStream(random.nextInt(), 0.05, 1, 1, 100);
			//StreamGenerator dataStream = new BernoulliGenerator(0.5);
			//StreamGenerator dataStream = new CumulativeBernuoliGenerator(0.5, 100, seed);
			//StreamGenerator dataStream = new BinomialGenerator(100, 0.5, 1);
			SummerExperiment experiment = null;
			double detectorConfidence = confidence;
			
			CutPointDetector detector = null;
			if(detectorName.equals("ProSeed2"))
			{
				SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 32);
				detector = new ProSeed2(3, 20, 0.05, 100, 
						VDSeedDetector, 32, 0.05, 10, 2000, false, beta);
			}
			else if(detectorName.equals("ADWIN"))
			{
				detector = new ADWINChangeDetector(detectorConfidence);
			}
			else if(detectorName.equals("Seed"))
			{
				detector = new OriginalSeedDetector(detectorConfidence,32);
			}
			else if(detectorName.equals("ProSeed1")) {
				SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 32);
				detector = new ProSeed2(3, 20, 0.05, 100, 
						VDSeedDetector, 32, 0.05, 10, 2000, false, 0);
			}
			else
			{
				detector = null;
			}
			
			
			experiment = new SummerExperiment(detector, detectorConfidence, dataStream, 
					blockLength, blockLength, seed, networkTransitions, patterns, severityEdges);
			if(detectorName.equals("ProSeed2") || detectorName.equals("ProSeed1"))
			{
				experiment.setNeedTraining(true);
			}
			double[] res = experiment.run();
			// detectorName, repeatID, beta, confidence, FP, FN, DL, true positive rate, instance count
			writer.write(String.format("%s,%d,%f,%f,%f,%f,%f,%f,%f\n", detectorName, i, beta, res[0], res[1], res[2], res[3], res[4], res[5]));
			
		}


	}

}
