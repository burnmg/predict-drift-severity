package lin.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import a.algorithms.VolatilityMonitor;
import moa.classifiers.core.driftdetection.ADWIN;

public class TestVolatilityMonitor
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("binary"));
		VolatilityMonitor monitor = new VolatilityMonitor();
		// reader.readLine();
		String line = "";
		
		int i = 1;
		while((line=reader.readLine())!=null)
		{
			//System.out.println(line);
			
			float c = Float.parseFloat(line);
			int interval;
			interval = monitor.setInput(c);
			if(interval!=-1)
			{
				System.out.println(i+ " "+interval);
			}
			
			i++;
		}
		
		System.out.println("Test Done.");

	}
	
	public static void ADWIN()
	{
		
	}
}
