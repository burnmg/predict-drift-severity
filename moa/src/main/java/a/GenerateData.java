package a;

import java.awt.Frame;

import moa.DoTask;

public class GenerateData
{

	public static void main(String[] args)
	{
		String[] tasks =
		{ "WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l 1000000 -d 5 -c 4 -f /Users/rl/789/Streams/low_high/dump.csv) -f /Users/rl/789/Streams/low_high/low_high.arff",
			"WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l 1000000 -d 50 -c 4 -f /Users/rl/789/Streams/low_high/dump.csv) -f /Users/rl/789/Streams/low_high/low_high.arff -h -c"
		};
		for (String task : tasks)
		{
			String[] p = new String[1];
			p[0] = task;
			DoTask.main(p);
		}

	}

}
