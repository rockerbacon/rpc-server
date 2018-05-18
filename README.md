#RPC SERVER

Here is provided a standard generic system for making RPCs.
The system works as follows:
`
Client picks unused index 'i' in a shared array
Client packs a UDP datagram with containing 'i' followed by the name of the procedure and the arguments used by the procedure and sends it to the server
The server adds the received datagram to a queue
The server gets an item in the queue, calls the procedure with the needed arguments and returns a UDP datagram with the index followed by the procedure's return to the client 
`
