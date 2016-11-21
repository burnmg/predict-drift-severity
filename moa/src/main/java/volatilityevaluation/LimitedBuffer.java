package volatilityevaluation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.formula.functions.IfFunc;

public class LimitedBuffer implements BufferInterface
{
	private double[] buffer;
	private int size;
	private int slidingIndex;
	private boolean isFull;

	private double total;

	public LimitedBuffer(int size)
	{
		this.buffer = new double[size];
		this.size = size;
		this.slidingIndex = 0;
		this.isFull = false;

		this.total = 0;
	}
	
	@Override
	public double add(double value)
	{
		if (slidingIndex == size)
		{
			isFull = true;
			slidingIndex = 0;
		}

		double removed = buffer[slidingIndex];
		total -= removed;

		buffer[slidingIndex++] = value;
		total += value;

		if (isFull)
		{
			return removed;
		} else
		{
			return -1;
		}
	}
	
	@Override
	public double getMean()
	{
		if (isFull)
		{
			return total / size;
		} else
		{
			return total / slidingIndex;
		}
	}
	
	public double getMax()
	{
		double max = buffer[0];
		for(int i=1;i<buffer.length;i++)
		{
			if(buffer[i] > max)
			{
				max = buffer[i];
			}
		}
		return max;
	}

	public Boolean isFull()
	{
		return isFull;
	}

	@Override
	public void clear()
	{
		this.buffer = new double[size];
		this.slidingIndex = 0;
		this.isFull = false;

		this.total = 0;
	}

	public double[] getBuffer()
	{
		return buffer;
	}

	public double getStdev()
	{
		return calculateStdev(buffer, getMean());
	}

	public double calculateStdev(double[] times, double mean)
	{
		double sum = 0;
		int count = 0;
		for (double d : times)
		{
			if (d > 0)
			{
				count++;
				sum += Math.pow(d - mean, 2);
			}
		}
		return Math.sqrt(sum / count);
	}

	@Override
	public int size()
	{
		if(isFull)
		{
			return size;
		}
		else
		{
			return slidingIndex;
		}
		
	}

	@Override
	public List<Double> getAllElements()
	{
		LinkedList<Double> newList = new LinkedList<Double>();
		int index = slidingIndex - 1;
		if(index<0) return newList;
		
		if(isFull)
		{
			for(int i=0;i<buffer.length;i++)
			{
				if(index==-1) index = buffer.length-1;
				newList.addFirst(buffer[index]);
				index--;
			}
		}
		else
		{
			for(int i=index;i>=0;i--)
			{
				newList.addFirst(buffer[i]);
			}
		}
			
		return newList;
	}

	@Override
	public void addAll(BufferInterface otherBuffer)
	{
		List<Double> otherList  = otherBuffer.getAllElements();
		for(Double item : otherList)
		{
			this.add(item);
		}
		
	}
}
