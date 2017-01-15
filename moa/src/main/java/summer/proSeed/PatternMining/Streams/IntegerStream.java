package summer.proSeed.PatternMining.Streams;

import java.util.Random;

import summer.proSeed.PatternMining.StreamGenerator;

public class IntegerStream implements StreamGenerator
{
	private double mean;
	private double streamNoise;
	private double driftNoise;
	private Random random;
	
	public IntegerStream(int ranSeed, double mean, double streamNoise, double driftNoise)
	{
		random = new Random(ranSeed);
		this.mean = mean;
		this.streamNoise = streamNoise;
		this.driftNoise = driftNoise;
	}
	public double generateNext()
	{
		return mean + streamNoise*random.nextGaussian();
	}
	
	public void addDrift(int driftSeverity)
	{
		this.mean += driftSeverity + driftNoise*random.nextGaussian();
	}
}
