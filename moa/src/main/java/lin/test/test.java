package lin.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cutpointdetection.PHT;
import moa.classifiers.core.driftdetection.ADWIN;

public class test
{

	public static void main(String[] args) throws IOException
	{
		
		testPHT();
	}
	
	public static void testPHT() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("/Users/rl/789/test/old case/low_high/vol copy.csv"));
		PHT pht = new PHT(0.1);
		pht.setMagnitudeThreshold(1000);
		reader.readLine();
		String line = "";
		
		int i = 1;
		while((line=reader.readLine())!=null)
		{
			//System.out.println(line);
			
			String[] strings = line.split(",");
			
			float vol = Float.parseFloat(strings[1]);
			System.out.print((i++)+ " "+vol);
			if(pht.setInput(vol))
			{
				System.out.println(" switch");
			}else
			{
				System.out.println("");
			}
		}
	}
	
	public static void testADWIN() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("/Users/rl/789/test/old case/low_high/vol copy.csv"));
		ADWIN adwin = new ADWIN(0.1);
		reader.readLine();
		String line = "";
		
		int i = 1;
		while((line=reader.readLine())!=null)
		{
			//System.out.println(line);
			
			String[] strings = line.split(",");
			
			float vol = Float.parseFloat(strings[1]);
			System.out.print((i++)+ " "+vol);
			if(adwin.setInput(vol))
			{
				System.out.println(" switch");
			}else
			{
				System.out.println("");
			}
		}
	}

}
