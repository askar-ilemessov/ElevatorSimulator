/*
 * $Id: Client.java 14511 2021-03-02 20:43:37Z $
 */

package assignment3Package;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * @author yasin, Ifiok
 * this class represents the client class for the elevator system
 */

public class Client implements Runnable{
	//variable declarations
	DatagramSocket sndSocket; //Socket for sending data
	DatagramSocket rcvSocket; //Socket for receiving data
	
	/**
	 * 
	 * construct for the client class
	 * @param port for the String used
	 */
	public Client(int sndPort) {
		try {
			sndSocket = new DatagramSocket(sndPort);
			rcvSocket = new DatagramSocket(sndPort +1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * Send data using send socket
	 * @param mssg data to be sent
	 * @param destinationPort destination port of data
	 */
	public void sendData(String mssg, int destinationPort){

			 //Creates a new socket. This will be used for sending and receiving packets
			//			socket.setSoTimeout(5000); //Sets the timeout value to 5 seconds. If 5 seconds elapses and no packet arrives on receive, an exception will be thrown	
		InetAddress local = null;
			try {
				local = InetAddress.getLocalHost();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //Gets the local address of the computer 
			
				byte[] dataArray = mssg.getBytes();

				DatagramPacket packetToSend = new DatagramPacket(dataArray, dataArray.length, local, destinationPort); //Creates a packet from the dataArray, to be sent to the intermediate host

				try {
					sndSocket.send(packetToSend);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
	}

	/**
	 * 
	 * receive data using recv socket
	 * @param rcvqueue queue to add received data
	 */
	public void recv(BlockingQueue<String> rcvqueue) {
		DatagramPacket receivedPacket = new DatagramPacket(new byte[100], 100); //Creates a new packet for receiving
		try {
			rcvSocket.receive(receivedPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Receive the response
		String msg = new String(receivedPacket.getData(), StandardCharsets.UTF_8);
	    rcvqueue.add(msg);
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
