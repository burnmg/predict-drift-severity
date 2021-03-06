/*
 * CutPointDetector.java
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

package summer.proSeed.DriftDetection;

public interface CutPointDetector {
	public long getChecks();


	public void setPredictions(double[][] predictions);
	
	public double getSeverity();

	public void setPredictions(PredictionModel predictions);

	boolean setInput(double input);

	boolean setInputWithTraining(double input);
}