package summer.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.rosuda.JRI.Rengine;

import moa.classifiers.core.driftdetection.ADWIN;
import summer.proSeed.DriftDetection.ProSeed2;
import summer.proSeed.DriftDetection.SeedDetector;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.kylieExample.TextConsole;

public class RealWorldDataTester
{
	public static void main(String args[]) throws FileNotFoundException, IOException
	{/*
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
		String stream = "/Users/rl/Desktop/forest error.csv";
		String PRESSdriftWriterDir = "/Users/rl/Desktop/forest_press.csv";
		String ADWINdriftWriterDir = "/Users/rl/Desktop/forest_adwin.csv";
		double detectorConfidence = 0.15;
		double beta = 1;
		SeedDetector VDSeedDetector = new SeedDetector(detectorConfidence, 32);
		ProSeed2 proseed = new ProSeed2(3, 20, 0.05, 100, 
				VDSeedDetector, 32, 0.05, 10, 2000, false, beta);
		
		BufferedReader streamReader = null;
		String input = null;
		for(int i=0;i<4;i++)
		{
			//Train
			streamReader =	new BufferedReader(new FileReader(stream));
			
			streamReader.readLine();
			input = streamReader.readLine();
			while(input!=null)
			{
				double value = Double.parseDouble(input);
				proseed.setInputWithTraining(value);
				input = streamReader.readLine();
			}
			streamReader.close();
		}
		
		//Test PRESS
		BufferedWriter PRESSdriftWriter = new BufferedWriter(new FileWriter(PRESSdriftWriterDir));
		streamReader = new BufferedReader(new FileReader(stream));
		streamReader.readLine();
		input = streamReader.readLine();
		int dataCount = 0;
		int driftCount = 0;
		while(input!=null)
		{
			double value = Double.parseDouble(input);
			if(proseed.setInputWithTraining(value)) {
				PRESSdriftWriter.write(dataCount+"\n");
				driftCount++;
			}
			input = streamReader.readLine();
			dataCount++;
		}
		PRESSdriftWriter.close();
		streamReader.close();
		System.out.println(driftCount);
		
		//Test ADWIN
		ADWIN adwin = new ADWIN(detectorConfidence);
		BufferedWriter ADWINdriftWriter = new BufferedWriter(new FileWriter(ADWINdriftWriterDir));
		streamReader = new BufferedReader(new FileReader(stream));
		streamReader.readLine();
		input = streamReader.readLine();
		dataCount = 0;
		driftCount = 0;
		while(input!=null)
		{
			double value = Double.parseDouble(input);
			if(adwin.setInput((value))) {
				ADWINdriftWriter.write(dataCount+"\n");
				driftCount++;
			}
			input = streamReader.readLine();
			dataCount++;
		}
		ADWINdriftWriter.close();
		streamReader.close();
		System.out.println(driftCount);
		
	}
}
