/*
 * IndexedPattern.java
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

package summer.PatternMining;

public class IndexedPattern extends Pattern {

    private int index;

    public IndexedPattern(double mean, double variance) {
        super(mean, variance);
    }

    /**
     * creates an indexed pattern object
     *
     * @param mean mean of pattern
     * @param variance variance of pattern
     * @param i index of pattern
     */
    public IndexedPattern(double mean, double variance, int i) {
        super(mean, variance);
        this.index = i;
    }

    /**
     * sets the index of a pattern
     *
     * @param i pattern index
     */
    public void setIndex(int i) {
        index = i;
    }
    
    public int getIndex() {
        return index;
    }
    
	public String toString() {				
		return getMean() + "\t" + getRealVariance() + "\t" + ALPHA;
	}
}
