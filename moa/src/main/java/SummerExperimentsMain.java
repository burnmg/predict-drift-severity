import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.rosuda.JRI.Rengine;

import summer.main.PatternGenerator;
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
		int repeatTime = 5;
		
		ExecutorService pool = Executors.newFixedThreadPool(10);

		double[] cons = new double[]{0.05, 0.1, 0.15, 0.2, 0.25};
		Random ran = new Random(5776);
		
		// 5 patterns
		
		Pattern[] patterns = { new Pattern(1000, 100), new Pattern(2000, 100), new Pattern(3000, 100),new Pattern(4000, 100),new Pattern(5000, 100)};
		double[][] networkTransitions = PatternGenerator.generateNetworkProb(new double[]{0.2, 0.2, 0.4, 0.2});
		
		Double[][] severityEdges = PatternGenerator.generateEdgesHighLow(0.3, 0.8, 5);
		
		
		/*
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.1}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.1"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.2}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.2"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.3}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.3"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.4}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.4"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.5}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.5"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.6"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.7}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.7"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.8}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.8"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.9}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta0.9"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{1.0}, ran.nextInt(), repeatTime, "ProSeed2", "ProSeed2 beta1.0"));
		*/
		
		/*
		 * ONLINE PRESS
		 * 
		 */
		
		
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.05}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.05 set1"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.1}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.1 set1"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.15}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.15 set1"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.2}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.2 set1"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.25}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.25 set1"));

		pool.submit(new SummerExperimentThreadUnit(new double[]{0.05}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.05 set2"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.1}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.1 set2"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.15}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.15 set2"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.2}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.2 set2"));
		pool.submit(new SummerExperimentThreadUnit(new double[]{0.25}, new double[]{0.6}, ran.nextInt(), repeatTime, "ProSeed2", "PRESS beta0.6 conf 0.25 set2"));
		
		
		/**
		 * Seed and ADWIN
		 */
		/*
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.0}, ran.nextInt(), 10, "ADWIN", "ADWIN"));
		pool.submit(new SummerExperimentThreadUnit(cons, new double[]{0.0}, ran.nextInt(), 10, "Seed", "Seed"));
		*/
		
		/**
		 * ProSeed1
		 */
		
		/*
		pool.submit(new SummerExperimentThreadUnit((double[]) Arrays.copyOfRange(cons, 0, 3), new double[]{0.0}, ran.nextInt(), 10, "ProSeed1", "ProSeed1.1"));
		pool.submit(new SummerExperimentThreadUnit((double[]) Arrays.copyOfRange(cons, 3, cons.length), new double[]{0.0}, ran.nextInt(), 10, "ProSeed1", "ProSeed1.2"));
		*/
		
		pool.shutdown();

		


		try
		{
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		re.end();
		
		System.out.println("Done!");

	}

}
