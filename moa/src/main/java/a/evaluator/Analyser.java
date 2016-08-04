package a.evaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoublePredicate;

import javax.swing.filechooser.FileFilter;

import org.apache.poi.ss.formula.functions.Value;

import a.tools.Directory;

public class Analyser
{

	public static void main(String[] args)
	{
//		new Analyser().analyse(Directory.resultFolderPath, "", "200mblock_5noise_5,50,5,5,5,50,5,5");
		new Analyser().analyse(Directory.resultFolderPath, "", "100mblock_5noise_5,50,5,5,5,50,5,5");
//		new Analyser().analyse("C:\\Users\\rjia477\\Desktop\\Results archive", "", "100mblock_5noise_50,5,50,50,50,5,50,50,50");
		
	}
	
	public void analyse(String resultFolderPath, String pathPrefix, String streamId)
	{
		final String finalPathPrefix = pathPrefix;
		final String finalStreamId = streamId;
		
		File resultFolder = new File(resultFolderPath);
		File[] fileList = resultFolder.listFiles(new FilenameFilter()
		{
			
			@Override
			public boolean accept(File dir, String name)
			{
				return name.startsWith(finalPathPrefix + finalStreamId + "_");
			}
		}
				);
		
		// Algorithm name, AlgorithmStatisticsOverall
		Map<String, AlgorithmStatisticsOverall> algorithmStatsSummaries = new HashMap<String, Analyser.AlgorithmStatisticsOverall>();
		
		for(int i=0;i<fileList.length;i++)
		{
			System.out.println(fileList[i].getPath());
			
			File[] algorithmsResultFolders = fileList[i].listFiles();
			
			// HAT, HT, VAC
			// for each algorithm
			for(int j=0;j<algorithmsResultFolders.length;j++)
			{
				if(!algorithmStatsSummaries.containsKey(algorithmsResultFolders[j].getName()))
				{
					algorithmStatsSummaries.put(algorithmsResultFolders[j].getName(), new AlgorithmStatisticsOverall());
				}
				// read from the file
				BufferedReader reader;
				try
				{
					reader = new BufferedReader(new FileReader(algorithmsResultFolders[j].getPath() + "/summary.txt"));
					
					String line = null;
					while((line = reader.readLine())!=null)
					{
						String[] pair = line.split(":");
						
						if(pair.length==2)
						{
							algorithmStatsSummaries.get(algorithmsResultFolders[j].getName()).add(pair[0], Double.parseDouble(pair[1]));
						}
						
						
					
					}
					
					reader.close();
					
				} catch (FileNotFoundException e)
				{
					System.out.println("escape file: "+e.getMessage());
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		
		for(String key : algorithmStatsSummaries.keySet())
		{
			String algorihtmName = key;
			AlgorithmStatisticsOverall oneAlgorithmOverall = algorithmStatsSummaries.get(key);
			
			System.out.println(algorihtmName);
			for(String item : oneAlgorithmOverall.getOverallStatistics())
			{
				System.out.println(item);
			}
			System.out.println("********");
			
		}
		
		
	}
	
	
	class AlgorithmStatisticsOverall
	{
		// Term, Statistics
		Map<String, AlgorithmStatistics> dict;
		
		public AlgorithmStatisticsOverall()
		{
			dict = new HashMap<String, Analyser.AlgorithmStatistics>();
		}
		
		public void add(String term, double value)
		{
			if(!dict.containsKey(term))
			{
				dict.put(term, new AlgorithmStatistics());
			}
			
			dict.get(term).add(value);
		}
		
		public ArrayList<String> getOverallStatistics()
		{
			ArrayList<String> list = new ArrayList<String>();
			
			for(String key : dict.keySet())
			{
				AlgorithmStatistics statistics = dict.get(key);
				String mean = statistics.getMean() + "";
				String var = statistics.getVariance() + "";
				list.add(key+":"+mean+"   "+var);
			}
			
			return list;
		}
		
	}
	
	class AlgorithmStatistics
	{
		private ArrayList<Double> elements = new ArrayList<Double>();
		
		private Double mean = null; 
		
		public void add(double d)
		{
			elements.add(d);
		}
		
		public double getMean()
		{
			double sum = 0;
			for(int i=0;i<elements.size();i++)
			{
				sum += elements.get(i);
			}
			this.mean = sum/elements.size();
			return this.mean;
		}
		
		public double getVariance()
		{
			double numerator = 0;
			
			double meanValue;
			
			if(this.mean==null)
			{
				meanValue = this.mean;
			}
			else 
			{
				meanValue = getMean();
			}
			
			for(int i=0;i<elements.size();i++)
			{
				
				numerator += Math.pow(elements.get(i) - meanValue, 2);
			}
			
			return numerator/(elements.size() - 1);
			
		}
		
		
	}
	

}
