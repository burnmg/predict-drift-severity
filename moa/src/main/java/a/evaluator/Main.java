package a.evaluator;

import a.streams.GenerateDriftData;

public class Main
{
	public static void main(String args[]) throws Exception
	{
		GenerateDriftData generateDriftData = new GenerateDriftData();
		generateDriftData.main(args);
		generateDriftData = null;
		EvaluateMain.main(args);
	}
}
