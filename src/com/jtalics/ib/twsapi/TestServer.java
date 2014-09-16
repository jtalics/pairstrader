package com.jtalics.ib.twsapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	public static void main(String[] args) { // IB port is usually 7496
		int portNumber = Integer.parseInt(args[0]);

		BufferedReader in=null;
		PrintWriter out=null;
		try {
		
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("LISTENING FOR CONNECTION on port "+serverSocket.getLocalPort());
			Socket clientSocket = serverSocket.accept();
			System.out.println("ACCEPTED CONNECTION from remote host:port "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
			// 'out' is where we talk to the client
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			// 'in' is where we receive bytes from the client
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		
		String inputLine;
		try {
			System.out.println("SENDING: hello.");
			out.println("hello");
			while ((inputLine = in.readLine()) != null) {
				System.out.println("RECEIVED: "+inputLine);
				System.out.println("SENDING: ack.");
				out.println("ack");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("TERMINATED.");
	}
}
