package lin.test;

import static org.junit.Assert.*;

import org.junit.Test;

import volatilityevaluation.LimitedBuffer;

public class TestBufferMax
{
	
	@Test
	public void test1()
	{
		LimitedBuffer buffer = new LimitedBuffer(5);
		double[] values = new double[]{2,3,-1,2.7, 1,6};
		for(int i=0;i<values.length;i++)
		{
			buffer.add(values[i]);
		}
		assertEquals(6, buffer.getMax(), 0.000001);
	}
	@Test
	public void test2()
	{
		LimitedBuffer buffer = new LimitedBuffer(5);
		double[] values = new double[]{2,3,-1,2.7, 1,6,2,3,-1,2.7,1,6,21.2,3,-1,2.7,1,60.23,12};
		for(int i=0;i<values.length;i++)
		{
			buffer.add(values[i]);
		}
		assertEquals(60.23, buffer.getMax(), 0.000001);
	}
	

}
