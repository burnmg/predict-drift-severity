package cutpointdetection;

public interface CutPointDetector
{    
    public boolean setInput(double d);
    
    public void clear();
    
    public double getEstimation();
}
