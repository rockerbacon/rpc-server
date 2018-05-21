# RPC SERVER

Here is provided a standard generic system for making RPCs.
The system works as follows:
1. Client picks unused index *i* in a shared array;
1. Client packs an UDP datagram containing an answer port followed by the name of the procedure and the arguments used by the procedure and sends it to the server;
1. The server adds the received datagram to a queue;
1. The server gets an item in the queue, executes the procedure, and sends an UDP datagram containing the procedure's return to the client through the previously specified port.

The RPCServer class is abstract and the method *ByteBuffer processCmd (String s_procedure, ByteBuffer args)* must be implemented by a class that extends RPCServer. The implementation should follow the format:
```java
ByteBuffer processCmd (String s_procedure, ByteBuffer args) {
	switch (s_procedure) {
		case "1st procedure name":
			//retrieve arguments from args
			//execute code with retrieved args
		break;
		case "another procedure name":
			//retrieve arguments from args
			//execute code with retrieved args
		break;
		//and so on
	}
}
```

For examples on the usage refer to the Tests folder
