import com.lab309.network.UDPClient;
import java.net.InetAddress;

public class Client {

	private ByteBuffer [] bb_queue;
	private boolean [] b_allocated;
	private UDPClient udpc;
	private int size_call;
	private int size_return;
	
	private class RetrieveReturnPacket extends Runnable {
		private int i_cmdr;
		private UDPServer udps;
		
		public RetrieveReturnPacket (int i_cmdr) {
			this.i_cmdr = i_cmdr;
			this.udps = new UDPServer(Client.this.size_return, null);
		}
		
		public int getPort () {
			return this.udps.getPort();
		}
		
		@Override
		public static void run () {
			UPDDatagram dtg = this.udps.receive();
			Client.this.bb_queue[this.i_cmdr] = dtg.getBuffer();
			this.udps.close();
		}
	};
	
	public Client (int size_queue, int size_call, int size_return) {
		this.bb_queue = new CommandResult[size_queue];
		this.b_allocated = new boolean[size_queue];
		for (i = 0; i < b_allocated.length; i++) {
			this.bb_queue[i] = null;
			this.b_allocated[i] = false;
		}
		this.size_call = size_call;
		this.size_return = size_return;
	}
	
	public int connect (int port, InetAddress address) {
		this.udpc = new UDPClient(port, address, null);
		
		//TODO test connection
	}
	
	public ByteBuffer retrieveReturn (int i_queueIndex) {
		//blocks waiting for the return
		//TODO implement interruption
		while (bb_queue[i_queueIndex] == null);
		
		ByteBuffer result = bb_queue[i_queueIndex];
		b_allocated[i_queueIndex] = false;
		bb_queue[i_queueIndex] = null;
		return result;
	}
	
	private int allocateCmdr () throws QueueOverflowException {
		int i = 0;
		while (i < this.b_allocated.length && this.b_allocated[i] == true) {
			i++;
		}
		if (i == this.b_allocated.length) {
			throw new QueueOverflowException();
		}
		this.b_allocated[i] = true;
		return i;
	}
	
	//calls procedure without blocking, returns queue index used for retrieving the result later
	public int asyncCall (String s_procedureName, Serializable... args) throws QueueOverflowException {
		int i_cmdr = this.allocateCmdr();
		UDPDatagram dtg_call = new UDPDatagram(size_call);
		RetrieveReturnPacket run_udps = new RetrieveReturnPacket(i_cmdr);
		
		dtg_call.getBuffer().pushInt(run_udps.getPort());
		dtg_call.getBuffer().pushLatinString(s_procedureName);
		for (Serializable sr : args) {
			dtg_call.getBuffer().pushSerializable(sr);
		}
		this.udpc.send(dtg_call);
		
		new Thread(run_udps).start();
	}

	//calls procedure and blocks, waiting for the result
	public ByteBuffer call (String s_procedureName, Serializable... args) throws QueueOverflowException {
		return this.retrieveReturn(this.asyncCall(s_procedureName, args));
	}

}
