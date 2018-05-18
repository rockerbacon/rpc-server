import com.lab309.adt.ConcurrentStaticQueue;
import com.lab309.general.ByteBuffer;

public abstract class RPCServer {

	private ConcurrentStaticQueue<Command> queueCmd;
	private boolean broutineExecute;

	public RPCServer () {
		broutineExecute = false;
	}
	
	public abstract CommandResult processCmd (Command cmd);
	
	public void executeNext () {
		Command cmd = this.queueCmd.pop();
		CommandResult result;
		UDPDatagram dtgReceived = cmd.getDatagram();
		UDPDatagram dtgSending;
		UDPClient uc;
		
		//process data
		result = this.processCmd(cmd);
		
		//post result
		uc = new UDPClient(dtgReceived.getPort(), dtgReceived.getSender(), null);
		dtgSending = new UDPDatagram(new ByteBuffer(result.size()));
		uc.send(dtgSending);
		uc.close();
		
		return result;
	}
	
	public void startRoutineExecute () {
		broutineExecute = true;
		new Thread ( new Runnable () { @Override public void run () {
		
			while (this.broutineExecute) {
				this.executeNext();
			}
		
		}}).start();
	}
	
	public void stopRoutineExecute () {
		broutineExecute = false;
	}
	
}
