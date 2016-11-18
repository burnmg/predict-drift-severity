package summer.originalSeed;

/*
This source code is provided "as is" without warranty of any kind. 
All warranties, expresss and implied, including without limitation, 
any implied warranties of merchantability, fitness for a particular 
purpose or noninfringement are further disclaimed.

In no event shall the authors be liable for any indirect, incidental, 
special, punitive, or consequential damages, or damages for loss of 
profits, revenue, data or data use, incurred by you or any third party, 
whether in an action in contract or tort, even if the authors have been 
advised of the possibility of such damages.
*/

public class SeedBlock
{
    private SeedBlock next;
    private SeedBlock previous;

    // private double[] items;
    private int blockSize;
    private double total;
    private double variance;
    private int itemCount;

    public SeedBlock(int blockSize)
    {
	this.next = null;
	this.previous = null;
	this.blockSize = blockSize;

	// this.items = new double[blockSize];
	this.total = 0;
	this.variance = 0;
	this.itemCount = 0;
    }

    public SeedBlock(SeedBlock block)
    {
	this.next = block.getNext();
	this.previous = block.getPrevious();
	this.blockSize = block.blockSize;

	// this.items = new double[blockSize];
	this.total = block.total;
	this.variance = block.variance;
	this.itemCount = block.itemCount;
    }

    public void setNext(SeedBlock next)
    {
	this.next = next;
    }

    public SeedBlock getNext()
    {
	return this.next;
    }

    public void setPrevious(SeedBlock previous)
    {
	this.previous = previous;
    }

    public SeedBlock getPrevious()
    {
	return this.previous;
    }

    public int getBlockSize()
    {
	return blockSize;
    }

    public void setBlockSize(int blockSize)
    {
	this.blockSize = blockSize;
    }

    public void add(double value)
    {
	// items[itemCount++] = value;
	itemCount++;
	total += value;
    }

    public boolean isFull()
    {
	if (itemCount == blockSize)
	// if(itemCount == items.length)
	{
	    return true;
	} else
	{
	    return false;
	}
    }

    public double getMean()
    {
	return this.total / this.itemCount;
    }

    public void setTotal(double value)
    {
	this.total = value;
    }

    public double getTotal()
    {
	return this.total;
    }

    public void setItemCount(int value)
    {
	this.itemCount = value;
    }

    public int getItemCount()
    {
	return this.itemCount;
    }

    public void setVariance(double value)
    {
	this.variance = value;
    }

    public double getVariance()
    {
	return this.variance;
    }

}