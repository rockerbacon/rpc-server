package Tests;

import com.lab309.middleware.RPCServer;

import com.lab309.general.ByteBuffer;

public class TestServer extends RPCServer {

	private String upperCase (String s) {
		return s.toUpperCase();
	}

	//override methods
	@Override
	public ByteBuffer processCmd (String s_procedure, ByteBuffer args) {
		ByteBuffer bb_return = new ByteBuffer(this.getSizeReturn());
		switch (s_procedure) {
			//returns sum and multiplication of the two values passed
			case "test1":
				//retrieve arguments
				double a = args.retrieveDouble();
				double b = args.retrieveDouble();
				//execute computation inside switch case
				double sum = a+b;
				try {
					Thread.sleep(1000); //supose an expensive operation that takes about 1s to compute
				} catch (InterruptedException e) {
					System.out.println("Thread interrupted");
				}
				double mult = a*b;	
				//pack return
				bb_return.pushDouble(sum);
				bb_return.pushDouble(mult);
			break;
			//returns string in uppercase
			case "test2":
				//retrieve arguments
				String s_arg = args.retrieveString();
				//execute computation in external method to avoid pollution inside switch case
				String s_return = this.upperCase(s_arg);
				//pack return
				bb_return.pushString(s_return);
			break;
		}
		return bb_return;
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
