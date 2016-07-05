package com.datadisplay.console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerTest {

	public static void main(String[] args) {
		
		int port = 1432;
		DatagramSocket serverSocket = null;
		try {
	        serverSocket = new DatagramSocket(port);
	        byte[] receiveData = new byte[1024];

	        System.out.printf("Listening on udp:%s:%d%n",
	                InetAddress.getLocalHost().getHostAddress(), port);     
	        DatagramPacket receivePacket = new DatagramPacket(receiveData,
	                           receiveData.length);

	        while(true)
	        {
	              serverSocket.receive(receivePacket);
	              String sentence = new String( receivePacket.getData(), 0,
	                                 receivePacket.getLength() );
	              System.out.println("RECEIVED: " + sentence);
	             
	        }
	      } catch (IOException e) {
	              System.out.println(e);
	      } finally {
	    	  if (serverSocket != null) serverSocket.close();
	      }
		
	}

}
