package summer.main;

import java.util.Random;

public class Lab
{

	public static void main(String args[])
	{
		Random r = new Random();
		for(int i=0;i<100;i++)
		{
			System.out.println(r.nextGaussian()*10+100);
		}
	}
}