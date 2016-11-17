
package summer.proSeed.DriftDetection;

import com.github.javacliparser.FloatOption;

import moa.classifiers.core.driftdetection.ADWIN;
import moa.classifiers.core.driftdetection.AbstractChangeDetector;
import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;

/**
 * Drift detection method based in ADWIN. ADaptive sliding WINdow is a change
 * detector and estimator. It keeps a variable-length window of recently seen
 * items, with the property that the window has the maximal length statistically
 * consistent with the hypothesis "there has been no change in the average value
 * inside the window".
 *
 *
 * @author Albert Bifet (abifet at cs dot waikato dot ac dot nz)
 * @version $Revision: 7 $
 */
public class ADWINChangeDetector extends AbstractChangeDetector implements CutPointDetector {

    protected ADWIN adwin;

    public FloatOption deltaAdwinOption = new FloatOption("deltaAdwin", 'a',
            "Delta of Adwin change detection", 0.002, 0.0, 1.0);

    @Override
    public void input(double inputValue) {
        if (this.adwin == null) {
            resetLearning();
        }
        this.isChangeDetected = adwin.setInput(inputValue);
        this.isWarningZone = false;
        this.delay = 0.0;
        this.estimation = adwin.getEstimation();
    }

    @Override
    public void resetLearning() {
        adwin = new ADWIN((double) this.deltaAdwinOption.getValue());
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        // TODO Auto-generated method stub
    }

    @Override
    public long getChecks() {
        return 0; // ignore this
    }

    @Override
    public boolean setInput(double d) {
        input(d);
        return this.isChangeDetected;
    }

	@Override
	public void setPredictions(double[][] predictions) {
	    // dummy method not implemented	
	}
}

