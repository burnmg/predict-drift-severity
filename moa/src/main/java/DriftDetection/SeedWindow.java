/*
 * SeedWindow.java
 * Authors: Kylie Chen - The University of Auckland
 * 			David T.J. Huang - The University of Auckland
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
 
package DriftDetection;

 /**
 * 
 * Proactive drift detection method as published in:
 * Kylie Chen, Yun Sing Koh, and Patricia Riddle. Proactive Drift Detection: Predicting Concept Drifts in Data Streams using Probabilistic Networks.  
 * In Proceedings of the International Joint Conference on Neural	Networks (IJCNN), 2016.
 * 
 * Usage: setInput(double)
 * 
 * @author Kylie Chen - The University of Auckland
 * @author David T.J. Huang - The University of Auckland
 * @version 2.0
 */
public class SeedWindow {
	
	private SeedBlock head;
	private SeedBlock tail;

	private int blockSize;
	private int width;
	private double total;
	private double variance;
	private int blockCount;

	private int DECAY_MODE = 1;
	private final int LINEAR_DECAY = 1;
	private final int EXPONENTIAL_DECAY = 2;

	private int COMPRESSION_MODE = 1;
	private final int FIXED_TERM = 1;
	private final int PARETO = 2;

	private int decayCompressionCount = 0;
	private int decayIteration = 1;
	private int paretoDecayTermSize = 200;
	private int paretoDecayDefaultTermSize = 200;
	private int linearFixedTermSize = 50;

	private double epsilonPrime = 0.0;
	private double alpha = 0.0;

	// Testing purpose public variable
	public int compCount = 0;
	public int compChecks = 0;

	public SeedWindow(int blockSize) {
		clear();
		this.blockSize = blockSize;
		addBlockToHead(new SeedBlock(blockSize));
	}

	public SeedWindow(int blockSize, int decayMode, int compressionMode, double epsilonPrime, double alpha,
			int compressionTerm) {
		clear();
		this.blockSize = blockSize;
		this.DECAY_MODE = decayMode;
		this.COMPRESSION_MODE = compressionMode;
		this.epsilonPrime = epsilonPrime;
		this.alpha = alpha;
		setCompressionTerm(compressionTerm);
		addBlockToHead(new SeedBlock(blockSize));
	}

	public void clear() {
		head = null;
		tail = null;
		width = 0;
		blockCount = 0;
		total = 0;
		variance = 0;
	}

	private boolean overlaps(double a, double b, double x, double y) {
		double rangeAB = Math.abs(a - b);
		double rangeXY = Math.abs(x - y);
		if (rangeAB > rangeXY && b >= x && a <= y) {
			return true;
		}
		return false;
	}
	
	public void addTransaction(double value, double[][] predictions, double maxMean) {
		if (tail.isFull()) {
			compChecks++;

			if (tail.getPrevious() != null && predictions != null) {
				decayCompressionCount = 0;
				SeedBlock cursor = head; 
				
				double dist = 0.0;
				double distPrev = 0.0;
				boolean prevCompression = true; 
				
				while (cursor != null && cursor.getNext() != null) {
					distPrev = dist + 1;
					dist = dist + cursor.getItemCount();
					if (cursor.getPrevious() != null) {
						boolean compress = true;						
						int numPred = predictions[0].length;
						for (int i = 0; i < numPred; i++) {
							double lowerBound = predictions[0][i];
							double upperBound = predictions[1][i];	
							double mean = (lowerBound + upperBound) / 2.0;
							if (distPrev >= maxMean) {
								compress = false;
								break;
							} else if (dist >= maxMean) {
								compress = false;
								break;
							} else if (contains(distPrev, dist, mean)) {
								compress = false;
								break;
							}
						}						
						if (prevCompression && compress) 
							compressBlock(cursor);
						}
						prevCompression = compress; // update state of previous compression
					}
					cursor = cursor.getNext();
				}
			}

			addBlockToTail(new SeedBlock(this.blockSize));
			decayCompressionCount++;
		}
		tail.add(value);
		total += value;

		width++;
		if (width >= 2) {
			double incVariance = (width - 1) * (value - total / (width - 1)) * (value - total / (width - 1)) / width;
			variance += incVariance;
			tail.setVariance(tail.getVariance() + incVariance);
		}

	}

	private boolean contains(double distPrev, double dist, double mean) {
		if (mean >= distPrev && mean <= dist) {
			return true;
		}
		return false;
	}

	public void resetDecayIteration() {
		this.decayIteration = 0;
	}

	public int getParetoEpsilon(int x) {
		double paretoAlpha = 1;
		int size = (int) Math.pow(x, -1 * paretoAlpha) * paretoDecayDefaultTermSize;

		if (size < 32) {
			return 32;
		}

		return size;

	}

	public void compressBlock(SeedBlock cursor) {
		compCount++;
		cursor.getPrevious().setTotal(cursor.getTotal() + cursor.getPrevious().getTotal());
		cursor.getPrevious().setItemCount(cursor.getItemCount() + cursor.getPrevious().getItemCount());
		cursor.getPrevious().setVariance(cursor.getVariance() + cursor.getPrevious().getVariance());
		cursor.getPrevious().setBlockSize(cursor.getBlockSize() + cursor.getPrevious().getBlockSize());

		if (cursor.getNext() != null) {
			cursor.getPrevious().setNext(cursor.getNext());
			cursor.getNext().setPrevious(cursor.getPrevious());
		} else {
			cursor.getPrevious().setNext(null);
			tail = cursor.getPrevious();
		}

		blockCount--;
	}

	public boolean checkHomogeneity(SeedBlock block) {
		double diff = Math.abs(block.getMean() - block.getPrevious().getMean());
		double epsilonPrime = getADWINBound(block.getItemCount(), block.getPrevious().getItemCount());
		if (diff < epsilonPrime) {
			return true;
		} else {
			return false;
		}
	}

	private double getADWINBound(double n0, double n1) {
		double n = n0 + n1;
		double dd = Math.log(2 * Math.log(n) / 0.99);
		double v = variance / width;
		double m = (1 / (n0)) + (1 / (n1));
		double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;

		return epsilon;
	}

	public void addBlockToHead(SeedBlock block) {
		if (head == null) {
			head = block;
			tail = block;
		} else {
			block.setNext(head);
			head.setPrevious(block);
			head = block;
		}
		blockCount++;
	}

	public void removeBlock(SeedBlock block) {
		width -= block.getItemCount();
		total -= block.getTotal();
		variance -= block.getVariance();
		blockCount--;

		if (block.getPrevious() != null && block.getNext() != null) {
			block.getPrevious().setNext(block.getNext());
			block.getNext().setPrevious(block.getPrevious());
			block.setNext(null);
			block.setPrevious(null);
		} else if (block.getPrevious() == null && block.getNext() != null) {
			block.getNext().setPrevious(null);
			head = block.getNext();
			block.setNext(null);
		} else if (block.getPrevious() != null && block.getNext() == null) {
			block.getPrevious().setNext(null);
			tail = block.getPrevious();
			block.setPrevious(null);
		} else if (block.getPrevious() == null && block.getNext() == null) {
			head = null;
			tail = null;
		}
	}

	public void addBlockToTail(SeedBlock block) {
		if (tail == null) {
			tail = block;
			head = block;
		} else {
			block.setPrevious(tail);
			tail.setNext(block);
			tail = block;
		}
		blockCount++;
	}

	public int getBlockCount() {
		return this.blockCount;
	}

	public void setBlockCount(int value) {
		this.blockCount = value;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int value) {
		this.width = value;
	}

	public void setHead(SeedBlock head) {
		this.head = head;
	}

	public void setTail(SeedBlock tail) {
		this.tail = tail;
	}

	public SeedBlock getHead() {
		return this.head;
	}

	public SeedBlock getTail() {
		return this.tail;
	}

	public double getTotal() {
		return this.total;
	}

	public void setTotal(double value) {
		this.total = value;
	}

	public double getVariance() {
		return this.variance;
	}

	public void setVariance(double value) {
		this.variance = value;
	}

	public void setBlockSize(int value) {
		if (value > 32) {
			this.blockSize = value;
		} else {
			this.blockSize = 32;
		}
	}

	public int getBlockSize() {
		return this.blockSize;
	}

	public double getEpsilonPrime() {
		return epsilonPrime;
	}

	public void setEpsilonPrime(double epsilonPrime) {
		this.epsilonPrime = epsilonPrime;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setCompressionTerm(int value) {
		this.paretoDecayTermSize = value;
		this.paretoDecayDefaultTermSize = value;
		this.linearFixedTermSize = value;
	}
}
