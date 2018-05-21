package com.lab309.middleware;

import com.lab309.adt.ConcurrentStaticQueue;
import com.lab309.general.ByteBuffer;
import com.lab309.network.UDPDatagram;
import com.lab309.network.UDPClient;
import com.lab309.network.UDPServer;

import java.io.Serializable;
import java.io.IOException;

import javax.crypto.IllegalBlockSizeException;

public abstract class RPCServer {

	private ConcurrentStaticQueue<UDPDatagram> queueCmd;
	private boolean broutineExecute;
	private UDPServer udps;
	private int size_args;
	private int size_return;

	public RPCServer (int size_queue, int size_return, int size_args) {
		this.queueCmd = new ConcurrentStaticQueue<UDPDatagram>(size_queue);
		this.broutineExecute = false;
		this.udps = null;
		this.size_args = size_args;
		this.size_return = size_return;
	}
	
	protected int getSizeArgs () {
		return this.size_args;
	}
	
	protected int getSizeReturn () {
		return this.size_return;
	}
	
	protected abstract ByteBuffer processCmd (String s_procedure, ByteBuffer args);
	
	private void executeNext () throws IOException {
		UDPDatagram dtgReceived = this.queueCmd.pop();	//blocks until there's something to pop
		int i_port = dtgReceived.getBuffer().retrieveInt();
		String s_proc = dtgReceived.getBuffer().retrieveLatinString();
		ByteBuffer result;
		UDPDatagram dtgSending;
		UDPClient uc;
		
		//process data
		result = this.processCmd(s_proc, dtgReceived.getBuffer());
		
		//post result
		uc = new UDPClient(i_port, dtgReceived.getSender(), null);
		dtgSending = new UDPDatagram(result);
		uc.send(dtgSending);
		uc.close();
	}
	
	public void startRoutineExecute () {
		broutineExecute = true;
		new Thread ( new Runnable () { @Override public void run () {
		
			while (RPCServer.this.broutineExecute) {
				try {
					RPCServer.this.executeNext();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		}}).start();
	}
	
	public void stopRoutineExecute () {
		broutineExecute = false;
	}
	
	public void startRoutineReceiveCmd (int i_port) {
		try {
			this.udps = new UDPServer (i_port, this.size_args, null); 
			new Thread ( new Runnable () { @Override public void run () {
				UDPDatagram dtg;
				try {
					while (RPCServer.this.udps != null) {
						dtg = RPCServer.this.udps.receive();
						RPCServer.this.queueCmd.push(dtg);
					}
				} catch (IOException e) {
					System.out.println("Socket closed");
				}
			}}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopRoutineReceiveCmd () {
		this.udps.close();
		this.udps = null;
	}
	
}
