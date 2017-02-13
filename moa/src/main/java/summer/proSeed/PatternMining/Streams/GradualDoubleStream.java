package summer.proSeed.PatternMining.Streams;

import java.util.Random;

import summer.proSeed.PatternMining.StreamGenerator;

public class GradualDoubleStream implements StreamGenerator
{
	private double mean;
	private double streamNoise;
	private double driftMag;
	private Random random;
	// for gradual drift
	private double driftCoefficient  = 0;
	private double prevMean;
	private int driftWidth;
	private boolean isDrifting = false;
	
	public GradualDoubleStream(int ranSeed, double mean, double streamNoise, double driftMag, int driftWidth)
	{
		random = new Random(ranSeed);
		this.mean = mean;
		this.streamNoise = streamNoise;
		this.driftMag = driftMag;
		this.driftWidth = driftWidth;
	}

	@Override
	public double generateNext()
	{
		double value = 0;
		
		if(isDrifting)
		{
			value = (1-driftCoefficient)*this.prevMean + 
					driftCoefficient * mean + 
					streamNoise * random.nextGaussian();
			driftCoefficient += (double)1/driftWidth;
			
			if(driftCoefficient>1)
			{
				isDrifting = false;
				driftCoefficient = 0;
			}
			
		}
		else
		{
			value = mean + streamNoise * random.nextGaussian();
		}
		

		return value;
		
	}

	@Override
	public void addDrift(double driftSeverity)
	{
		isDrifting = true;
		this.prevMean = this.mean;
		this.mean = this.mean + driftSeverity*driftMag;
		
	}

	@Override
	public void setSeed(int seed)
	{
		// TODO Auto-generated method stub
		
	}

}
