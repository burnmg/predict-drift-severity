package lin.test;

import static org.junit.Assert.*;

import java.util.function.IntPredicate;

import org.junit.Test;

import a.evaluator.EvaluateCorreteModeCoverage;

public class TestEvaluateCorreteModeCoverage
{
	
	EvaluateCorreteModeCoverage ev = new EvaluateCorreteModeCoverage();
	@Test
	public void test()
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
		System.out.println(ev.evalutate(expected, actual));
	}

}
