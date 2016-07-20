

package moa.classifiers;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import moa.MOAObject;
import moa.core.Example;

import com.yahoo.labs.samoa.instances.InstancesHeader;

import moa.core.Measurement;
import moa.core.ObjectRepository;
import moa.core.StringUtils;
import moa.gui.AWTRenderer;
import moa.learners.Learner;
import moa.options.AbstractOptionHandler;

import com.github.javacliparser.IntOption;

import moa.tasks.TaskMonitor;

import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.MultiLabelPrediction;
import com.yahoo.labs.samoa.instances.Prediction;

import moa.core.Utils;

public abstract class MyAbstractClassifier extends AbstractClassifier implements Classifier 
{

	public abstract void notifyConceptDrift();
	public abstract boolean getIsDrift();

}
