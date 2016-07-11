package a.evaluator;

public class CorreteModeCoverageEvaluator
{
	/**
	 * Evaluate percentage of using the right volatility mode. 
	 * 
	 * @param expected each array is a pair of int representing an interval, with an additional int representing volatility mode. 
	 * e.g. {
	 * 		{0, 50000, 1},
	 * 		{50001, 100000, 2}
	 * }
	 * @param actual each array is a pair of int representing an interval
	 * 
	 * @return percentage of using the right mode.  
	 */
	public double evalutate(int[][] expected, int[][] actual)
	{
		
		int indexExpected = 0; 
		int indexActual = 0;
		
		int numOverlaps = 0;
		int streamLength = expected[expected.length-1][1] + 1; // the tail of the last interval is the length of the stream.
		
		while(indexExpected < expected.length && indexActual < actual.length)
		{
			
			//case 1: expected interval is on the right of actual
			if(!(expected[indexExpected][0] > actual[indexActual][1] && expected[indexExpected][1] > actual[indexActual][0]) 
					&& (expected[indexExpected][2] == actual[indexActual][2]))
			{
				int actualHead = actual[indexActual][0];
				int actualTail = actual[indexActual][1];
				
				if(actual[indexActual][0] < expected[indexExpected][0] )
				{
					actualHead = expected[indexExpected][0];
				}
				
				if(actual[indexActual][1] > expected[indexExpected][1])
				{
					actualTail = expected[indexExpected][1];
					indexExpected++;
				}
				else
				{
					indexActual++;
				}
				
				numOverlaps += actualTail - actualHead + 1;
			}
			else
			{
				if(actual[indexActual][1] > expected[indexExpected][1])
				{
					indexExpected++;
				}
				else
				{
					indexActual++;
				}
			}	
		}
		
		return (double)numOverlaps/streamLength;
	}

}
