package lin.test;

import static org.junit.Assert.*;

import org.junit.Test;

import a.evaluator.CorreteModeCoverageEvaluator;

public class TestEvalutateLowAndHigh
{

	CorreteModeCoverageEvaluator ev = new CorreteModeCoverageEvaluator();
	
	@Test
	public void test1()
	{
		int[][] expected = 
			{
					{0,17,1},
		};
		
		int[][] actual = 
			{
				{0,5,1},
				{6,10,2},
				{11,12,1},
				{13,17,2}
			};
		
		
		double[] results = ev.evalutateLowAndHigh(expected, actual);
		assertEquals(8.0/18, results[0], 0.001);
		
		
	}
	@Test
	public void test2()
	{
		int[][] expected = 
			{
				{0,5,1},
				{6,10,2},

		};
		
		int[][] actual = 
			{
					{0,1,1},
					{2,3,2},
					{4,6,1},
					{7,10,2}
			};
		
		double[] results = ev.evalutateLowAndHigh(expected, actual);
		
		assertEquals(8.0/18, results[0], 0.001);
		assertEquals(8.0/18, results[1], 0.001);
		
	}
	

}
