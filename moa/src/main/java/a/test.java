package a;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yahoo.labs.samoa.instances.Attribute;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;

import moa.clusterers.clustream.WithKmeans;
import moa.core.FastVector;

public class test
{

	public static void main(String[] args)
	{
		
		Attribute att1 = new Attribute("att");
		FastVector attList = new FastVector();
		attList.add(att1);
		Instances dataset = new Instances("dataset", attList, 0);

		Instance inst = new DenseInstance(1);
		inst.setValue((Attribute)attList.elementAt(0), 123);
		
		
		WithKmeans clusterer = new WithKmeans();
		clusterer.kOption.setValue(2);
		clusterer.resetLearning();
		clusterer.trainOnInstance(inst);
	}

}
