package a.evaluator;

import a.streams.GenerateDriftData;

public class Main
{
	public static void main(String args[]) throws Exception
	{
		EvaluateMain.main(args); // order changed. 
		GenerateDriftData.main(args);
		
	}
}
