package summer.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.formula.ptg.StringPtg;

import summer.magSeed.MagSeed;



public class Main
{

	public static void main(String[] args) throws IOException
	{
		FileReader reader = new FileReader("/Users/rl/Desktop/data/fludata.csv");
		CSVFormat csvFileFormat = CSVFormat.RFC4180.withFirstRecordAsHeader();
		CSVParser parser = new CSVParser(reader, csvFileFormat);
		List<CSVRecord> records = parser.getRecords();
		
		ArrayList<Double> data = new ArrayList<Double>(records.size());
		
		for(int i=0; i<records.size();i++)
		{
			data.add(Double.parseDouble(records.get(i).get("New Zealand")));
		}
		
		parser.close();
		
		MagSeed magSeed = new MagSeed(0.1, 32, 32);
		
		int i=0;
		for(double item : data)
		{
			if(magSeed.setInput(item))
			{
				System.out.println(i+","+item);
			}
			i++;
		}

	}

}
