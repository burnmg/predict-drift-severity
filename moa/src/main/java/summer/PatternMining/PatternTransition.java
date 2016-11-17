/*
 * PatternTransition.java
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

public class PatternTransition implements Comparable<PatternTransition> {
    
    private int index;
    private double transitionProbability;
    
    public PatternTransition(int index, double prob) {
        this.index = index;
        this.transitionProbability = prob;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void setTransitionProbability(double prob) {
        this.transitionProbability = prob;
    }
    
    public int getIndex() {
        return index;
    }
    
    public double getTransitionProbability() {
        return transitionProbability;
    }

    // for descending sort
    @Override
    public int compareTo(PatternTransition t) {
        if (this.transitionProbability < t.getTransitionProbability() ) {
            return 1;
        } else {
            return -1;
        }        
    }
    
}
