package summer.proSeed.PatternMining;

public interface StreamGenerator
{

	double generateNext();

	void addDrift(double driftSeverity);

	public void setSeed(int seed);
}
