package a;

import java.awt.Frame;

import moa.DoTask;

public class GenerateData
{

	public static void main(String[] args)
	{
		String[] tasks =
		{ "WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l 999999 -d 1 -c 4) -f /Users/rl/Desktop/output/b.arff",
				"WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l 999999 -d 2 -c 4) -f /Users/rl/Desktop/output/b.arff -h -c" };

		for (String task : tasks)
		{
			String[] p = new String[1];
			p[0] = task;
			DoTask.main(p);
		}

	}

}
