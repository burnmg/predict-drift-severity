package a.evaluator;

import a.streams.GenerateDriftData;

public class CMD
{
	public static void main(String args[]) throws Exception
	{
		GenerateDriftData.main(args);
		EvaluateMain.main(args);
	}
}
