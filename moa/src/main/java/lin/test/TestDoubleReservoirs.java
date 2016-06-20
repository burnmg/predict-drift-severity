package lin.test;

import a.algorithms.DoubleReservoirs;
import java.io.*;
import java.util.Iterator;
import java.util.Random;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class TestDoubleReservoirs
{

	public static void main(String[] args) throws IOException
	{


		
//		 test(generateSinData(0, 10000, 1, 10000, 15000, 100));
		test(generatePlainData(0, 1000, 1, 10000, 15000,1000)); 
	}
	
	
	
	public static void test(String dataPath) throws IOException
	{
		DoubleReservoirs doubleReservoirs = new DoubleReservoirs(100, 0);
		
		Workbook outputWorkbook = new HSSFWorkbook();
		Sheet outputSheet = outputWorkbook.createSheet("1");
		outputSheet.createRow(0).createCell(0).setCellValue("vol");
		outputSheet.getRow(0).createCell(2).setCellValue("isActive");
		
		FileInputStream inputStream = new FileInputStream(new File(dataPath)); 
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
		
		FileOutputStream outputStream = new FileOutputStream(new File(dataPath+"_lambda_"+doubleReservoirs.getLambda()+"_Result_With_Hoeffding.xls")); 
		outputWorkbook.write(outputStream);
		outputWorkbook.close();
		inputWorkbook.close();
		
	}
	
	public static String generatePlainData(double startX, double endX, 
			double step, double amplitude, double startY, double noise) throws IOException
	{
	    Workbook workbook = new HSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Test");
	    String filePath = "plain_data_"+
	    "_amplitude_"+
	    (int)amplitude+
	    "_noise_"+
	    noise+ //TODO
	    ".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath
	    );
	    sheet.createRow(0).createCell(0).setCellValue("Vol");
	    
	    Random ran = new Random();
	    int rowNum = 1; 
		for(double i=startX;i<endX; i += step)
		{
			double output = startY + ran.nextFloat()*noise;
			sheet.createRow(rowNum).createCell(0).setCellValue(output);
			rowNum++;
		}
		
		
	    workbook.write(fileOut);
	    fileOut.close();
	    workbook.close();
	    
	    return filePath;
	}
	
	public static String generateSinData(double startX, double endX, 
			double step, double amplitude, double startY, double noise) throws IOException
	{
	    Workbook workbook = new HSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Test");
	    String filePath = "sin_wave_amplitude_"+(int)amplitude+
	    		"_startX_"+(int)startX+
	    		"_endX_"+(int)endX+
	    		"_noise_"+(int)noise+
	    		".xls";
		FileOutputStream fileOut = new FileOutputStream(filePath);
	    sheet.createRow(0).createCell(0).setCellValue("Vol");
	    
	    Random ran = new Random();
	    int rowNum = 1; 
		for(double i=startX;i<endX; i += step)
		{
			double output = Math.sin(i * 0.005)*amplitude + startY + ran.nextFloat()*noise;
			sheet.createRow(rowNum).createCell(0).setCellValue(output);
			rowNum++;
		}
		
		
	    workbook.write(fileOut);
	    fileOut.close();
	    workbook.close();
	    
	    return filePath;
	}
	


}