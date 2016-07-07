package lin.test;

import static org.junit.Assert.*;

import java.util.function.IntPredicate;

import org.junit.Test;

import a.evaluator.EvaluateCorreteModeCoverage;

public class TestEvaluateCorreteModeCoverage
{
	
	EvaluateCorreteModeCoverage ev = new EvaluateCorreteModeCoverage();
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
		assertEquals(8, ev.evalutate(expected, actual));
	}
	@Test
	public void test2()
	{
		int[][] expected = 
			{
				{0,5,1},
				{6,10,2},
				{11,12,1},
				{13,17,2}
		};
		
		int[][] actual = 
			{
					{0,17,1},
			};
		assertEquals(8, ev.evalutate(expected, actual));
	}
	@Test
	public void test3()
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
		assertEquals(8, ev.evalutate(expected, actual));
	}

}
