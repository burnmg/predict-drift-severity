package lin.test;

import java.util.LinkedList;
import java.util.Queue;

public class TestLinkedList
{
	public static void main(String args[])
	{
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(1);
		queue.add(2);
		System.out.println(queue.poll());
	}
}
