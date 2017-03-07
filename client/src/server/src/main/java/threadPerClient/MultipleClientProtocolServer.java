package threadPerClient;

import java.io.*;
import java.net.*;

import database.EchoProtocol;
import protocols.ServerProtocolFactory;


class AsyncServerProtocolFactory implements ServerProtocolFactory {
	public EchoProtocol create(){
		return new EchoProtocol();
	}
}



class ConnectionHandler implements Runnable {
	
	private BufferedReader in;
	private PrintWriter out;
	Socket clientSocket;
	EchoProtocol protocol;
	
	public ConnectionHandler(Socket acceptedSocket, EchoProtocol p)
	{
		in = null;
		out = null;
		clientSocket = acceptedSocket;
		protocol = p;
		System.out.println("Accepted connection from client!");
		System.out.println("The client is from: " + acceptedSocket.getInetAddress() + ":" + acceptedSocket.getPort());
	}
	
	public void run()
	{

		String msg;
		
		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}

		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 
		
		System.out.println("Connection closed - bye bye...");
		close();

	}
	
	public void process() throws IOException
	{
		String msg;
		
		while ((msg = in.readLine()) != null)
		{
			System.out.println("Received \"" + msg + "\" from client");
			
			protocol.processMessage(msg, v -> {
				out.println(v);
            });
			
			if (protocol.shouldClose())
			{
				break;
			}
			
		}
	}
	
	// Starts listening
	public void initialize() throws IOException
	{
		// Initialize I/O
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
		System.out.println("I/O initialized");
	}
	
	// Closes the connection
	public void close()
	{
		try {
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
			
			clientSocket.close();
		}
		catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
	}
	
}

class MultipleClientProtocolServer implements Runnable {
	private ServerSocket serverSocket;
	private int listenPort;
	private AsyncServerProtocolFactory factory;
	
	
	public MultipleClientProtocolServer(int port, AsyncServerProtocolFactory p)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (true)
		{
			try {
				ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
            new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
	}
	

	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}
	
	public static void main(String[] args) throws IOException
	{
		// Get port
		int port = Integer.decode(args[0]).intValue();
		
		MultipleClientProtocolServer server = new MultipleClientProtocolServer(port, new AsyncServerProtocolFactory());
		Thread serverThread = new Thread(server);
      serverThread.start();
		try {
			serverThread.join();
		}
		catch (InterruptedException e)
		{
			System.out.println("Server stopped");
		}
		
		
				
	}
}



