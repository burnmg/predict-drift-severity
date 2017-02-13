package summer.main;

import static org.junit.Assert.*;

import org.junit.Test;

import summer.proSeed.PatternMining.Pattern;

public class PatternGeneratorTest
{
	public static void main(String[] args)
	{
		PatternGenerator generator = new PatternGenerator();
		double[][] network = generator.generateNetworkProb(new double[]{0.1, 0.1,0.1,0.1, 0.2, 0.2, 0.1, 0.1}); 
		int t = 0;
	}

	public void testGeneratePattern()
	{
		PatternGenerator generator = new PatternGenerator();
		Pattern[] pattern = generator.generatePattern(1000, 1000, 3);
		int t = 0;
	}
	
	public static void testgenerateNetworkProb()
	{
		PatternGenerator generator = new PatternGenerator();
		double[][] network = generator.generateNetworkProb(new double[]{0.1, 0.1,0.1,0.1, 0.2, 0.2, 0.1, 0.1}); 
		int t = 0;
	}

}
