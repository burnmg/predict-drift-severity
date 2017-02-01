package summer.originalSeed;

/*
This source code is provided "as is" without warranty of any kind. 
All warranties, expresss and implied, including without limitation, 
any implied warranties of merchantability, fitness for a particular 
purpose or noninfringement are further disclaimed.

In no event shall the authors be liable for any indirect, incidental, 
special, punitive, or consequential damages, or damages for loss of 
profits, revenue, data or data use, incurred by you or any third party, 
whether in an action in contract or tort, even if the authors have been 
advised of the possibility of such damages.
*/

public class SeedDetector implements summer.proSeed.DriftDetection.CutPointDetector {
	public SeedWindow window;
	private double DELTA;
	private int defaultBlockSize;
	private int blockSize;
	private int elementCount;

	// Testing purpose public variable
	public long checks;
	public int warningCount = 0;

	public SeedDetector(double delta, int blockSize) {
		this.DELTA = delta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize);
	}

	public SeedDetector(double delta, int blockSize, int decayMode, int compressionMode, double epsilonHat,
			double alpha, int term) {
		this.DELTA = delta;
		this.defaultBlockSize = blockSize;
		this.blockSize = blockSize;
		this.window = new SeedWindow(blockSize, decayMode, compressionMode, epsilonHat, alpha, term);
	}

	@Override
	public boolean setInputWithTraining(double inputValue) {
		SeedBlock cursor;

		addElement(inputValue);

		if (elementCount % blockSize == 0 && window.getBlockCount() >= 2) // Drift
																			// Point
																			// Check
		{
			boolean blnReduceWidth = true;

			while (blnReduceWidth) {
				// int warningStage = 0;
				// boolean warning = false;

				blnReduceWidth = false;
				int n1 = 0;
				int n0 = window.getWidth();
				double u1 = 0;
				double u0 = window.getTotal();

				cursor = window.getTail();
				while (cursor.getPrevious() != null) {
					n0 -= cursor.getItemCount();
					n1 += cursor.getItemCount();
					u0 -= cursor.getTotal();
					u1 += cursor.getTotal();
					double diff = Math.abs(u1 / n1 - (u0 / n0));

					checks++;
					if (diff > getADWINBound(n0, n1)) {
						blnReduceWidth = true;
						window.resetDecayIteration();
						window.setHead(cursor);

						while (cursor.getPrevious() != null) {
							cursor = cursor.getPrevious();
							window.setWidth(window.getWidth() - cursor.getItemCount());
							window.setTotal(window.getTotal() - cursor.getTotal());
							window.setVariance(window.getVariance() - cursor.getVariance());
							window.setBlockCount(window.getBlockCount() - 1);
						}

						window.getHead().setPrevious(null);

						return true;
					}

					cursor = cursor.getPrevious();
				}
			}
		}

		return false;
	}

	private double getADWINBound(double n0, double n1) {
		double n = n0 + n1;
		// System.out.println(n0 + " " + n1);
		double dd = Math.log(2 * Math.log(n) / DELTA);
		double v = window.getVariance() / window.getWidth();
		double m = (1 / (n0)) + (1 / (n1));
		double epsilon = Math.sqrt(2 * m * v * dd) + (double) 2 / 3 * dd * m;

		return epsilon;
	}

	public void addElement(double value) {
		window.addTransaction(value);
		elementCount++;
	}

	public long getChecks() {
		return checks;
	}

	@Override
	public void setPredictions(double[][] predictions) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getSeverity()
	{
		return 0;
	}
}
