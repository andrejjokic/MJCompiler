package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
	
	private List<T> stack = new ArrayList<>();
	
	public T peek() {
		if (stack.size() == 0) 
			return null;
		else
			return stack.get(stack.size() - 1);
	}
	
	public T pop() {
		if (stack.size() == 0) 
			return null;
		else
			return stack.remove(stack.size() - 1);
	}
	
	public void push(T item) {
		stack.add(item);
	}
	
	public List<T> getStackList() {
		return this.stack;
	}
}
