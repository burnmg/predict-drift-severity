package summer.main;

import java.util.Random;

import org.apache.commons.math3.analysis.function.Max;

public class Lab
{

	public static void main(String args[])
	{
		Random r = new Random();
		if(Double.POSITIVE_INFINITY>Double.MAX_VALUE)
		{
			System.out.println(r.nextGaussian()+100);
		}
	}
}
