package summer.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.rosuda.JRI.Rengine;

import summer.magSeed.MagSeed;
import summer.proSeed.DriftDetection.ProSeed;
import summer.proSeed.PatternMining.BernoulliGenerator;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.kylieExample.TextConsole;

public class Main
{

	public static void main(String[] args) throws IOException
	{
		// TODO test 
		String[] reArgs = new String[]{"--save"};
		Rengine re = new Rengine(reArgs, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		Pattern.setRengine(re);
		
		fluTrendServerityProSeed();
	}
	
	public static void testProSeed() throws FileNotFoundException, IOException
	{
		summer.originalSeed.SeedDetector VDSeedDetector =new summer.originalSeed.SeedDetector(0.05, 32, 1, 1, 0.01, 0.8, 75);
		ProSeed proSeed = new ProSeed(3, 100, 0.05, 100, 
				VDSeedDetector, 32, 0.5, 0);
	}
	


	public static void testDrift()
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
	
	public static void fluTrendServerityProSeed() throws IOException
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

		summer.originalSeed.SeedDetector VDSeedDetector =new summer.originalSeed.SeedDetector(0.5, 32, 1, 1, 0.01, 0.8, 75);
		ProSeed proSeed = new ProSeed(3, 100, 0.05, 100, 
				VDSeedDetector, 32, 0.5, 0);

		int i = 0;
		for (double item : data)
		{
			if (proSeed.setInput(item))
			{
				System.out.println(i + "," + proSeed.getSeverity());

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
