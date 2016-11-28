package summer.proSeed.DriftDetection;

import java.io.FileNotFoundException;
import java.io.IOException;

import summer.proSeed.VolatilityDetection.DriftPrediction;
import summer.proSeed.VolatilityDetection.RelativeVolatilityDetector;

public class ProSeed implements CutPointDetector
{
	RelativeVolatilityDetector volatilityDetector;
	SeedDetector seedDetector;
	int numSamples;
	int learningPeriod; 
	
	
	/**
	 * 
	 * @param mergeParameter default: 3
	 * @param patternSize default: 100
	 * @param ksConfidence KSConfidence select from 0.05, 0.01, 0.1 
	 * @param topK default: 100
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ProSeed(int mergeParameter, int patternSize, double ksConfidence, int topK, 
			CutPointDetector VDdriftDetector, int VDSize, double VDconfidience,
			int learningPeriod) throws FileNotFoundException, IOException
	{
		numSamples = 0;
		this.learningPeriod = learningPeriod;
		
		// volatilityDetector
		DriftPrediction driftPredictor = new DriftPrediction(3, patternSize, ksConfidence, topK);
		volatilityDetector = new RelativeVolatilityDetector(VDdriftDetector, VDSize, VDconfidience, driftPredictor);
		
		// seed detector TODO
	}

	@Override
	public long getChecks()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setInput(double input)
	{
		// detecting phase
		boolean foundDrift = seedDetector.setInput(input);
		
		// training phase
		boolean volDrift = false;
		try
		{
			volDrift = volatilityDetector.setInputVarViaBuffer(input);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		double timestamp = volatilityDetector.getTimeStamp() + 1.0;
		// set prediction phase
		double[][] prediction = null;
		if(numSamples > learningPeriod)
		{
			prediction = volatilityDetector.getPredictor().predictNextCI(volDrift, timestamp);
		}
		seedDetector.setPredictions(prediction);
		
		return foundDrift;
	}

	@Override
	public void setPredictions(double[][] predictions)
	{
		// TODO Auto-generated method stub

	}

}
