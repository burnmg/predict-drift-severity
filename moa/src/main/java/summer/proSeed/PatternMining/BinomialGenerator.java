package summer.proSeed.PatternMining;

import java.util.Random;

public class BinomialGenerator implements StreamGenerator
{
	private int n;
	private double p;
	Random ran;
	
	public BinomialGenerator(int n, double p, int seed)
	{
		this.n = n;
		this.p = p;
		this.ran = new Random(seed);
	}

	@Override
	public double generateNext()
	{
		
		return (int)(n*p + Math.sqrt(n*p*(1-p)) * ran.nextGaussian());
	}

	@Override
	public void addDrift(double driftSeverity)
	{
		p = p + driftSeverity;
		if(p>0.95) p = 0.95;
		if(p<0.05) p = 0.05;

	}

	@Override
	public void setSeed(int seed)
	{
		ran = new Random(seed);

	}

}
