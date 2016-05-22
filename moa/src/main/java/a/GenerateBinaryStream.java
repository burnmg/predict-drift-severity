package a;

import inputstream.MultipleDriftBernoulliDistributionGenerator;;
public class GenerateBinaryStream
{

	public static void main(String[] args)
	{
		MultipleDriftBernoulliDistributionGenerator g = new MultipleDriftBernoulliDistributionGenerator();
		
		g.generateInput(1, 3, 3412, 3000, false, "binary");

	}

}
