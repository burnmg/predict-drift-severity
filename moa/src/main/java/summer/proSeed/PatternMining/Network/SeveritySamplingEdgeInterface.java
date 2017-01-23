package summer.proSeed.PatternMining.Network;

public interface SeveritySamplingEdgeInterface
{
	public void addSamples(double[] newSamples);
	
	public double[] getSamples();

	public void clear();
	
	public double getMean();
	
	public int getSampleCount();

}
