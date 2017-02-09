import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.rosuda.JRI.Rengine;

import summer.main.SummerExperimentThreadUnit;
import summer.proSeed.PatternMining.Pattern;
import summer.proSeed.kylieExample.TextConsole;

public class SummerExperimentsMain
{

	public static void main(String[] args) throws Exception
	{
		/*
		 * START Rengine
		 */
		String[] reArgs = new String[]{"--save"};
		Rengine re = new Rengine(reArgs, false, new TextConsole());
		System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
		if (!re.waitForR()) {
			System.out.println("Cannot load R");
			return;
		}
		Pattern.setRengine(re);
		/*
		 * END Rengine
		 */
		
		
		ExecutorService pool = Executors.newFixedThreadPool(2);

		// double[] cons = new double[]{0.05, 0.1, 0.15, 0.2, 0.25};
		double[] betas = new double[]{0.1};
		double[] cons = new double[]{0.05, 0.1};
		// public SummerExperimentThreadUnit(double[] confidences, 
		// double[] betas, int seed, int repeatTime, String detectorName, String fileName)
		

		// pool.submit(new SummerExperimentThreadUnit(cons, betas, 321, 2, "ProSeed2", "ProSeed"));
		new SummerExperimentThreadUnit(cons, betas, 321, 2, "ProSeed1", "ProSeed1").call();

		pool.shutdown();
		
		try
		{
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("Done!");
		
		re.end();

	}

}
