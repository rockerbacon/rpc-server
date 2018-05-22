package com.lab309.adt;

public class ProductionQueue<T> {

	private ConcurrentStaticQueue<T> sque;
	private Object lk_pop;
	private Object lk_push;
	
	public ProductionQueue (int size_queue) {
		this.sque = new ConcurrentStaticQueue<T>(size_queue);
	}
	
	public T pop () throws IndexOutOfBoundsException {
		T obj = this.sque.pop();
		while (obj == null) {
			lk_pop.wait();
			obj = this.sque.pop();
		}
		if (!sque.full()) {
			lk_push.notify();
		}
		return obj;
	}
	
	public void push (T obj) throws IndexOutOfBoundsException {
		if (this.sque.full()) {
			lk_push.wait();
		}
		this.sque.push(obj);
		if (!this.sque.full()) lk_push.notify();
		lk_pop.notify();
	}

}
