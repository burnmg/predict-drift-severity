package a.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test
{
	public static void main(String[] args)
	{
		try
		{
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader("/Users/rl/789/Results/10,100,10,100.arff/volSwitchIntervalDesc.csv"));
			System.out.println(bReader.readLine());

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}


	}

}
