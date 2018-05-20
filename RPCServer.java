import com.lab309.adt.ConcurrentStaticQueue;
import com.lab309.general.ByteBuffer;
import com.lab309.network.UDPDatagram;
import com.lab309.network.UDPClient;
import com.lab309.network.UDPServer;

import java.io.IOException;

public abstract class RPCServer {

	private ConcurrentStaticQueue<UDPDatagram> queueCmd;
	private boolean broutineExecute;
	private UPDServer udps;
	private int size_args;

	public RPCServer (int size_queue, int size_args) {
		this.queueCmd = new ConcurrentStaticQueue<UDPDatagram>(size_queue);
		this.broutineExecute = false;
		this.udps = null;
		this.size_args = size_args;
	}
	
	public abstract ByteBuffer processCmd (UDPDatagram cmd);
	
	public void executeNext () {
		UDPDatagram dtgReceived = this.queueCmd.pop();	//blocks until there's something to pop
		ByteBuffer result;
		UDPDatagram dtgSending;
		UDPClient uc;
		
		//process data
		result = this.processCmd(dtgReceived);
		dtgReceived.getBuffer().rewind();
		
		//post result
		uc = new UDPClient(dtgReceived.getBuffer().retrieveInt(), dtgReceived.getSender(), null);
		dtgSending = new UDPDatagram(result);
		uc.send(dtgSending);
		uc.close();
	}
	
	public void startRoutineExecute () {
		broutineExecute = true;
		new Thread ( new Runnable () { @Override public void run () {
		
			while (RPCServer.this.broutineExecute) {
				RPCServer.this.executeNext();
			}
		
		}}).start();
	}
	
	public void stopRoutineExecute () {
		broutineExecute = false;
	}
	
	public void startRoutineReceiveCmd (int i_port) {
		this.udps = new UDPServer (i_port, this.size_args, null); 
		new Thread ( new Runnable () { @Override public void run () {
			UDPDatagram dtg;
			try {
				while (RPCServer.this.udps != null) {
					dtg = RPCServer.udps.receive();
					RPCServer.this.queueCmd.push(dtg);
				}
			} catch (IOException e) {
				System.out.println("Socket closed");
			}
		}}).start();
	}
	
	public void stopRoutineReceiveCmd () {
		this.udps.close();
		this.udps = null;
	}
	
}
