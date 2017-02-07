import java.util.concurrent.Callable;
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
		
		
		ExecutorService pool = Executors.newFixedThreadPool(1);
		
		Callable<Integer> task = new SummerExperimentThreadUnit(0.1, 2313 , 3, "ProSeed2");
		task.call();
		
		// pool.submit(task);
		pool.shutdown();
		
		try
		{
			pool.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		re.stop();

	}

}
