package summer.proSeed.PatternMining.Network;

import java.util.ArrayList;

import summer.proSeed.VolatilityDetection.Reservoir;

public class SeverityReservoirSampingEdge implements SeveritySamplingEdgeInterface
{
	Reservoir samples; 
	
	public SeverityReservoirSampingEdge(int size)
	{
		this.samples = new Reservoir(size);
	}
	
	@Override
	public void addSamples(double[] newSamples)
	{
		for(double sample : newSamples)
		{
			this.samples.addElement(sample);
		}
	}

	@Override
	public double[] getSamples()
	{
		ArrayList<Double> list = new ArrayList<Double>();
		double[] input = this.samples.getReservoir();
		for(int i=0;i<input.length;i++)
		{
			if(Math.abs(input[i]-0)>0.001)
			{
				list.add(input[i]);
			}
		}
		double[] output = new double[list.size()];
		for(int i=0;i<output.length;i++)
		{
			output[i] = list.get(i);
		}
		
		return output;
	}

	@Override
	public void clear()
	{
		this.samples.clear();
	}
	
	public void isClear()
	{
		this.samples.isClear();
	}

	@Override
	public double getMean()
	{
		return this.samples.getReservoirMean();
	}

	@Override
	public int getSampleCount()
	{
		
		return this.samples.getCount();
	}
}
