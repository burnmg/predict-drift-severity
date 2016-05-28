package a.algorithms;

public class DoubleReservoirs
{
	public Reservoir highReservoir;
	public Reservoir lowReservoir;

	public DoubleReservoirs(int size)
	{
		this.highReservoir = new Reservoir(size);
		this.lowReservoir = new Reservoir(size);
	}

	public void setInput(double input)
	{
		if (highReservoir.getElementNum() == 0)
		{
			highReservoir.addElement(input);
			return;
		}
		if (lowReservoir.getElementNum() == 0)
		{
			lowReservoir.addElement(input);
			return;
		}

		double threshold = getMean();

		if (input > threshold)
		{
			highReservoir.addElement(input);
		} else
		{
			lowReservoir.addElement(input);
		}

	}

	public double getMean()
	{
		if (highReservoir.size() == 0 && lowReservoir.size() == 0)
			return 0;

		// Can be optimised if needed.
		return (highReservoir.getTotal() + lowReservoir.getTotal()) / (highReservoir.getElementNum() + lowReservoir.getElementNum());
	}

}
