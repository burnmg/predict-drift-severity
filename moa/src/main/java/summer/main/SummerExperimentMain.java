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

public class SummerExperimentMain
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
		
		
		/*
		 * S E1
		 */
		double detectorConfidence = 0.1;
		SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 0.1, 5, 1, 1, 0.01, 0.8, 75, 32, 200);
		ProSeed2 proSeed2 = new ProSeed2(3, 80, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 10, 2000, false, 0.3);
		
		
		DoubleStream trainingStream = new DoubleStream(1024, 0, 1, 1);
		
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100)};
		double transHigh = 0.75;
		double transLow = 0.25;
		double[][] networkTransitions = { { 0, transHigh, transLow }, { transLow, 0, transHigh }, { transHigh, transLow, 0 } };

		Double[][] severityEdges = {{null, new Double(0.4), new Double(0.35)}, 
				{new Double(0.3), null, new Double(0.25)}, 
				{new Double(0.2), new Double(0.15), null}
		};
		
		SummerExperiment e1 = new SummerExperiment(proSeed2, detectorConfidence, trainingStream, 
				100000, 100000, 3213, networkTransitions, patterns, severityEdges);
		double[] res = e1.run();
		/*
		 * E E1
		 */
		
		
		System.out.println("done");
	}

}
