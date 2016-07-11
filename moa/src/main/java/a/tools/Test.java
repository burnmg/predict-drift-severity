package a.tools;

import java.io.FileReader;
import java.io.IOException;
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
