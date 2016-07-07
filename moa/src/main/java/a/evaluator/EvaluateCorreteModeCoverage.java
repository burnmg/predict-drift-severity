package a.evaluator;

public class EvaluateCorreteModeCoverage
{
	/**
	 * Evaluate percentage of using the right volatility mode. 
	 * 
	 * @param expected: each array is a pair of int representing an internval, with an additional int representing volatility mode. 
	 * e.g. {
	 * 		{0, 50000, 1},
	 * 		{50001, 100000, 2}
	 * }
	 * @param actual: each array is a pair of int representing an internval
	 * 
	 * @return percentage of using the right mode.  
	 */
	public int evalutate(int[][] expected, int[][] actual)
	{

		
		int indexExpected = 0; 
		int indexActual = 0;
		
		int numOverlaps = 0;
		int streamLength = expected[expected.length-1][1] + 1; // the tail of the last interval is the length of the stream.
		
		while(indexExpected < expected.length && indexActual < actual.length)
		{
			
			
			
//			//case 1. 
//			if(expected[indexExpected][0] > actual[indexActual][0] && expected[indexExpected][0] <= actual[indexActual][1] 
//					&& expected[indexExpected][1] > )
//			{
//				if(expected[indexExpected][2] == actual[indexActual][2])
//				{
//					//shrink actual head.
//					int actualHead = expected[indexExpected][0];
//					int actualTail = actual[indexActual][1];
//					
//					numOverlaps += actualTail - actualHead + 1;
//					
//				}
//
//				indexActual++;
//			}
//			// case 2
//			else if(expected[indexExpected][1] <= actual[indexActual][0] && expected[indexExpected][1] > actual[indexActual][0])
//			{
//				if(expected[indexExpected][2] == actual[indexActual][2])
//				{
//					int actualHead = actual[indexActual][0];
//					// shrink actual tail
//					int actualTail = expected[indexExpected][1];
//					
//					numOverlaps += actualTail - actualHead + 1;
//				}
//				
//				indexExpected++;
//			}
//			// case 3
//			else if(expected[indexExpected][0] <= actual[indexActual][0] && expected[indexExpected][1] >= actual[indexActual][1])
//			{
//				if(expected[indexExpected][2] == actual[indexActual][2])
//				{
//					int actualHead = actual[indexActual][0];
//					int actualTail = actual[indexActual][1];
//					
//					numOverlaps += actualTail - actualHead + 1;
//				}
//
//				
//				indexActual++;
//			}
//			// case 4
//			else if(expected[indexExpected][0] > actual[indexActual][0] && expected[indexExpected][1] < actual[indexActual][1])
//			{
//				if(expected[indexExpected][2] == actual[indexActual][2])
//				{
//					// shrink both 
//					int actualHead = expected[indexExpected][0];
//					int actualTail = expected[indexExpected][1];
//					
//					numOverlaps += actualTail - actualHead + 1;
//				}
//				
//				indexExpected++;
//			}
//			// case 5
//			else if(expected[indexExpected][0] > actual[indexActual][1])
//			{
//				
//				indexActual++;
//			}
//			
//			else
//			{
//				indexExpected++;
//			}
			
			
//			//case 1: expected interval is on the right of actual
//			if(expected[indexExpected][0] > actual[indexActual][1])
//			{
//				
//			}
//			//case 1: expected interval is on the left of actual
//			else if(expected[indexExpected][1] < actual[indexActual][0])
//			{
//				
//			}
			
			
			
			
		}
		
		
		
//		return (double)numOverlaps/streamLength;
		return numOverlaps;
	}

}
