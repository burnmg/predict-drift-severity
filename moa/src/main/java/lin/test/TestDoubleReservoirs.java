package lin.test;

import a.algorithms.DoubleReservoirs;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.apache.poi.POIDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.google.common.collect.Table.Cell;

public class TestDoubleReservoirs
{

	public static void main(String[] args) throws IOException
	{


		
		// generateSinData(0, 1000, 1, 10000, 15000);
		test();
		
	}
	
	
	
	public static void test() throws IOException
	{
		DoubleReservoirs doubleReservoirs = new DoubleReservoirs(100);
		
		Workbook outputWorkbook = new HSSFWorkbook();
		Sheet outputSheet = outputWorkbook.createSheet("1");
		outputSheet.createRow(0).createCell(0).setCellValue("vol");
		outputSheet.createRow(0).createCell(1).setCellValue("mean");
		outputSheet.createRow(0).createCell(2).setCellValue("isActive");
		
		FileInputStream inputStream = new FileInputStream(new File("sin_wave_vol.xls")); 
		Workbook inputWorkbook = new HSSFWorkbook(inputStream);
		Sheet inputsheet = inputWorkbook.getSheetAt(0);
		Iterator<Row> rowIterator = inputsheet.rowIterator();
		rowIterator.next();
		
		int rowCount = 1;
		outputSheet.getRow(0).createCell(1).setCellValue("reservoir 100");
		while(rowIterator.hasNext())
		{
			double value = rowIterator.next().getCell(0).getNumericCellValue();
			outputSheet.createRow(rowCount).createCell(0).setCellValue(value);
			
			doubleReservoirs.setInput(value);
			outputSheet.getRow(rowCount).createCell(1).setCellValue(doubleReservoirs.getMean());
			
			if(doubleReservoirs.isActive())
			{
				outputSheet.getRow(rowCount).createCell(2).setCellValue(1000);
			}
			
			rowCount++;
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File("sin_wave_vol_Result_With_Hoeffding.xls")); 
		outputWorkbook.write(outputStream);
		outputWorkbook.close();
		inputWorkbook.close();
		
	}
	
	public static void generateSinData(double startX, double endX, 
			double step, double amplitude, double startY) throws IOException
	{
		//getDataSet(0, 1000, 1, 1000);
	    Workbook workbook = new HSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Test");
	    FileOutputStream fileOut = new FileOutputStream("workbook.xls");
	    sheet.createRow(0).createCell(0).setCellValue("Vol");
	    
	    Random ran = new Random();
	    int rowNum = 1; 
		for(double i=startX;i<endX; i += step)
		{
			double output = Math.sin(i * 0.005)*amplitude + startY + ran.nextFloat()*0.5*amplitude;
			sheet.createRow(rowNum).createCell(0).setCellValue(output);
			rowNum++;
		}
		
		
	    workbook.write(fileOut);
	    fileOut.close();
	    workbook.close();
	}
	


}