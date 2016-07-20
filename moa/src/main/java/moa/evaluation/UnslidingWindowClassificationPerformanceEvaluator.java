package moa.evaluation;

import com.github.javacliparser.IntOption;

public class UnslidingWindowClassificationPerformanceEvaluator extends BasicClassificationPerformanceEvaluator 
{
    private static final long serialVersionUID = 1L;

    public IntOption widthOption = new IntOption("width",
            'w', "Size of Window", 1000);

    @Override
    protected Estimator newEstimator() {
        return new WindowEstimator(this.widthOption.getValue());
    }

    public class WindowEstimator implements Estimator {

        protected double[] window;

        protected int posWindow;

        protected int lenWindow;

        protected int SizeWindow;

        protected double sum;

        public WindowEstimator(int sizeWindow) {
            window = new double[sizeWindow];
            SizeWindow = sizeWindow;
            posWindow = 0;
            lenWindow = 0;
        }

        public void add(double value) {

            if (posWindow == SizeWindow) {
                posWindow = 0;
                sum = 0;
                lenWindow = 0;
            }
            
            lenWindow++;
            sum += value;
            window[posWindow] = value;
            posWindow++;
        }

        public double estimation(){
            return sum/(double) lenWindow;
        }

    }
}
