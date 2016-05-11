package a;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class test
{

	public static void main(String[] args)
	{
		Random driftRandom = new Random();
		double gaussianRandom = driftRandom.nextGaussian();
		
		
		// limit the range of gaussianRandom within [-0.9, 0.9]. 
		for(int i=0;i<10;i++)
		{
			System.out.println(driftRandom.nextGaussian()*0.05);
		}
	}

}
