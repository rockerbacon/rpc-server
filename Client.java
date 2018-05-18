import com.lab309.network.UDPClient;
import java.net.InetAddress;

public class Client {

	private CommandResult [] cmdr_queue;
	private Integer i_queueSize;
	private UDPClient udpc;
	
	public Client (int i_queueSize) {
		this.cmdr_queue = new CommandResult[i_queueSize];
		i_queueSize = new Integer(0);
	}
	
	public int connect (int port, InetAddress address) {
		this.udpc = new UDPClient(port, address, null);
		
		//TODO test connection
	}
	
	public CommandResult retrieveReturn (Integer i_queueIndex) {
		//blocks waiting for the return
		//TODO implement interruption
		while (cmdr_queue[i_queueIndex] == null);
		
		CommandResult result = cmdr_queue[i_queueIndex];
		cmdr_queue[i_queueIndex] = null;
		return result;
	}
	
	//calls procedure without blocking, returns value used for retrieving the result later
	public Integer asyncCall (String s_procedureName, CommandArgument... args) {
		
	}

	//calls procedure and blocks, waiting for the result
	public CommandResult call (String s_procedureName, CommandArgument... args) {
		return this.retrieveReturn(this.asyncCall(s_procedureName, args));
	}

}
