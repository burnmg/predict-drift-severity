package a.evaluator;

public class EvaluateCorreteModeCoverage
{
	/**
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
	public double evalutate(int[][] expected, int[][] actual)
	{

		
		int indexExpected = 0; 
		int indexActual = 0;
		
		while(indexExpected < expected.length)
		{
			
			
			
			//case 1. 
			if(expected[indexExpected][0] >= actual[indexActual][0] && expected[indexExpected][0] <= actual[indexActual][1])
			{
				if(expected[indexExpected][2] == actual[indexActual][2])
				{
					//shrink expected head.
					int actualHead = expected[indexExpected][0];
					
				}

				indexActual++;
			}
			// case 2
			else if(expected[indexExpected][0] <= actual[indexActual][0] && expected[indexExpected][1] >= actual[indexActual][0])
			{
				
				indexExpected++;
			}
			// case 3
			else if(expected[indexExpected][0] <= actual[indexActual][0] && expected[indexExpected][1] >= actual[indexActual][1])
			{
				
				indexActual++;
			}
			// case 4
			else if(expected[indexExpected][0] > actual[indexActual][0] && expected[indexExpected][1] < actual[indexActual][1])
			{
				indexExpected++;
			}
			else
			{
				
			}
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
		
		
		
		return 0;
	}

}
