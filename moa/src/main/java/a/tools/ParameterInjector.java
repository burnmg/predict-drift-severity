package a.tools;

import moa.classifiers.a.other.CurrentVolatilityMeasure;

public class ParameterInjector
{

	public int getDecisionMode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public CurrentVolatilityMeasure getcurrentVolatilityMeasureObject() 
	{
		// TODO Auto-generated method stub
		return null;
	}


}

class ParameterInjectionException extends Exception
{
	public ParameterInjectionException(String msg)
	{
		super(msg);
	}
}

