
package summer.proSeed.DriftDetection;

import com.github.javacliparser.FloatOption;

import moa.classifiers.core.driftdetection.ADWIN;
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

	@Override
	public boolean setInputWithTraining(double input)
	{
		// TODO Auto-generated method stub
		return false;
	}


}

