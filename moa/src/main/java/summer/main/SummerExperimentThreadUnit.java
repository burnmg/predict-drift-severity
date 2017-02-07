package summer.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;
import java.util.concurrent.Callable;

import summer.proSeed.DriftDetection.ADWINChangeDetector;
import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.PatternMining.Streams.DoubleStream;

public class SummerExperimentThreadUnit implements Callable<Integer>
{

	private double confidence;
	private int seed;
	private String detectorName;
	private int repeatTime;
	
	public SummerExperimentThreadUnit(double confidence, int seed, int repeatTime, String detectorName)
	{
		this.confidence = confidence;
		this.seed = seed;
		this.detectorName = detectorName;
		this.repeatTime = repeatTime;
	}

	@Override
	public Integer call() throws Exception
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rl/Desktop/experiments/"+detectorName+".csv"));
		// confidence, FP, FN, DL
		writer.write("detectorName,repeatID,confidence,FP,FN,DL\n");

		// set network
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };

		Double[][] severityEdges = {{null, new Double(0.15), new Double(0.2)}, 
				{new Double(0.25), null, new Double(0.35)}, 
				{new Double(0.4), new Double(0.45), null}
		};
		
		// loop start here
		for(int i=0;i<repeatTime;i++)
		{
			System.out.println(detectorName+":"+i);
			Random random = new Random(seed);
			DoubleStream dataStream = new DoubleStream(random.nextInt(), 0, 1, 1);
			SummerExperiment e1 = null;
			
			double detectorConfidence = confidence;
			
			CutPointDetector detector = null;
			if(detectorName.equals("ProSeed2"))
			{
				SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 0.1, 5, 1, 1, 0.01, 0.8, 75, 32, 200);
				detector = new ProSeed2(3, 80, 0.05, 100, 
						VDSeedDetector, 32, 0.05, 10, 2000, false, 0.3);
			}
			else if(detectorName.equals("ADWIN"))
			{
				detector = new ADWINChangeDetector(detectorConfidence);
			}
			
			int blockLenght = 500;
			e1 = new SummerExperiment(detector, detectorConfidence, dataStream, 
					blockLenght, blockLenght, seed, networkTransitions, patterns, severityEdges);
			if(detectorName.equals("ProSeed2")) e1.setNeedTraining(true);
			double[] res = e1.run();
			// detectorName, repeatID, confidence, FP, FN, DL
			writer.write(String.format("%s, %d, %f,%f,%f,%f\n", detectorName, i, res[0], res[1], res[2], res[3]));
			
		}

		writer.close();
		return null;
	}

}
