package volatilityevaluation;

import java.util.ArrayList;
import java.util.List;

public class UnlimitedBuffer implements BufferInterface
{
	private ArrayList<Double> list;
	private int suggestedSize;

	public UnlimitedBuffer()
	{
		suggestedSize = 0;
		list = new ArrayList<Double>();
	}
	
	public UnlimitedBuffer(int suggestedSize)
	{
		this.suggestedSize = suggestedSize;
		list = new ArrayList<Double>(suggestedSize);
	}
	
	@Override
	public double add(double inputValue)
	{
		list.add(inputValue);
		return 0;
	}

	@Override
	public double getMean()
	{
		if(list.size()==0) return 0;
		
		double sum = 0;
		for(Double item : list)
		{
			sum += item;
		}
		return sum/list.size();
	}

	@Override
	public int size()
	{
		return list.size();
	}

	@Override
	public void clear()
	{
		list = new ArrayList<Double>(suggestedSize);	
	}

	@Override
	public void addAll(BufferInterface warningBuffer)
	{
		list.addAll(warningBuffer.getAllElements());
		
	}

	@Override
	public List<Double> getAllElements()
	{
		return list;
	}

}
