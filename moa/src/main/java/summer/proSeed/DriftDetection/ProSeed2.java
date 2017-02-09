package summer.proSeed.DriftDetection;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import summer.proSeed.PatternMining.PatternReservoir;
import summer.proSeed.PatternMining.Network.ProbabilisticNetwork;
import summer.proSeed.PatternMining.Network.SeverityReservoirSampingEdge;
import summer.proSeed.PatternMining.Network.SeveritySamplingEdgeInterface;
import summer.proSeed.VolatilityDetection.DriftPrediction;
import summer.proSeed.VolatilityDetection.RelativeVolatilityDetector;

public class ProSeed2 implements CutPointDetector
{
	RelativeVolatilityDetector volatilityDetector;
	int numSamples;
	int learningPeriod;
	BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/rl/Desktop/data/coefficient"));

	/**
	 * 
	 * @param mergeParameter
	 *            default: 3
	 * @param patternSize
	 *            default: 100
	 * @param ksConfidence
	 *            KSConfidence select from 0.05, 0.01, 0.1
	 * @param topK
	 *            default: 100
	 * @param VDSize
	 *            default: 32
	 * @param VDconfidience
	 *            default: 0.5
	 * @param learningPeriod
	 *            default: 0
	 * @param severitySampeSize
	 *  		  default: 100          
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ProSeed2(int mergeParameter, int patternSize, double ksConfidence, int topK, SeedDetector VDdriftDetector,
			int VDSize, double VDconfidience, int learningPeriod, int severitySampeSize, boolean useSimpleRiskMethod,
			double coefficientBeta) throws FileNotFoundException, IOException
	{
		numSamples = 0;
		this.learningPeriod = learningPeriod;

		// volatilityDetector
		DriftPrediction driftPredictor = new DriftPrediction(3, patternSize, ksConfidence, topK, severitySampeSize, useSimpleRiskMethod, coefficientBeta);
		
		volatilityDetector = new RelativeVolatilityDetector(VDdriftDetector, VDSize, VDconfidience, driftPredictor);

		// seedDetector = new SeedDetector(0.05, 0.1, 32, 1, 1, 0.01, 0.8, 75, 32, 50); // best
	}

	public void mergeNetwork()
	{
		this.volatilityDetector.getPredictor().getPatternReservoir().merge();
	}

	public ProbabilisticNetwork getNetwork()
	{
		return this.volatilityDetector.getPredictor().getPatternReservoir().getNetwork();
	}

	public RelativeVolatilityDetector getVolatilityDetector()
	{
		return this.volatilityDetector;
	}

	@Override
	public long getChecks()
	{
		return 0;
	}

	// perform training without detection.
	private boolean setTraining(double input)
	{
		boolean volDrift = false;
		try
		{
			volDrift = volatilityDetector.setInputVarViaBuffer(input);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return volDrift;
	}

	@Override
	public boolean setInputWithTraining(double input)
	{
		this.setTraining(input);

		return volatilityDetector.getDriftFound();
	}
	
	@Override
	public boolean setInput(double input)
	{
		// detecting phase
		// boolean foundDrift = this.seedDetector.setInput(input);

		double timestep = this.volatilityDetector.getTimeStamp() + 1.0;

		
		boolean volDrift = false;
		try
		{
			volDrift = volatilityDetector.setInputVarViaBuffer(input);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// setting prediction phase
		PredictionModel predictionModel = new PredictionModel();
		DriftPrediction driftPrediction = volatilityDetector.getPredictor();


		
		if(volDrift) 
		{
			predictionModel.predictedDriftPostion = driftPrediction.predictNextCI(volDrift, timestep);
			predictionModel.deltaCoefficient = driftPrediction.getThresholdCoefficient();
		 	volatilityDetector.getDetector().setPredictions(predictionModel);
		 	try
			{
				writer.write(predictionModel.deltaCoefficient+"\n");
				writer.flush();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		numSamples++;
			
		return volatilityDetector.getDriftFound();
	}

	public void setPrediction()
	{

	}

	@Override
	public void setPredictions(double[][] predictions)
	{
		// TODO Auto-generated method stub

	}
	
	public PatternReservoir getPatternReservoir()
	{
		return this.volatilityDetector.getPredictor().getPatternReservoir();
	}
	
	public double getSeverity()
	{
		return volatilityDetector.getDetector().getSeverity();
		
	}

	@Override
	public void setPredictions(PredictionModel predictions)
	{
		// TODO Auto-generated method stub
		
	}
	

}
