package a.evaluator;

import a.streams.GenerateDriftData;

public class Main
{
	public static void main(String args[]) throws Exception
	{
		GenerateDriftData.main(args);
		System.gc();
		EvaluateMain.main(args);
	}
}
