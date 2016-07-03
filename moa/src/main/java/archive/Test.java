package archive;

import moa.streams.MultipleConceptDriftStreamGenerator3;
import moa.tasks.WriteStreamToARFFFile3;

public class Test
{

	public static void main(String[] args)
	{
		MultipleConceptDriftStreamGenerator3 streamGenerator3 = new MultipleConceptDriftStreamGenerator3();
		streamGenerator3.getOptions().resetToDefaults();
		streamGenerator3.prepareForUse();
		
		WriteStreamToARFFFile3 task = new WriteStreamToARFFFile3(streamGenerator3);
		task.getOptions().resetToDefaults();

		task.arffFileOption.setValue("TestFile.arff");
		task.maxInstancesOption.setValue(500000);
		
		task.doTask();

	}

}
