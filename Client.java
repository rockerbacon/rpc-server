import com.lab309.network.UDPClient;
import java.net.InetAddress;

public class Client {

	private ByteBuffer [] cmdr_queue;
	private UDPClient udpc;
	private int size_call;
	
	public Client (int i_queueSize, int size_call) {
		this.cmdr_queue = new CommandResult[i_queueSize];
		this.size_call = size_call;
	}
	
	public int connect (int port, InetAddress address) {
		this.udpc = new UDPClient(port, address, null);
		
		//TODO test connection
	}
	
	public ByteBuffer retrieveReturn (int i_queueIndex) {
		//blocks waiting for the return
		//TODO implement interruption
		while (cmdr_queue[i_queueIndex] == null);
		
		ByteBuffer result = cmdr_queue[i_queueIndex];
		cmdr_queue[i_queueIndex] = null;
		return result;
	}
	
	private int allocateCmdr () throws QueueOverflowException {
		int i = 0;
		while (i < this.cmdr_queue.length && this.cmdr_queue[i] != null) {
			i++;
		}
		if (i == this.cmdr_queue.length) {
			throw new QueueOverflowException();
		}
		this.cmdr_queue[i] = new ByteBuffer(1);
		return i;
	}
	
	//calls procedure without blocking, returns queue index used for retrieving the result later
	public int asyncCall (String s_procedureName, Serializable... args) throws QueueOverflowException {
		int i_cmdr = this.allocateCmdr();
		UDPDatagram dtg_call = new UDPDatagram(size_call);
		
		dtg_call.getBuffer().pushInt(i_cmdr);
		dtg_call.getBuffer().pushLatinString(s_procedureName);
		for (Serializable sr : args) {
			dtg_call.getBuffer().pushSerializable(sr);
		}
		this.udpc.send(dtg_call);
	}

	//calls procedure and blocks, waiting for the result
	public ByteBuffer call (String s_procedureName, Serializable... args) throws QueueOverflowException {
		return this.retrieveReturn(this.asyncCall(s_procedureName, args));
	}

}
