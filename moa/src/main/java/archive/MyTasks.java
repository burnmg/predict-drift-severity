package archive;

import moa.DoTask;

public class MyTasks
{
	final 

	public static void main(String[] args)
	{
		String[] tasks = streams();
		for (int i = 0; i < tasks.length; i++)
		{
			System.out.println("*****Task*****: " + i);
			String[] p = new String[1];
			p[0] = tasks[i];
			DoTask.main(p);
		}

	}

	public static String[] test()
	{
		String[] tasks =
		{

				//test HT in 12500-10m limit memory
				"EvaluatePrequential -l (a.HoeffdingTreeADWIN -m 33554) -s (ArffFileStream -f /Users/rl/789/Streams/12500-10m.arff) -f 1000 -q 1000 -d /Users/rl/789/test/12500-10m-limit-m/HT/res.csv",

				//test HAT in 12500-10m limit memory
				"EvaluatePrequential -l (trees.HoeffdingAdaptiveTree -m 33554) -s (ArffFileStream -f /Users/rl/789/Streams/12500-10m.arff) -f 1000 -q 1000 -d /Users/rl/789/test/12500-10m-limit-m/HAT/res.csv",
		
		
		};
		return tasks;
	}

	public static String[] streams()
	{
		String[] tasks =
		{
				//12500-10m
				"WriteStreamToARFFFile2 -s (MultipleConceptDriftStreamGenerator -l 5000000 -d 12500 -c 4 -w 600) -f /Users/rl/789/Streams/sample.arff",

		};
		return tasks;
	}
	private static String[] myEvaluatePrequential()
	{
		String[] tasks =
		{
				"EvaluatePrequential -l a.HoeffdingTreeADWIN -s (ArffFileStream -f /Users/rl/789/Streams/regularchangstream.arff/regularchangstream.arff) -f 1000 -q 1000 -d /Users/rl/789/test/a.HoeffdingTreeADWINres.csv",

		};
		return tasks;
	}

}
