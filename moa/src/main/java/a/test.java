package a;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class test
{

	public static void main(String[] args) throws IOException
	{
		BufferedWriter bWriter = new BufferedWriter(new FileWriter("/Users/rl/Desktop/output/dump.csv "));
		bWriter.write("1");
		bWriter.flush();
	}

}
