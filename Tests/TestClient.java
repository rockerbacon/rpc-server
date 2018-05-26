package Tests;

import com.lab309.middleware.QueueOverflowException;
import com.lab309.middleware.RPCClient;

import com.lab309.general.ByteBuffer;

import java.net.InetAddress;

import java.io.IOException;

public class TestClient {

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

			//SIMULATING EXPENSIVE OPERATION		
			Thread.sleep(800);
			//SIMULATING EXPENSIVE OPERATION
		
			result = (Test1Return)this.rpcc.retrieveReturn(0);	//blocks waiting for the return. Similar to a join between the main thread and the server thread
		
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
			String result = (String)this.rpcc.call("test2", s);	//call function and blocks waiting for the result
			return result;
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
