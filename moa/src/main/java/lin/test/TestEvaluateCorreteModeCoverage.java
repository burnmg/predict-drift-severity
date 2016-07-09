package lin.test;

import static org.junit.Assert.*;

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
	
	@Test
	public void test4()
	{
		int[][] expected = 
			{
				{0,4,1},
				{5,10,2},

		};
		
		int[][] actual = 
			{
					{0,1,1},
					{2,6,2},
					{7,10,1}
			};
		assertEquals(4, ev.evalutate(expected, actual));
	}
	
	@Test
	public void test5()
	{
		int[][] expected = 
			{
				{0,1,1},
				{2,3,2},

		};
		
		int[][] actual = 
			{
					{0,1,1},
					{2,3,2}
			};
		assertEquals(4, ev.evalutate(expected, actual));
	}
	
	@Test
	public void test6()
	{
		int[][] expected = 
			{
				{0,0,1},
				{1,1,2},

		};
		
		int[][] actual = 
			{
					{0,1,1}
			};
		assertEquals(1, ev.evalutate(expected, actual));
	}

}
