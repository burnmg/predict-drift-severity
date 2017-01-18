package summer.proSeed.PatternMining.Network;

import static org.junit.Assert.*;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.ws.RespectBinding;

import org.apache.commons.math3.analysis.function.Abs;
import org.junit.Test;

import weka.gui.treevisualizer.Edge;

public class ProbabilisticNetworkTest
{

	@Test
	public void testMerge()
	{
		ProbabilisticNetwork network = new ProbabilisticNetwork(10);
		
		SeveritySamplingEdgeInterface[][] edges 
		= new SeveritySamplingEdgeInterface[network.getPatternNetworkSize()][network.getPatternNetworkSize()];
		
		for(int i=0;i<edges.length;i++)
		{
			for(int j=0;j<edges[0].length;j++)
			{
				edges[i][j] = new SeverityReservoirSampingEdge(100);
			}
		}
		
		// let 1 be FIRST. let 2 be the SECOND
		// in 2
		edges[0][2].addSamples(new double[]{1,1,1,1,10,11,100});
		edges[5][2].addSamples(new double[]{2,2,2,2});
		
		// out 2
		edges[2][4].addSamples(new double[]{3,3,3});
		edges[2][9].addSamples(new double[]{4,4,4});
		network.setEdges(edges);
		int countIn = 0;
		int countOut = 0;
		
		network.merge(1, 2);;
		SeveritySamplingEdgeInterface[][] res = network.getEdges();
		for(int i=0;i<network.getPatternNetworkSize();i++)
		{
			double[] sample = res[1][i].getSamples();
			for(int j=0;j<sample.length;j++)
			{
				if (Math.abs(sample[j])>0)
						
				{
					countOut++;
				}
				
				

			}
			
			sample = res[i][1].getSamples();
			for(int j=0;j<sample.length;j++)
			{
				if (Math.abs(sample[j])>0)	
				{
					countIn++;
				}
			}
			
		}
		
		System.out.println("In:" + countIn +"\n"+ "Out:" + countOut );
	}

}
