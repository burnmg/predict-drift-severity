package moa.streams;

import com.yahoo.labs.samoa.instances.Instance;

import moa.core.Example;

public interface DriftingStream extends ExampleStream<Example<Instance>>
{ 
	public void addPartialDrift();
	public void addFullDirft();
}
