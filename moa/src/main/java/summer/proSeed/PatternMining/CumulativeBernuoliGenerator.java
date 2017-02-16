package summer.proSeed.PatternMining;

import java.util.Random;

public class CumulativeBernuoliGenerator implements StreamGenerator
{

	private double p;
	private int cumLength;
	private Random random;
	public CumulativeBernuoliGenerator(double p, int cumLength, int seed)
	{
		this.p = p;
		this.cumLength = cumLength;
		random = new Random(seed);
	}
	
	@Override
	public double generateNext()
	{
		int sum = 0;
		for(int i=0;i<cumLength;i++)
		{
	        double rand = this.random.nextDouble();
	        int output = 0 ;
	        if (rand < p) {
	        	output = 1;
	        }
	        
	        rand = this.random.nextDouble();
	        if(rand<0.05){
                if (output == 1) {
                    output = 0;
                } else if (output == 0) {
                    output = 1;
                }
	        }
	        
	        sum += output;
	        
		}

		double rand = this.random.nextDouble();

		return sum;
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
		random = new Random(seed);

	}

}
