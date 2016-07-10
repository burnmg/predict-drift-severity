package a.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import au.com.bytecode.opencsv.CSVReader;

public class Test
{
	public static void main(String[] args) throws IOException
	{
		
	     CSVReader reader = new CSVReader(new FileReader("/Users/rl/789/Results/10,100,10,100.arff/volSwitchIntervalDesc.csv"));
	     String [] nextLine;
	     while ((nextLine = reader.readNext()) != null) {
	        // nextLine[] is an array of values from the line
	        System.out.println(nextLine[0] + nextLine[1] + "etc...");
	     }
//		
//		Reader in = new FileReader("/Users/rl/789/Results/10,100,10,100.arff/volSwitchIntervalDesc.csv");
//		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
//		for (CSVRecord record : records) {
//		    System.out.println(record.get("Head"));
//		}


	}

}
