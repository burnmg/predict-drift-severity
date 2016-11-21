package volatilityevaluation;

import java.util.List;

public interface BufferInterface
{
	public double add(double value);
	public double getMean();
	public int size();
	public void clear();
	public void addAll(BufferInterface warningBuffer);
	public List<Double> getAllElements();
}
