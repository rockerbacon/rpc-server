package Tests;

import com.lab309.middleware.RPCServer;

import com.lab309.general.ByteBuffer;

import java.io.Serializable;

public class TestServer extends RPCServer {

	private String upperCase (String s) {
		return s.toUpperCase();
	}

	//override methods
	@Override
	public Serializable processCmd (String s_procedure, Object[] args) {
		switch (s_procedure) {
		
			//returns sum and multiplication of the two values passed
			case "test1":
			
				//retrieve arguments
				double a = ((Double)args[0]).doubleValue();
				double b = ((Double)args[1]).doubleValue();
				
				//execute computation inside switch case
				Test1Return ret = new Test1Return();
				ret.sum = a+b;
				
				//SIMULATING EXPENSIVE OPERATION
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted");
				}
				//SIMULATING EXPENSIVE OPERATION
				
				ret.mult = a*b;
				
				//return result
				return ret;
			
			//returns string in uppercase
			case "test2":
			
				//retrieve arguments
				String s_arg = (String)args[0];
				
				//execute computation in external method to avoid pollution inside switch case
				String s_return = this.upperCase(s_arg);
				
				//return result
				return s_return;
			
		}
		
		//returns null if the function name that was passed was not declared. This will be used by the server to raise an error
		return null;
		
	}
	
	public TestServer (int port) {
		super(5, 10000, 10000);
		this.startRoutineReceiveCmd(port);
		this.startRoutineExecute();
	}
	
	public void close() {
		this.stopRoutineReceiveCmd();
		this.stopRoutineExecute();
	}
	
}
