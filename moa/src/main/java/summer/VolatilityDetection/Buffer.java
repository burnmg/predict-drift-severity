/*
 * Buffer.java
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

package summer.VolatilityDetection;

public class Buffer {
	private double[] buffer;
	private int size;
	private int slidingIndex;
	private boolean isFull;

	private double total;

	public Buffer(int size) {
		this.buffer = new double[size];
		this.size = size;
		this.slidingIndex = 0;
		this.isFull = false;

		this.total = 0;
	}

	public double add(double value) {
		if (slidingIndex == size) {
			isFull = true;
			slidingIndex = 0;
		}

		double removed = buffer[slidingIndex];
		total -= removed;

		buffer[slidingIndex++] = value;
		total += value;

		if (isFull) {
			return removed;
		} else {
			return -1;
		}
	}

	public int getSlidingIndex() {
		return slidingIndex;
	}

	public double getMean() {
		if (isFull) {
			return total / size;
		} else {
			return total / slidingIndex;
		}
	}

	public Boolean isFull() {
		return isFull;
	}

	public void clear() {
		this.buffer = new double[size];
		this.slidingIndex = 0;
		this.isFull = false;

		this.total = 0;
	}

	public double[] getBuffer() {
		return buffer;
	}

	public double getStdev() {
		return calculateStdev(buffer, getMean());
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
}
