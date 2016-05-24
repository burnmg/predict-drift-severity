package archive;

import java.util.Random;

public class GenerateGaussianData
{
	public static void main(String[] args)
	{
		Random random = new Random();
		double[] randomVariable = new double[200];
		
		for(int i=0;i<200;i++)
		{
			randomVariable[i] = random.nextGaussian()*100; 
			System.out.println(randomVariable[i]);
		}
		System.out.println("******");
		System.out.println(std(randomVariable));
	}
	
	public static double mean(double[] dataset)
	{
		double sum = 0;
		for(int i=0;i<dataset.length;i++)
		{
			sum += dataset[i]; 
		}
		
		return sum/dataset.length;
	}
	
	public static double std(double[] dataset)
	{
		double mean = mean(dataset);
		double sumVar = 0;
		for(int i=0;i<dataset.length;i++)
		{
			 sumVar += (dataset[i] - mean)*(dataset[i] - mean); 
		}
		
		return Math.sqrt(sumVar/(dataset.length-1));
	}
}
