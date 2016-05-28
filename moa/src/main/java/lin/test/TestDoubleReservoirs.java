package lin.test;

import a.algorithms.DoubleReservoirs;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import org.apache.poi.POIDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

public class TestDoubleReservoirs
{

	public static void main(String[] args) throws IOException
	{


		
		generateData();

		
	}
	
	public static void test(int reservoirSize)
	{
		
	}
//	public static void test(int reservoirSize) throws NumberFormatException, IOException
//	{
//		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("sin wave 100000 middle point.csv")));
//		DoubleReservoirs dReservoirs = new DoubleReservoirs(reservoirSize);
//		
//		String line = null;
//		while((line = bufferedReader.readLine())!=null)
//		{
//			double input = Double.parseDouble(line);
//			dReservoirs.setInput(input);
////			System.out.println(dReservoirs.highReservoir.getReservoirMean());
////			System.out.println(dReservoirs.lowReservoir.getReservoirMean());
//			System.out.println(dReservoirs.getMean());
//		}
//		bufferedReader.close();
//	}
	
	public static void generateData() throws IOException
	{
		//getDataSet(0, 1000, 1, 1000);
	    Workbook wb = new HSSFWorkbook();
	    wb.createSheet("Test");
	    FileOutputStream fileOut = new FileOutputStream("workbook.xls");
	    wb.write(fileOut);
	    fileOut.close();
	    wb.close();
	}
	
	public static ArrayList<Double> getDataSet(double low, double high, double step, double amplitude) throws IOException
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("sin wave 100000 middle point.csv"))); 
		Random ran = new Random();
		
		ArrayList<Double> data = new ArrayList<Double>();
		for(double i=low;i<high; i += step)
		{
			double output = 0;
			output = Math.sin(i)*amplitude*ran.nextFloat()*0.1 + 100000;
			data.add(output);
			bufferedWriter.write(output+"\n");
		}
		bufferedWriter.flush();
		bufferedWriter.close();
		
		return data;
	}
	


}