package a.evaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoublePredicate;

import javax.swing.filechooser.FileFilter;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import a.tools.Directory;

public class Analyser
{

	public static void main(String[] args)
	{
		// new Analyser().analyse(Directory.resultFolderPath, "",
		// "200mblock_5noise_5,50,5,5,5,50,5,5");
		// new Analyser().analyse(Directory.resultFolderPath, "",
		// "3000000measuringwindows_size_100wblock_5noise_100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5,100,100,5,5");

		Analyser analyser = new Analyser();
		// analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
		// experiment"
		// + "\\fulldrift\\composed drift", true);
		// analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
		// experiment"
		// + "\\fulldrift\\long high vol period", true);
		// analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
		// experiment"
		// + "\\fulldrift\\long low vol period", true);
		// analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
		// experiment"
		// + "\\fulldrift\\regular", true);

//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed"
//		  + " experiment\\partial drift\\composed", false);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed"
//				  + " experiment\\partial drift\\composed", true);
		 
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
//		 experiment"
		// + "\\partial drift\\decreasing vol", true);
		// analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
		// experiment"
		// + "\\partial drift\\increasing and decreasing", true);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment"
//		 + "\\partial drift\\long high vol period", false);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment"
//		 + "\\partial drift\\regular", false);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment"
//		 + "\\partial drift\\long low vol period", false);

		// analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed
		// experiment"
		// + "\\partial drift\\1000 window size", true);

		/**
		 * Measure Window sizee
		 */
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\measure window 30", true);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\measure window 300", true);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\measure window 3000", true);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\measure window 30000", true);
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\measure window 3000000", true);
		/**
		 * reservoir size
		 */
		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\reservoir size 5", true);
		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\reservoir size 10", true);
		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\reservoir size 1000", true);
		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment\\partial drift\\reservoir size 10000", true);
		 
		/**
		 * SEA
		 */
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment"
//		 + "\\sea lambda 20\\long low vol period", true);
		
//		 analyser.analyseToExcel("C:\\Users\\rjia477\\Desktop\\completed experiment"
//		 + "\\sea lambda 20\\long low vol period", true);


		System.out.println("Analyse Done");

	}

	public void analyseToExcel(String resultFolderPath, boolean roundResult)
	{

		File resultFolder = new File(resultFolderPath);
		File[] fileList = resultFolder.listFiles(new FilenameFilter()
		{

			@Override
			public boolean accept(File dir, String name)
			{
				return new File(dir, name).isDirectory();
			}
		});

		// Algorithm name, AlgorithmStatisticsOverall
		Map<String, AlgorithmStatisticsOverall> algorithmStatsSummaries = new HashMap<String, Analyser.AlgorithmStatisticsOverall>();

		for (int i = 0; i < fileList.length; i++)
		{
			System.out.println(fileList[i].getPath());

			File[] algorithmsResultFolders = fileList[i].listFiles();

			// HAT, HT, VAC
			// for each algorithm
			for (int j = 0; j < algorithmsResultFolders.length; j++)
			{
				if (!algorithmStatsSummaries.containsKey(algorithmsResultFolders[j].getName()))
				{
					algorithmStatsSummaries.put(algorithmsResultFolders[j].getName(), new AlgorithmStatisticsOverall());
				}
				// read from the file
				BufferedReader reader;
				try
				{
					reader = new BufferedReader(new FileReader(algorithmsResultFolders[j].getPath() + "/summary.txt"));

					String line = null;
					while ((line = reader.readLine()) != null)
					{
						String[] pair = line.split(":");

						if (pair.length == 2)
						{
							algorithmStatsSummaries.get(algorithmsResultFolders[j].getName()).add(pair[0],
									Double.parseDouble(pair[1]));
						}

					}

					reader.close();

				} catch (FileNotFoundException e)
				{
					System.out.println("escape file: " + e.getMessage());
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				// Excel
				XSSFWorkbook wb = new XSSFWorkbook();
				XSSFSheet sheet = wb.createSheet("sheet");
				int algorithmIndex = 0;
				sheet.createRow(0); // top row

				for (String key : algorithmStatsSummaries.keySet())
				{
					String algorihtmName = key;
					// write algorithm name & abbreviate name
					if (algorihtmName.equals("VOL_ADAPTIVE_CLASSIFIER_AverageCurrentIntervalTimeStampMeasure"))
					{
						algorihtmName = "VACS";
					} else if (algorihtmName.equals("HOEFFDING_ADWIN"))
					{
						algorihtmName = "HRT";
					}
					sheet.getRow(0).createCell(2 * algorithmIndex + 1).setCellValue(algorihtmName + ":Mean");
					sheet.getRow(0).createCell(2 * algorithmIndex + 2).setCellValue(algorihtmName + ":SD"); // change
																											// to
																											// SD
					AlgorithmStatisticsOverall oneAlgorithmOverall = algorithmStatsSummaries.get(key);

					int rowCount = 1;
					for (String item : oneAlgorithmOverall.getOverallStatistics())
					{
						System.out.println(item);
						String[] strings = item.split(":");
						if (algorithmIndex == 0)
						{
							// write left column
							sheet.createRow(rowCount).createCell(0).setCellValue(strings[0]);
						}

						if (!roundResult)
						{
							// write Mean
							sheet.getRow(rowCount).createCell(2 * algorithmIndex + 1)
									.setCellValue(Double.parseDouble(strings[1]));
							// write Var
							sheet.getRow(rowCount).createCell(2 * algorithmIndex + 2)
									.setCellValue(Math.sqrt(Double.parseDouble(strings[2])));// compute
																								// SD
																								// from
																								// variance
						} else
						{
							// write Mean
							double mean = Double.parseDouble(strings[1]);
							mean = (double) Math.round(mean * 100) / 100;
							sheet.getRow(rowCount).createCell(2 * algorithmIndex + 1).setCellValue(mean);

							// write Var
							double var = Math.sqrt(Double.parseDouble(strings[2]));
							var = (double) Math.round(var * 100) / 100;
							sheet.getRow(rowCount).createCell(2 * algorithmIndex + 2).setCellValue(var);// compute
																										// SD
																										// from
																										// variance
						}

						rowCount++;
					}

					algorithmIndex++;

				}

				FileOutputStream fileOut;
				try
				{
					Path p = Paths.get(resultFolderPath);
					String fileNamePrefix = p.getParent().getFileName() + "_" + p.getFileName().toString();

					String fileName = roundResult ? fileNamePrefix + "_rounded.xlsx" : fileNamePrefix + ".xlsx";
					fileOut = new FileOutputStream(resultFolderPath + "\\" + fileName);
					wb.write(fileOut);
					fileOut.close();
					fileOut.close();
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		}
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
		});

		// Algorithm name, AlgorithmStatisticsOverall
		Map<String, AlgorithmStatisticsOverall> algorithmStatsSummaries = new HashMap<String, Analyser.AlgorithmStatisticsOverall>();

		for (int i = 0; i < fileList.length; i++)
		{
			System.out.println(fileList[i].getPath());

			File[] algorithmsResultFolders = fileList[i].listFiles();

			// HAT, HT, VAC
			// for each algorithm
			for (int j = 0; j < algorithmsResultFolders.length; j++)
			{
				if (!algorithmStatsSummaries.containsKey(algorithmsResultFolders[j].getName()))
				{
					algorithmStatsSummaries.put(algorithmsResultFolders[j].getName(), new AlgorithmStatisticsOverall());
				}
				// read from the file
				BufferedReader reader;
				try
				{
					reader = new BufferedReader(new FileReader(algorithmsResultFolders[j].getPath() + "/summary.txt"));

					String line = null;
					while ((line = reader.readLine()) != null)
					{
						String[] pair = line.split(":");

						if (pair.length == 2)
						{
							algorithmStatsSummaries.get(algorithmsResultFolders[j].getName()).add(pair[0],
									Double.parseDouble(pair[1]));
						}

					}

					reader.close();

				} catch (FileNotFoundException e)
				{
					System.out.println("escape file: " + e.getMessage());
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

		for (String key : algorithmStatsSummaries.keySet())
		{
			String algorihtmName = key;
			AlgorithmStatisticsOverall oneAlgorithmOverall = algorithmStatsSummaries.get(key);

			System.out.println(algorihtmName);
			for (String item : oneAlgorithmOverall.getOverallStatistics())
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
			if (!dict.containsKey(term))
			{
				dict.put(term, new AlgorithmStatistics());
			}

			dict.get(term).add(value);
		}

		public ArrayList<String> getOverallStatistics()
		{
			ArrayList<String> list = new ArrayList<String>();

			for (String key : dict.keySet())
			{
				AlgorithmStatistics statistics = dict.get(key);
				String mean = statistics.getMean() + "";
				String var = statistics.getVariance() + "";
				list.add(key + ":" + mean + ":" + var);
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
			for (int i = 0; i < elements.size(); i++)
			{
				sum += elements.get(i);
			}
			this.mean = sum / elements.size();
			return this.mean;
		}

		public double getVariance()
		{
			double numerator = 0;

			double meanValue;

			if (this.mean == null)
			{
				meanValue = this.mean;
			} else
			{
				meanValue = getMean();
			}

			for (int i = 0; i < elements.size(); i++)
			{

				numerator += Math.pow(elements.get(i) - meanValue, 2);
			}

			return numerator / (elements.size() - 1);

		}

	}

}
