package summer.magSeed;
/*
 * MagSeedChangeDetector.java
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

import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;

import moa.classifiers.core.driftdetection.AbstractChangeDetector;

/**
 * 
 * Drift detection method as published in:
 * Kylie Chen, Yun Sing Koh, and Patricia Riddle. Tracking Drift Severity in Data Streams. In AI 2015: Advances in Artificial Intelligence, pages 96-108, 2015.
 * 
 * Usage: input(double)
 * 
 * @author Kylie Chen - The University of Auckland
 * @author David T.J. Huang - The University of Auckland
 * @version 1.0
 */

import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

public class MagSeedChangeDetector extends AbstractChangeDetector {

	protected MagSeed magSeed;

	public FloatOption deltaOption = new FloatOption(
	"deltaSeed", 
	'd',
	"Delta for drift detection.", 
	0.05, 0.0, 1.0);

	public IntOption blockSizeOption = new IntOption(
	"blockSize",
	's',
	"The size of blocks for block compression.",
	32, 1, Integer.MAX_VALUE);

	public FloatOption epsilonOption = new FloatOption(
	"epsilonPrime",
	'e',
	"Epsilon prime for block compression.",
	0.01, 0.0025, 0.01);

	public FloatOption alphaGrowthOption = new FloatOption(
	"alphaGrowth",
	'g',
	"Alpha growth for block compression.",
	0.8, 0.2, 0.8);

	public IntOption compressionTermOption = new IntOption(
	"compressionTerm",
	'c',
	"Compression term for block compression.",
	75, 50, 100);

	public FloatOption warningDeltaOption = new FloatOption(
	"warningDelta",
	't',
	"Delta for warning detection (delta_w).",
	0.1, 0.0, 1.0); 

	public FloatOption warningConfidenceOption = new FloatOption(
	"warningCondifence",
	'b',
	"Confidence for warning detection (c_w).",
	2.0, 0.0, 3.0);

	public IntOption windowSizeOption = new IntOption(
	"windowSize",
	'w',
	"Size of sliding window for computing current mean error.",
	100, 1, 10000);
	
	@Override
	public void resetLearning() {
		magSeed = new MagSeed(this.deltaOption.getValue(), this.blockSizeOption.getValue(), 1, 1, this.epsilonOption.getValue(), this.alphaGrowthOption.getValue(), this.compressionTermOption.getValue());
		magSeed.setWarningDelta(this.warningDeltaOption.getValue());
		magSeed.setWarningConfidence(this.warningConfidenceOption.getValue());
		magSeed.setWindowSize(this.windowSizeOption.getValue());
		magSeed.setWarning(false);
	}

	@Override
	public void input(double inputValue) {
		if (this.magSeed == null) {
			resetLearning();
		}
		this.isChangeDetected = magSeed.setInput(inputValue);
		this.isWarningZone = magSeed.getWarning(); // warning version
		this.delay = 0.0;
		this.estimation = magSeed.getEstimation();
	}

	@Override
	public void getDescription(StringBuilder sb, int indent) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
		// TODO Auto-generated method stub
	}

}
