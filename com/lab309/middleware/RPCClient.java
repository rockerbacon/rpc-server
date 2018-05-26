package com.lab309.middleware;

import com.lab309.general.ByteBuffer;
import com.lab309.network.UDPClient;
import com.lab309.network.UDPServer;
import com.lab309.network.UDPDatagram;

import java.net.InetAddress;
import java.io.Serializable;
import java.io.IOException;

public class RPCClient {

	private ByteBuffer [] bb_queue;
	private boolean [] b_allocated;
	private UDPClient udpc;
	private int size_args;
	private int size_return;
	
	private class RetrieveReturnPacket implements Runnable {
		private int i_cmdr;
		private UDPServer udps;
		
		public RetrieveReturnPacket (int i_cmdr) {
			try {
				this.i_cmdr = i_cmdr;
				this.udps = new UDPServer(RPCClient.this.size_return, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public int getPort () {
			return this.udps.getPort();
		}
		
		@Override
		public void run () {
			try {
				UDPDatagram dtg = this.udps.receive();
				System.out.println("Client received an answer on index "+this.i_cmdr);	//debug
				RPCClient.this.bb_queue[this.i_cmdr] = dtg.getBuffer();
				this.udps.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	public RPCClient (int size_queue, int size_return, int size_args) {
		this.bb_queue = new ByteBuffer[size_queue];
		this.b_allocated = new boolean[size_queue];
		for (int i = 0; i < b_allocated.length; i++) {
			this.bb_queue[i] = null;
			this.b_allocated[i] = false;
		}
		this.size_args = size_args;
		this.size_return = size_return;
	}
	
	public void connect (int port, InetAddress address) throws IOException {
		this.udpc = new UDPClient(port, address, null);
		
		//TODO test connection
	}
	
	
	public void close () {
		this.udpc.close();
	}
	
	public ByteBuffer retrieveReturn (int i_queueIndex) {
		//blocks waiting for the return
		//TODO implement interruption
		System.out.println("Waiting for answer to be published on index "+i_queueIndex);	//debug
		while (bb_queue[i_queueIndex] == null);
		System.out.println("Processing answer published on index "+i_queueIndex);	//debug
		
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
	public int asyncCall (String s_procedureName, Serializable... args) throws QueueOverflowException, IOException {
		int i_cmdr = this.allocateCmdr();
		UDPDatagram dtg_call = new UDPDatagram(size_args);
		RetrieveReturnPacket run_udps = new RetrieveReturnPacket(i_cmdr);
		
		dtg_call.getBuffer().pushInt(run_udps.getPort());
		dtg_call.getBuffer().pushLatinString(s_procedureName);
		for (Serializable sr : args) {
			dtg_call.getBuffer().pushSerializable(sr);
		}
		
		System.out.println("Client sent a request");	//debug
		this.udpc.send(dtg_call);
		
		new Thread(run_udps).start();
		
		return i_cmdr;
	}

	//calls procedure and blocks, waiting for the result
	public ByteBuffer call (String s_procedureName, Serializable... args) throws QueueOverflowException, IOException {
		return this.retrieveReturn(this.asyncCall(s_procedureName, args));
	}

}
