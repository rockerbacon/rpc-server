package com.lab309.adt;

import com.lab309.multithreading.ProductionNotifier;

public class ProductionQueue<T> {

	private ConcurrentStaticQueue<T> sque;
	private ProductionNotifier cs;
	private Object lk_pop;
	private Object lk_push;
	
	public ProductionQueue (int size_queue) {
		this.sque = new ConcurrentStaticQueue<T>(size_queue);
		this.cs = new ProductionNotifier(size_queue);
	}
	
	public T pop () throws IndexOutOfBoundsException {
		T obj;
		this.cs.requestProduct();
		obj = this.sque.pop();
		return obj;
	}
	
	public void push (T obj) throws IndexOutOfBoundsException {
		this.cs.requestSpace();
		this.sque.push(obj);
	}

}
