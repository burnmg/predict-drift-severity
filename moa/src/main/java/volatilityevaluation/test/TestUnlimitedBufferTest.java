package volatilityevaluation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import volatilityevaluation.LimitedBuffer;
import volatilityevaluation.UnlimitedBuffer;

public class TestUnlimitedBufferTest
{
	@Test
	public void testExactSize()
	{
		UnlimitedBuffer buffer = new UnlimitedBuffer(5);
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
		UnlimitedBuffer buffer = new UnlimitedBuffer(5);
		for(int i=0;i<10;i++)
		{
			buffer.add(i);
		}
		
		double[] actualValues = new double[]{0,1,2,3,4,5,6,7,8,9};
		
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
		UnlimitedBuffer buffer = new UnlimitedBuffer(5);
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
	
	@Test
	public void testGetMean()
	{
		UnlimitedBuffer buffer = new UnlimitedBuffer(5);
		buffer.add(1);
		buffer.add(11);
		buffer.add(5);
		
		double expected = 5.66667;
		
		assertEquals(expected, buffer.getMean(), 0.0001);
	}

}
