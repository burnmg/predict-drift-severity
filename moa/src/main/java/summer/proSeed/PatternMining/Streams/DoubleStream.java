package summer.proSeed.PatternMining.Streams;

import java.util.Random;

import javax.print.attribute.standard.Severity;

import summer.proSeed.PatternMining.StreamGenerator;

public class DoubleStream implements StreamGenerator
{
	private double mean;
	private double streamNoise;
	private double driftNoise;
	private Random random;

	public DoubleStream(int ranSeed, double mean, double streamNoise, double driftNoise)
	{
		random = new Random(ranSeed);
		this.mean = mean;
		this.streamNoise = streamNoise;
		this.driftNoise = driftNoise;
	}

	public double generateNext()
	{
		return mean + streamNoise * random.nextGaussian();
	}

	public void addDrift(double driftSeverity)
	{
		// this.mean += driftSeverity + driftNoise * random.nextGaussian();
		this.mean += (1+driftSeverity) * streamNoise * random.nextGaussian();
	}
	
	public void addVarDrift(double driftSeverity)
	{
		this.streamNoise += driftSeverity;
	}
}
