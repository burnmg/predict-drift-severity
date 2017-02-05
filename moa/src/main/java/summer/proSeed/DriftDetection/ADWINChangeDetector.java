
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
public class ADWINChangeDetector implements CutPointDetector {

    protected ADWIN adwin;


    public ADWINChangeDetector(double delta)
	{
    	adwin = new ADWIN(delta);
	}

    @Override
    public long getChecks() {
        return 0; // ignore this
    }

    @Override
    public boolean setInput(double d) {
    	boolean drift = adwin.setInput(d);
    	
    	return drift;
    }

	@Override
	public void setPredictions(double[][] predictions) {
	    // dummy method not implemented	
	}

	@Override
	public double getSeverity()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPredictions(PredictionModel predictions)
	{
		// TODO Auto-generated method stub
		
	}


}

