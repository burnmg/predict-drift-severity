/*


 * 
 * ProbabilisticNetwork.java
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
 * 
 */

package summer.proSeed.PatternMining.Network;

import java.util.ArrayList;
import java.util.Collections;

import summer.proSeed.PatternMining.IndexedPattern;
import summer.proSeed.PatternMining.PatternTransition;

public class ProbabilisticNetwork {

	private static final int EACH_EDGE_SIZE = 100;
	
	private double[][] patternNetwork;
	private SeveritySamplingEdgeInterface[][] edges;

	private int numberOfPatterns = 0;
	private int previousPatternIndex = -1;

	private int patternNetworkSize = 0;
	private final int DEFAULT_PATTERN_NETWORK_SIZE = 100;
	
	private void initiateEdges(int patternNetworkSize)
	{
		edges = new SeverityReservoirSampingEdge[patternNetworkSize][patternNetworkSize];
		
		/*
		for(int i=0;i<edges.length;i++)
		{
			for(int j=0;j<edges[0].length;j++)
			{
				edges[i][j] = new SeverityReservoirSampingEdge(100);
			}
		}
		*/
		
	}

	/**
	 * construct a network with a DEFAULT size of 100
	 */
	public ProbabilisticNetwork() {
		// construct a default new network
		this.patternNetworkSize = this.DEFAULT_PATTERN_NETWORK_SIZE;
		patternNetwork = new double[patternNetworkSize][patternNetworkSize];
		
		initiateEdges(patternNetworkSize);
	}

	/**
	 * construct a network with a specified size
	 *
	 * @param size
	 *            size of network
	 */
	public ProbabilisticNetwork(int size) {
		// construct a new network
		this.patternNetworkSize = size;
		patternNetwork = new double[patternNetworkSize][patternNetworkSize];
		
		initiateEdges(patternNetworkSize);
	}

	/**
	 * construct a network from specified transition probabilities
	 *
	 * @param network
	 *            transition probabilities of the network
	 */
	public ProbabilisticNetwork(double[][] network) {
		this.patternNetworkSize = network.length;
		this.numberOfPatterns = network.length;
		this.patternNetwork = network;
		
		initiateEdges(patternNetworkSize);
	}

	public int getNumberOfPatterns() {
		return this.numberOfPatterns;
	}

	/**
	 * gets the index of the previous inserted pattern
	 *
	 * @return pattern index
	 */
	public int getPreviousPatternIndex() {
		return this.previousPatternIndex;
	}

	/**
	 * sets the index of the previous pattern
	 *
	 * @param index
	 *            index of previous pattern
	 */
	public void setPreviousPatternIndex(int index) {
		this.previousPatternIndex = index;
	}

	public void setNumberOfPatterns(int number) {
		this.numberOfPatterns = number;
	}

	/**
	 *
	 * @param patternIndex
	 *            index of pattern to be updated
	 */
	public void incrementTransition(int patternIndex) {
		if (previousPatternIndex == -1) {
			// no previous pattern
		} else {
			// update probabilities of the previous pattern
			patternNetwork[previousPatternIndex][patternIndex] += 1;
		}
	}

	/**
	 * resets the probabilities for pattern at a specified index
	 *
	 * @param index
	 *            index of pattern in network
	 */
	public void resetNetworkForPattern(int index) {
		for (int i = 0; i < this.patternNetworkSize; i++) {
			// reset row 
			this.patternNetwork[index][i] = 0;
			// reset column 
			this.patternNetwork[i][index] = 0;
		}
	}

	/**
	 * compares the true network with the detector's network
	 * 
	 * @param otherNetwork
	 *            detector's network
	 * @return true positives at index 0, false positives (extra transitions) at
	 *         index 1
	 */
	public int[] compareTo(ProbabilisticNetwork otherNetwork) {
		int[] res = new int[2];
		int tP = 0;
		int fP = 0;

		double[][] otherTransitions = otherNetwork.getNetwork();
		for (int i = 0; i < this.numberOfPatterns; i++) {
			for (int j = 0; j < this.numberOfPatterns; j++) {
				if (otherTransitions[i][j] >= this.patternNetwork[i][j]) {
					fP += otherTransitions[i][j] - this.patternNetwork[i][j];
					tP += this.patternNetwork[i][j];
				} else {
					tP += otherTransitions[i][j];
				}
			}
		}

		res[0] = tP;
		res[1] = fP;
		return res;
	}

	public ProbabilisticNetwork compress() {
		int count = 0;
		int[] rows = new int[this.numberOfPatterns];
		for (int i = 0; i < this.numberOfPatterns; i++) {
			int sum = 0;
			for (int j = 0; j < this.numberOfPatterns; j++) {
				sum += this.patternNetwork[i][j];
			}
			if (sum > 5) {
				rows[count] = i;
				count++;
			}
		}

		double[][] compressedNet = new double[count][count];
		for (int i = 0; i < count; i++) {
			int rowNumber = rows[i];
			for (int j = 0; j < count; j++) {
				int colNumber = rows[j];
				compressedNet[i][j] = this.patternNetwork[rowNumber][colNumber];
			}
		}

		return new ProbabilisticNetwork(compressedNet);
	}

	public double[][] getNetwork() {
		return this.patternNetwork;
	}

	public double[][] getNetworkProbabilities() {
		double[][] probs = new double[this.numberOfPatterns][this.numberOfPatterns];

		for (int i = 0; i < probs.length; i++) {
			double sum = 0;
			for (int j = 0; j < probs[i].length; j++) {
				sum += this.patternNetwork[i][j];
			}
			for (int j = 0; j < probs[i].length; j++) {
				probs[i][j] = this.patternNetwork[i][j] / sum;
			}
		}

		return probs;
	}

	public int getSumTransitions() {
		int sum = 0;
		for (int i = 0; i < numberOfPatterns; i++) {
			for (int j = 0; j < numberOfPatterns; j++) {
				sum += this.patternNetwork[i][j];
			}
		}
		return sum;
	}

	public String getNetworkString() {
		System.out.println("writing network\n");
		String result = "Generated Network\n";
		for (int i = 0; i < numberOfPatterns; i++) {
			for (int j = 0; j < numberOfPatterns; j++) {
				if (i == j && this.patternNetwork[i][j] != 0) {
					System.out.println("i " + i + ", " + j);
					System.out.println("value " + patternNetwork[i][j]);
				}
				result = result + this.patternNetwork[i][j] + "\t";
			}
			result = result + "\n";
		}
		return result;
	}

	public String getNetworkProbabilitiesString() {
		String result = "Generated Network\n";
		double[][] generatedNetwork = getNetworkProbabilities();
		for (int i = 0; i < generatedNetwork.length; i++) {
			for (int j = 0; j < generatedNetwork[i].length; j++) {
				result = result + generatedNetwork[i][j] + "\t";
			}
			result = result + "\n";
		}
		return result;
	}

	public double[][] sortBy(IndexedPattern[] patternList) throws IllegalArgumentException {

		if (patternList.length != numberOfPatterns) {
			throw new IllegalArgumentException("Pattern list size and network size mismatch.");
		}

		double[][] unsortedNetwork = this.patternNetwork;
		double[][] sortedNetwork = new double[numberOfPatterns][numberOfPatterns];

		// sort by pattern index list
		for (int i = 0; i < numberOfPatterns; i++) {
			for (int j = 0; j < numberOfPatterns; j++) {
				sortedNetwork[i][j] = unsortedNetwork[patternList[i].getIndex()][patternList[j].getIndex()];
			}
		}

		return sortedNetwork;
	}

	public void decrementTransition(int row, int col) {
		this.patternNetwork[row][col] = this.patternNetwork[row][col] - 1;
	}

	public void delete(int row) {
		for (int i = row; i < numberOfPatterns - 1; i++) {
			for (int col = 0; col < numberOfPatterns; col++) {
				this.patternNetwork[i][col] = this.patternNetwork[i + 1][col];
			}
		}

		for (int c = row; c < numberOfPatterns - 1; c++) {
			for (int r = 0; r < numberOfPatterns; r++) {
				this.patternNetwork[r][c] = this.patternNetwork[r][c + 1];
			}
		}
		numberOfPatterns--;
	}

	/**
	 * merges two patterns by their indices (second pattern is removed and
	 * merges with first pattern)
	 *
	 * @param first
	 *            index of first pattern
	 * @param second
	 *            index of second pattern
	 */
	// TODO edge
	public void merge(int first, int second) {

		for (int i = 0; i < patternNetworkSize; i++) {
			// update row 
			patternNetwork[first][i] += patternNetwork[second][i];
			// update column 
			patternNetwork[i][first] += patternNetwork[i][second];
			// This merge can be used in edges merge.
		}

		patternNetwork[first][first] += patternNetwork[second][second]; // modified 18/12/15
		patternNetwork[first][second] = 0;
		patternNetwork[second][first] = 0;

		// remove second pattern row and column
		for (int i = second + 1; i < numberOfPatterns; i++) {
			for (int index = 0; index < numberOfPatterns; index++) {
				// shift rows to left
				patternNetwork[index][i - 1] = patternNetwork[index][i];
			}
		}

		for (int i = second + 1; i < numberOfPatterns; i++) {
			for (int index = 0; index < numberOfPatterns; index++) {
				// shift columns upwards
				patternNetwork[i - 1][index] = patternNetwork[i][index];
			}
		}

		numberOfPatterns--;

		for (int index = 0; index < numberOfPatterns; index++) {
			// reset extra columns and rows
			patternNetwork[numberOfPatterns][index] = 0;
			patternNetwork[index][numberOfPatterns] = 0;
		}

	}

	public ArrayList<PatternTransition> getTopKTransitionIndices(int patternIndex, int k) {
		ArrayList<PatternTransition> transitionList = new ArrayList<PatternTransition>();
		int sum = 0;
		for (int i = 0; i < numberOfPatterns; i++) {
			sum += patternNetwork[patternIndex][i];
		}

		if (sum == 0) {
			return null;
		}

		for (int i = 0; i < numberOfPatterns; i++) {
			if (patternNetwork[patternIndex][i] > 0) {
				double prob = patternNetwork[patternIndex][i] / (double) sum;
				transitionList.add(new PatternTransition(i, prob));
			}
		}

		Collections.sort(transitionList); // sort descending order

		int numInList = 0;
		ArrayList<PatternTransition> freqList = new ArrayList<PatternTransition>();
		for (int i = 0; i < transitionList.size(); i++) {
			if (numInList < k) {
				freqList.add(transitionList.get(i));
				numInList++;
			} else {
				break;
			}
		}

		return freqList;
	}

	public void addSeverityEdge(int from, int to, double[] severityData)
	{
		if(edges[from][to]==null) edges[from][to] = new SeverityReservoirSampingEdge(EACH_EDGE_SIZE);
		edges[from][to].addSamples(severityData);
	}
	
	public SeveritySamplingEdgeInterface[][] getEdges()
	{
		return this.edges;
	}

}
