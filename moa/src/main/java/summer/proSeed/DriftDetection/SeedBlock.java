/*
 * SeedBlock.java
 * Author: David T.J. Huang - The University of Auckland
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 * 
 */

package summer.proSeed.DriftDetection;

public class SeedBlock
{
	private SeedBlock next;
	private SeedBlock previous;

	private int blockSize;
	private double total;
	private double variance;
	private int itemCount;

	public SeedBlock(int blockSize)
	{
		this.next = null;
		this.previous = null;
		this.blockSize = blockSize;

		this.total = 0;
		this.variance = 0;
		this.itemCount = 0;
	}

	public SeedBlock(SeedBlock block)
	{
		this.next = block.getNext();
		this.previous = block.getPrevious();
		this.blockSize = block.blockSize;

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

		itemCount++;
		total += value;
	}

	public boolean isFull()
	{
		if (itemCount == blockSize)

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