package com.lab309.multithreading;

public class ProductionNotifier {

	private Integer i_count;
	private int size_count;
	private Object lk_up;
	private Object lk_down;
	
	public ProductionNotifier (int size_count) {
		this.i_count = 0;
		this.size_count = size_count;
		this.lk_up = new Object();
		this.lk_down = new Object();
	}
	
	public void requestSpace () {
		boolean b_wait;
		
		//checks for access to critical area
		synchronized(this.i_count) {
			b_wait = this.i_count.intValue() == this.size_count;
		}
		//wait for access to critical area
		if (b_wait) {
			try {
				synchronized(this.lk_up) {
					this.lk_up.wait();
				}	
			} catch (InterruptedException e) {
				//TODO log interruption
				System.err.println("Space request aborted");
			}
		}
		
		synchronized(this.i_count) {
			//accesses critical area
			this.i_count = this.i_count.intValue()+1;
		}
		synchronized (this.lk_down) {
			//wake a waiting thread
			this.lk_down.notify();
		}
	}
	
	public void requestProduct () {
		boolean b_wait;
		
		//checks for access to critical area
		synchronized(this.i_count) {
			b_wait = this.i_count.intValue() == 0;
		}
		//wait for access to critical area
		if (b_wait) {
			try {
				synchronized(this.lk_down) {
					this.lk_down.wait();
				}	
			} catch (InterruptedException e) {
				//TODO log interruption
				System.err.println("Space request aborted");
			}
		}
		
		synchronized(this.i_count) {
			//accesses critical area
			this.i_count = this.i_count.intValue()-1;
		}
		synchronized (this.lk_up) {
			//wake a waiting thread
			this.lk_up.notify();
		}
	}
}
