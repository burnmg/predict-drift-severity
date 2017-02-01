/*
 * PatternReservoir.java
 * Author: Kylie Chen - The University of Auckland 
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

package summer.proSeed.PatternMining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.print.attribute.standard.Severity;

import summer.proSeed.PatternMining.Network.ProbabilisticNetwork;
import summer.proSeed.PatternMining.Network.SeveritySamplingEdgeInterface;

public class PatternReservoir
{
	private ProbabilisticNetwork network;
	private Pattern[] patternReservoir;
	private int patternReservoirSize = 0; // maximum size of pattern reservoir
	private final int DEFAULT_PATTERNSIZE = 100;

	private int numberOfPatterns = 0; // number of patterns stored in reservoir
	private int mergeParameter;

	// slope compression parameters below
	private double slopeCompression;
	private boolean compressed = false;

	private int beforePrevPatternIndex = -1;
	private int prevPatternIndex = -1;
	private int prevLength = -1;
	
	private int fromIndex = -1;

	public PatternReservoir()
	{
		this.patternReservoirSize = DEFAULT_PATTERNSIZE;
		this.patternReservoir = new Pattern[patternReservoirSize];
		this.network = new ProbabilisticNetwork(patternReservoirSize);
		new LinkedList<Object>();
	}
	
	// I am using this one
	public PatternReservoir(int size, int severitySampleSize)
	{
		this.patternReservoirSize = size;
		this.patternReservoir = new Pattern[patternReservoirSize];
		this.network = new ProbabilisticNetwork(patternReservoirSize, severitySampleSize);
		new LinkedList<Object>();
		
	}

	public PatternReservoir(int size)
	{
		this.patternReservoirSize = size;
		this.patternReservoir = new Pattern[patternReservoirSize];
		this.network = new ProbabilisticNetwork(patternReservoirSize);
		new LinkedList<Object>();
	}

	public void setSlopeCompression(double slope)
	{
		slopeCompression = slope;
	}

	public void setMergeParameter(int m)
	{
		mergeParameter = m;
	}

	public int getMaxSize()
	{
		return this.patternReservoirSize;
	}

	public int getSize()
	{
		return this.numberOfPatterns;
	}

	public ProbabilisticNetwork getNetwork()
	{
		return this.network;
	}

	public int getLatestPatternIndex()
	{
		return this.prevPatternIndex;
	}

	/**
	 * get a pattern at specified index in the reservoir
	 *
	 * @param index
	 *            of pattern to retrieve
	 * @return pattern at a specified index in the reservoir
	 */
	public Pattern getPatternAt(int index)
	{
		return patternReservoir[index];
	}

	public Pattern[] getPatterns()
	{
		Pattern[] patterns = new Pattern[numberOfPatterns];
		System.arraycopy(this.patternReservoir, 0, patterns, 0, patterns.length);
		return patterns;
	}

	public String getPatternsString()
	{
		Pattern[] patternRes = getPatterns();
		return getPatternStringOf(patternRes);
	}
	
	public String getSortedPatternString()
	{
		Pattern[] patternsRes = getSortedPatterns();
		return getPatternStringOf(patternsRes);
	}

	public String getPatternStringOf(Pattern[] patternRes)
	{
		String result = "Patterns\nIndex\tMean\tVariance\n";
		int i = 0;
		for (Pattern p : patternRes)
		{
			result = result + i + "\t" + p + "\n";
			i++;
		}
		return result;
	}

	/**
	 * replaces a pattern at specified index in reservoir with new pattern
	 *
	 * @param newPattern
	 *            new pattern to add
	 * @param index
	 *            index of old pattern in reservoir to replace
	 */
	public void replacePattern(Pattern newPattern, int index)
	{
		patternReservoir[index] = newPattern;
	}

	/**
	 * adds a pattern to non-full reservoir at the next available position
	 *
	 * @param newPattern
	 *            new pattern to add
	 */
	public void addPatternToReservoir(Pattern newPattern)
	{
		patternReservoir[numberOfPatterns] = newPattern;
	}

	/**
	 * finds index of a pattern in pattern reservoir
	 *
	 * @param patternToFind
	 *            pattern to find
	 * @return index of pattern in the reservoir, or -1 if no pattern found
	 */
	public int findPatternIndex(Pattern patternToFind)
	{
		for (int i = 0; i < numberOfPatterns; i++)
		{
			if (patternReservoir[i] == null)
			{
				System.out.println();
			}
			boolean patternFound = patternToFind.equals(patternReservoir[i]);
			if (patternFound)
			{
				return i;
			}
		}
		return -1;
	}

	/*
	 * public int addPattern(double[] patternData, int dataLength, int
	 * patternLength) { // return addPattern(patternData, dataLength,
	 * patternLength, 0.05); return addPattern(patternData, dataLength,
	 * patternLength); }
	 */

	/**
	 * attempts to add a new pattern to the pattern reservoir
	 *
	 * @param patternData
	 *            data of new current pattern
	 * @param dataLength
	 *            number of new data points of current pattern
	 * @param currentLength
	 *            is the length of time the current pattern persists
	 * @param patternDelta
	 *            delta for pattern matching
	 * @return index of newly inserted pattern
	 */
	public int addPattern(double[] patternData, int dataLength, int currentLength, double[] severityData)
	{

		compressed = false; // reset compression flag
		Pattern newPattern = new Pattern(patternData, dataLength);

		// currentPatternIndex is the new pattern added (or updated).
		// beforePrevPatternIndex < prevPatternIndex < currentPatternIndex
		int currentPatternIndex = findPatternIndex(newPattern);
		
		if(prevPatternIndex!=currentPatternIndex) fromIndex = currentPatternIndex;
		
		if (currentPatternIndex == -1)
		{ // pattern not found, so new pattern should be inserted

			if (numberOfPatterns < this.patternReservoirSize)
			{ // pattern reservoir is not full
				// add pattern to reservoir
				currentPatternIndex = numberOfPatterns; // update pattern index
				addPatternToReservoir(newPattern);
				numberOfPatterns++;
				this.network.setNumberOfPatterns(numberOfPatterns);
			} else
			{
				currentPatternIndex = findLowestWeight();
			}

			// reset network probabilities for new pattern
			this.network.resetNetworkForPattern(currentPatternIndex);
		} else
		{
			// pattern found, then update data
			this.patternReservoir[currentPatternIndex].addData(patternData, dataLength);
		}


		// add severity edge

		if(currentPatternIndex!=prevPatternIndex) fromIndex = prevPatternIndex;
		if(fromIndex!=-1) this.network.addSeverityEdge(fromIndex, currentPatternIndex, severityData);
		// compression check
		if (beforePrevPatternIndex != -1)
		{
			double prevSlope = (patternReservoir[prevPatternIndex].getMean()
					- patternReservoir[beforePrevPatternIndex].getMean()) / prevLength;
			double curSlope = (patternReservoir[currentPatternIndex].getMean()
					- patternReservoir[prevPatternIndex].getMean()) / currentLength;
			double slopeDiff = Math.abs(prevSlope - curSlope);

			if (slopeDiff < slopeCompression)
			{
				// compress previous pattern transition and update network TODO
				// deal with edges as well
				currentPatternIndex = compressTransition(beforePrevPatternIndex, prevPatternIndex, currentPatternIndex);
				compressed = true;
				prevLength = prevLength + currentLength; // when compressed the
															// length between
															// patterns is
															// increased
			} else
			{
				// update network
				this.network.incrementTransition(currentPatternIndex);
				compressed = false;
			}
		}


		
		if (prevPatternIndex != -1)
		{
			if (compressed == false)
			{
				beforePrevPatternIndex = prevPatternIndex; // update value of
															// pattern before
				prevLength = currentLength; // update length if no compression
											// occurred
			}
		}

		
		prevPatternIndex = currentPatternIndex; // update index of previous
												// pattern

		this.network.setPreviousPatternIndex(currentPatternIndex);

		return prevPatternIndex;

	}

	/**
	 * compresses two transitions into a single transition A -> B -> C
	 * 
	 * @param indexA
	 *            index of pattern before previous pattern (two steps before
	 *            current pattern)
	 * @param indexB
	 *            index of previous pattern (one step before current pattern)
	 * @param indexC
	 *            index of current pattern
	 * @return index of current pattern
	 */
	// TODO edge (leave it right now)
	private int compressTransition(int indexA, int indexB, int indexC)
	{
		// adjust length of pattern when compressing
		// compress transitions from a -> b -> c to a -> c
		if (this.patternReservoir[indexB].getWeight() <= 1)
		{
			this.network.mergeEdge(indexA, indexB);
			// add a -> c
			this.network.setPreviousPatternIndex(indexA);
			this.network.incrementTransition(indexC);
			

			
			// remove pattern b
			this.network.delete(indexB);
			delete(indexB); // deletes pattern b from pattern reservoir
			// update current pattern index
			
			if (indexB < indexC)
			{
				return indexC - 1; // pattern's index is shifted when b deleted
			} else
			{
				return indexC;
			}
		} else
		{
			// add a -> c
			this.network.setPreviousPatternIndex(indexA);
			this.network.incrementTransition(indexC);
			// remove transition a -> b
			this.network.decrementTransition(indexA, indexB);
			
			
			// return current pattern index
			return indexC;
		}
	}

	/**
	 * deletes a pattern from the pattern reservoir
	 * 
	 * @param row
	 *            index of pattern to delete
	 */
	private void delete(int row)
	{
		for (int i = row; i < numberOfPatterns - 1; i++)
		{
			this.patternReservoir[i] = this.patternReservoir[i + 1];
		}
		numberOfPatterns--;
	}

	private int deleteHighVariance()
	{
		ArrayList<Integer> deleteList = new ArrayList<Integer>();
		for (int i = 0; i < this.numberOfPatterns; i++)
		{
			if (this.patternReservoir[i].calcVariance() > 300)
			{
				deleteList.add(i);
			}
		}

		for (int i = 0; i < deleteList.size(); i++)
		{
			int deleteIndex = deleteList.get(i) - i;
			delete(deleteIndex);
			network.delete(deleteIndex);
		}

		return deleteList.size();
	}

	/**
	 * merges closest patterns until only k patterns remain
	 *
	 * @return current pattern index
	 */
	
	public int merge()
	{

		while (numberOfPatterns > mergeParameter)
		{
			int[] closestPatterns = getClosestPatternIndices();
			int i = closestPatterns[0];
			int j = closestPatterns[1];

			System.out.println("merging " + i + ", " + j);

			// merge pattern
			patternReservoir[i].merge(patternReservoir[j]);

			network.merge(i, j); // this also merges the edges
			updatePatternReservoir(i, j); // update pattern reservoir

			if (prevPatternIndex == j)
			{
				prevPatternIndex = i;
				this.network.setPreviousPatternIndex(prevPatternIndex);
			} else if (prevPatternIndex > j)
			{
				prevPatternIndex = prevPatternIndex - 1;
				this.network.setPreviousPatternIndex(prevPatternIndex);
			}

			numberOfPatterns--;

			this.network.setNumberOfPatterns(numberOfPatterns);

		}

		return prevPatternIndex;
	}

	private int[] getClosestPatternIndices()
	{
		int[] closestIndices = new int[2];
		double minD = Integer.MAX_VALUE;
		for (int i = 0; i < this.numberOfPatterns; i++)
		{
			for (int j = i + 1; j < this.numberOfPatterns; j++)
			{
				double d = patternReservoir[i].computeD(patternReservoir[j]);
				if (d < minD)
				{
					minD = d;
					closestIndices[0] = i;
					closestIndices[1] = j;
				}
			}
		}
		return closestIndices;
	}

	/**
	 * merge equal patterns
	 *
	 * @return true if merging was applied
	 */
	private boolean mergePatterns()
	{
		boolean merged = false;
		for (int i = 0; i < numberOfPatterns; i++)
		{
			Pattern pattern = patternReservoir[i];
			for (int j = i + 1; j < numberOfPatterns; j++)
			{
				Pattern otherPattern = patternReservoir[j];
				if (pattern.equals(otherPattern))
				{
					System.out.println("merging " + i + ", " + j);

					merged = true;
					pattern.merge(otherPattern);
					network.merge(i, j);
					updatePatternReservoir(i, j);

					numberOfPatterns--;
					this.network.setNumberOfPatterns(numberOfPatterns);

					if (prevPatternIndex == j)
					{
						prevPatternIndex = i;
						this.network.setPreviousPatternIndex(prevPatternIndex);
					} else if (prevPatternIndex > j)
					{
						prevPatternIndex = prevPatternIndex - 1;
						this.network.setPreviousPatternIndex(prevPatternIndex);
					}
				}
			}
		}
		return merged;
	}

	private void updatePatternReservoir(int first, int second)
	{
		Pattern[] updatedReservoir = new Pattern[patternReservoir.length];
		for (int i = 0; i < this.numberOfPatterns; i++)
		{
			if (i > second)
			{
				updatedReservoir[i - 1] = this.patternReservoir[i];
			} else if (i < second)
			{
				updatedReservoir[i] = this.patternReservoir[i];
			}
		}
		this.patternReservoir = updatedReservoir;
	}

	private IndexedPattern[] getIndexedCopyOfPatterns()
	{
		IndexedPattern[] copy = new IndexedPattern[numberOfPatterns];
		for (int i = 0; i < numberOfPatterns; i++)
		{
			Pattern p = this.patternReservoir[i];
			double mean = p.getMean();
			double variance = p.calcVariance();
			copy[i] = new IndexedPattern(mean, variance);
			copy[i].setIndex(i);
		}

		return copy;
	}

	// TODO edge
	public double[][] getSortedNetwork()
	{
		IndexedPattern[] indexedPatterns = getIndexedCopyOfPatterns();
		Arrays.sort(indexedPatterns);
		return this.network.sortProbabilityBy(indexedPatterns);
	}
	
	public SeveritySamplingEdgeInterface[][] getSortEdges()
	{
		IndexedPattern[] indexedPatterns = getIndexedCopyOfPatterns();
		Arrays.sort(indexedPatterns);
		return this.network.sortEdgesBy(indexedPatterns);
	}
	
	public SeveritySamplingEdgeInterface[][] getEdges()
	{
		return this.network.getEdges();
	}

	public IndexedPattern[] getSortedPatterns()
	{
		IndexedPattern[] indexedPatterns = getIndexedCopyOfPatterns();
		Arrays.sort(indexedPatterns);
		return indexedPatterns;
	}

	private int findLowestWeight()
	{
		int min = Integer.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < this.numberOfPatterns; i++)
		{
			int weight = this.patternReservoir[i].weight;
			if (weight < min)
			{
				min = weight;
				minIndex = i;
			}
		}
		return minIndex;
	}

	public boolean getCompression()
	{
		return compressed;
	}
}
