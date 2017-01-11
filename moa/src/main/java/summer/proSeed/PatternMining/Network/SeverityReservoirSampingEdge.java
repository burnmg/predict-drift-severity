package summer.proSeed.PatternMining.Network;

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
		return this.samples.getReservoir();
	}
}
