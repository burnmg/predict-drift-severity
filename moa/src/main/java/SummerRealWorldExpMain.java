import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.rosuda.JRI.Rengine;

import summer.originalSeed.OriginalSeedDetector;
import summer.proSeed.DriftDetection.ADWINChangeDetector;
import summer.proSeed.DriftDetection.CutPointDetector;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.kylieExample.TextConsole;

public class SummerRealWorldExpMain
{
	public static void main(String[] args) throws IOException
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

		String fileName = "/Users/rl/Desktop/real world error streams/poker.csv";
		for(double confidence=0.05;confidence<0.3;confidence += 0.05)
		{
			System.out.println(confidence);
			runSet(fileName, confidence);
			System.out.println();
			System.out.println();
		}

		re.end();
		
	}
	
	private static void runSet(String fileName, double confidence) throws IOException
	{

		
		/**
		 * PRESS
		 */
		BufferedReader reader1 = new BufferedReader(new FileReader(fileName));
		BufferedReader reader2 = new BufferedReader(new FileReader(fileName));
		SeedDetector VDSeedDetector = new SeedDetector(0.25, 32);
		CutPointDetector detector = new ProSeed2(3, 10, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 10, 2000, false, 0.4);
		System.out.println(run(detector, reader1, reader2, true));
		/**
		 * ProSeed
		 */
		reader1 = new BufferedReader(new FileReader(fileName));
		reader2 = new BufferedReader(new FileReader(fileName));
		
		SeedDetector VDSeedDetector2 = new SeedDetector(confidence, 32);
		detector = new ProSeed2(3, 20, 0.05, 100, 
				VDSeedDetector2, 32, 0.05, 10, 2000, false, 0);
		System.out.println(run(detector, reader1, reader2, true));
		
		/**
		 * Seed
		 */
		reader1 = new BufferedReader(new FileReader(fileName));
		reader2 = new BufferedReader(new FileReader(fileName));
		detector = new OriginalSeedDetector(confidence,32);
		System.out.println(run(detector, reader1, reader2, false));
		/**
		 * ADWIN
		 */
		reader1 = new BufferedReader(new FileReader(fileName));
		reader2 = new BufferedReader(new FileReader(fileName));
		detector = new ADWINChangeDetector(confidence);

		System.out.println(run(detector, reader1, reader2, false));
		
		
	}
	
	
	private static int run(CutPointDetector detector, BufferedReader stream1, BufferedReader stream2, boolean training) throws NumberFormatException, IOException
	{
		
		
		if(training)
		{		
			String read = stream1.readLine();
			while(read!=null)
			{
				double input = Double.parseDouble(read);
				boolean drift = detector.setInputWithTraining(input);
				read = stream1.readLine();
				
				
			}
		}

		stream1.close();
		
		
		int driftCount = 0;
		String read = stream2.readLine();
		while(read!=null)
		{
			double input = Double.parseDouble(read);
			boolean drift = detector.setInput(input);
			read = stream2.readLine();
			if(drift) driftCount++;
		}
		stream2.close();
		return driftCount;
	}

}
