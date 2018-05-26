package Tests;

import com.lab309.middleware.QueueOverflowException;
import com.lab309.middleware.RPCClient;

import com.lab309.general.ByteBuffer;

import java.net.InetAddress;

import java.io.IOException;

public class TestClient {

	public static class Test1Return {
		public double sum;
		public double mult;
	}

	private RPCClient rpcc;
	
	public TestClient (int port) {
		try {
			this.rpcc = new RPCClient(5, 10000, 10000);
			this.rpcc.connect(port, InetAddress.getByName("localhost"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Test1Return test1 (double a, double b) {
		try {
			Test1Return result = new Test1Return();
		
			int i_return = this.rpcc.asyncCall("test1", a, b);	//call function without blocking for the return
		
			Thread.sleep(800);	//suppose an expensive operation that takes about 800ms to compute
		
			ByteBuffer bb_return = this.rpcc.retrieveReturn(0);	//blocks waiting for the return. Kind of like doing a join with the Server thread
		
			result.sum = bb_return.retrieveDouble();
			result.mult = bb_return.retrieveDouble();
		
			return result;
		} catch (QueueOverflowException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Sleep interrupted");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public String test2 (String s) {
		try {
			ByteBuffer bb_return = this.rpcc.call("test2", s);	//call function and blocks waiting for the result
			return bb_return.retrieveString();
		} catch (QueueOverflowException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void close() {
		this.rpcc.close();
	}
	
}
