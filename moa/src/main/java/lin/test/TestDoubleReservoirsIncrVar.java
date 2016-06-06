package lin.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import a.algorithms.DoubleReservoirs;
import java_cup.reduce_action;

public class TestDoubleReservoirsIncrVar
{

	DoubleReservoirs r = new DoubleReservoirs(100);
	@Test
	public void testIncrVar()
	{
		r.setInput(10);
		r.setInput(5);
		r.setInput(5);
		assertEquals(5.5555555555556, r.getVariance(), 0.001);

		r = new DoubleReservoirs(100);
		
		double[] data = 
			{
					10,5,5,5.7,10.6,510,5000
			};
		
		for(double d : data)
		{
			r.setInput(d);
		}
		assertEquals("Mean", 792.32857142857, r.getMean(), 0.001);
		assertEquals("variance:", 2980843.2706122, r.getVariance(), 0.001);
	}

}
