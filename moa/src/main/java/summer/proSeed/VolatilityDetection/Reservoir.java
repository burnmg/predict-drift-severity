/*
 * Reservoir.java
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

package summer.proSeed.VolatilityDetection;

import java.util.Random;

public class Reservoir {
	private int size;
	private double[] elements;
	private double elementTotal;
	private int eIndex;
	private Random rand;

	public Reservoir(int size) {
		this.size = size;
		this.elements = new double[size];
		elementTotal = 0;
		this.eIndex = 0;
		rand = new Random(50);
	}

	public void addElement(double inputValue) {
		if (eIndex < size) {
			elements[eIndex] = inputValue;
			elementTotal += inputValue;
			eIndex++;
		} else {
			int removeIndex = (int) (rand.nextDouble() * eIndex);
			elementTotal -= elements[removeIndex];
			elements[removeIndex] = inputValue;
			elementTotal += inputValue;
		}
	}

	public double getReservoirMean() {
		return elementTotal / eIndex;
	}

	public double getReservoirStdev() {
		double stdev = calculateStdev(elements, getReservoirMean());
		return stdev == 0 ? 0.00000000001 : stdev;
	}

	public int getCount() {
		return eIndex;
	}

	public boolean isFull() {
		if (eIndex == size) {
			return true;
		} else {
			return false;
		}
	}

	public double calculateStdev(double[] times, double mean) {
		double sum = 0;
		int count = 0;
		for (double d : times) {
			if (d > 0) {
				count++;
				sum += Math.pow(d - mean, 2);
			}
		}
		return Math.sqrt(sum / count);
	}

	public double[] getReservoir() {
		return elements;
	}

	public void clear() {
		this.elements = new double[size];
		elementTotal = 0;
		this.eIndex = 0;
	}
}
