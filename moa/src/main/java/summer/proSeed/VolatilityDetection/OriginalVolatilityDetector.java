/*
 * OriginalVolatilityDetector.java
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

import DriftDetection.CutPointDetector;
import java.io.IOException;

public class OriginalVolatilityDetector {
    
    private CutPointDetector cutpointDetector;
    private Reservoir reservoir;
    private Buffer buffer;
    private double confidence;

    private int timestamp = 0;

    public OriginalVolatilityDetector(CutPointDetector cutpointDetector,
            int resSize) {
        this.cutpointDetector = cutpointDetector;
        this.reservoir = new Reservoir(resSize);
        this.buffer = new Buffer(resSize);
        this.confidence = 0.05;
    }

    public OriginalVolatilityDetector(CutPointDetector cutpointDetector,
            int resSize, double confidence) {
        this.cutpointDetector = cutpointDetector;
        this.reservoir = new Reservoir(resSize);
        this.buffer = new Buffer(resSize);
        this.confidence = confidence;
    }

    public Boolean setInputVar(double inputValue) {
        
        if (cutpointDetector.setInput(inputValue)) {
            if (buffer.isFull()) {
                reservoir.addElement(buffer.add(++timestamp));
            } else {
                buffer.add(++timestamp);
            }

            System.out.println(timestamp);
            
            timestamp = 0; 

            if (buffer.isFull() && reservoir.isFull()) {

                double RelativeVar = buffer.getStdev() / reservoir.getReservoirStdev();

                if (RelativeVar > 1.0 + confidence || RelativeVar < 1.0 - confidence) {
                    reservoir.clear();
                    return true;
                } else {
                    return false;
                }
            }
            
        } else {
            timestamp++;
            return false;
        }
        return false;
    }
}
