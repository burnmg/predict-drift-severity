package a.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java_cup.internal_error;
import moa.streams.ArffFileStream;

public class StreamCutter
{
	public void cut(String inputStreamPath, String volSwitchIntervalDescFile, String outputDirPath)
	{
		// input stream
		ArffFileStream inputStream = new ArffFileStream();
		inputStream.getOptions().resetToDefaults();
		inputStream.arffFileOption.setValue(inputStreamPath);
		inputStream.prepareForUse();
		
		// load switch point	
		BufferedReader bReader;
		ArrayList<int[]> cutpoints = new ArrayList<int[]>();
		try
		{
			bReader = new BufferedReader(new FileReader(volSwitchIntervalDescFile));
			bReader.readLine();
			String line = bReader.readLine();
			while(line!=null)
			{
				String[] read = line.split(",");
				int[] parseRead = new int[3];
				parseRead[0] = Integer.parseInt(read[0]);
				parseRead[1] = Integer.parseInt(read[1]);
				parseRead[2] = Integer.parseInt(read[2]);
				
				line = bReader.readLine();
			}
			
			bReader.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		
	}
}
