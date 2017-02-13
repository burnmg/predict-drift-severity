package summer.main;

import summer.proSeed.PatternMining.Pattern;

public class PatternGenerator
{
	public static Pattern[] generatePattern(int start, int step, int count)
	{
		Pattern[] patterns = new Pattern[count];
		int length = start;
		for(int i=0;i<count;i++)
		{
			patterns[i] = new Pattern(length, 100);
			length += step;
		}
		return patterns;
	}
	
	public static double[][] generateNetworkProb(double[] probs)
	{
		int patternCount = probs.length + 1;
		
		double testSum = 0;
		for(int i=0;i<probs.length;i++)
		{
			testSum += probs[i];
		}
		
		if(Math.abs(testSum-1)>0.0001) return null;
		
		double[][] network = new double[patternCount][patternCount];
		int probsIndex = 0;
		for(int i=0;i<network.length;i++)
		{
			for(int j=0;j<network[0].length;j++)
			{
				if(i==j)
				{
					network[i][j] = 0;
				}
				else
				{
					network[i][j] = probs[probsIndex];
					
					probsIndex++;
				}
			}
			probsIndex = 0;
		}
		
		return network;
	}
	
	// different algorithm from the generateNetworkProb
	public static Double[][] generateEdges(double startSeverity, double step, int patternCount)
	{
		
		Double[][] edges = new Double[patternCount][patternCount];
		double severity = startSeverity;
		for(int i=0;i<edges.length;i++)
		{
			
			for(int j=0;j<edges[0].length;j++)
			{
				if(i==j)
				{
					edges[i][j] = null;
				}
				else
				{
					edges[i][j] = severity;
					severity += step;
				}
			}
		}
		
		return edges;
	}

}
