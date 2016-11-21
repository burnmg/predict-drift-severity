package volatilityevaluation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import volatilityevaluation.LimitedBuffer;

public class TestLimitedBuffer
{

	@Test
	public void testExactSize()
	{
		LimitedBuffer buffer = new LimitedBuffer(5);
		for(int i=0;i<5;i++)
		{
			buffer.add(i);
		}
		
		double[] actualValues = new double[]{0,1,2,3,4};
		
		int i=0;
		for(Double item : buffer.getAllElements())
		{
			assertEquals(item, actualValues[i], 0.0001);
			i++;
		}
	}

	@Test
	public void testMoreElements()
	{
		LimitedBuffer buffer = new LimitedBuffer(5);
		for(int i=0;i<10;i++)
		{
			buffer.add(i);
		}
		
		double[] actualValues = new double[]{5,6,7,8,9};
		
		int i=0;
		for(Double item : buffer.getAllElements())
		{
			assertEquals(item, actualValues[i], 0.0001);
			i++;
		}
	}
	
	@Test
	public void testLessElements()
	{
		LimitedBuffer buffer = new LimitedBuffer(5);
		for(int i=0;i<3;i++)
		{
			buffer.add(i);
		}
		
		double[] actualValues = new double[]{0,1,2};
		
		int i=0;
		for(Double item : buffer.getAllElements())
		{
			assertEquals(item, actualValues[i], 0.0001);
			i++;
		}
		assertEquals(3, buffer.getAllElements().size());
	}
}
