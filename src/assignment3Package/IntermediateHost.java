/*
 * $Id: IntermediateHost.java 14511 2021-03-02 20:43:37Z $
 */

package assignment3Package;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 
 * @author Ifiok, Yasin
 * This class represents the IntermediateHost class
 *
 */
public class IntermediateHost implements Runnable {
	private Queue<DatagramPacket> queue;
	private int portNumber;
	private DatagramSocket recieveSocket;
	private DatagramSocket socket;
	private InetAddress local;
	private DatagramPacket recievedPacket, recievedPacket1;
	private DatagramPacket ackPacket;
	private String sourcePort;

	public IntermediateHost(int portNumber) {
		queue = new LinkedList<DatagramPacket>();
		this.portNumber = portNumber;
	}

	public void run() {
		try {
			recieveSocket = new DatagramSocket(portNumber); //creates a socket bound to port portNumber
			System.out.println(Thread.currentThread().getName() + " is running on port: " + portNumber);
			local = InetAddress.getLocalHost(); //Creates inetaddress containing localhost
			byte[] ackData = "ack".getBytes(); //Defines ack byte array
			byte[] negAck = "NA".getBytes();;
			recievedPacket = new DatagramPacket(new byte[100], 100); //create the packet to recieve into
			recievedPacket1 = new DatagramPacket(new byte[100], 100); //create the packet to recieve into

			while (true) { //loop infinitely  
				recievedPacket = new DatagramPacket(new byte[100], 100);
				recieveSocket.receive(recievedPacket);//Recieve a packet
//				printPacket(recievedPacket, false);
				if (new String(recievedPacket.getData()).trim().equals("request")) { //If the recievedPacket was a request
					if (queue.isEmpty()) { //If there are no packets to forward
						ackPacket = new DatagramPacket(negAck, negAck.length, local, recievedPacket.getPort()); //acknowledge that packet
//						printPacket(ackPacket, true);
						recieveSocket.send(ackPacket);//acknowledge that packet						
					} else {
						System.out.println(Thread.currentThread().getName()+": Request Receieved, there are " + queue.size()+ "messages waiting");
//						printPacket(queue.peek(), true);
						recieveSocket.send(queue.remove()); //Send the first packet waiting
						
					}
				} else { //if the recievedPacket was not a request, it must have been data
					ackPacket = new DatagramPacket(ackData, ackData.length, local, recievedPacket.getPort()); //acknowledge that packet
//					Acknowledgethat data was received
					recieveSocket.send(ackPacket);//acknowledge that packet

					if (recievedPacket.getPort() == 6009) { //If the data came from the server, it must be going to client
						System.out.println("new dest port is: " + getDestinationPort(recievedPacket.getData()));
						System.out.println("new dest data is: " + Arrays.toString(recievedPacket.getData()));
						recievedPacket.setPort(getDestinationPort(recievedPacket.getData())); //Set the packet's to appropraite receiver client
//						recievedPacket1 = new DatagramPacket(ackData, ackData.length, local, Integer.parseInt(sourcePort));
						
					} else{ //The data must have come from the client, so it is going to the server
						recievedPacket.setPort(6009); //Set the packets port to the server port
					}
					queue.add(recievedPacket); //Enqueue the packet
//					queue.add(recievedPacket1); //Enqueue the packet
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method prints the information in recievedPacket, formatted according to if it was sent or recieved
	 * 
	 * @param receivedPacket takes in the packet to be printed
	 * @param sending        Boolean value that indicates if the packet is to be sent, or was recieved
	 */
	public void printPacket(DatagramPacket receivedPacket, boolean sending) {
		if (!sending) { //If the packet was recieved
			System.out.println(Thread.currentThread().getName() + ": Received the following packet (String): " + new String(receivedPacket.getData())); //Print data as string (Binary values will not appear correctly in the string, 
			System.out.println("Recived the following packet (Bytes): "); //but this is what the assignment said to do)
			for (int z = 0; z < receivedPacket.getData().length - 1; z++) { //Prints the byte array one index at a time
				System.out.print(receivedPacket.getData()[z] + ", ");
			}
			System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
			System.out.println("From:" + receivedPacket.getAddress() + " on port: " + receivedPacket.getPort()); //Prints the address and port the packet was recieved on
			System.out.println(""); //Adds a newline between packet sending and receiving
		} else { //The packet is being sent
			System.out.println(Thread.currentThread().getName() + ": Sending the following packet (String): " + new String(receivedPacket.getData()));//Print data as string (Binary values will not appear correctly in the string, 
			System.out.println("Sending the following packet (Bytes): "); //but this is what the assignment said to do)
			for (int z = 0; z < receivedPacket.getData().length - 1; z++) { //Prints the byte array one index at a time
				System.out.print(receivedPacket.getData()[z] + ", ");
			}
			System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
			System.out.println("To:" + receivedPacket.getAddress() + " on port: " + receivedPacket.getPort()); //Prints the address and port the packet is being sent to
			System.out.println(""); //Adds a newline between packet sending and receiving

		}
	}
	
	/**
	 * 
	 * @param dataArray
	 * @return
	 */
	public int getDestinationPort(byte[] dataArray) {
		int lengthOfFirstWord = checkFirstString(dataArray);	//Get the length of the first string
		String port = new String((checkSecondString(dataArray, lengthOfFirstWord)), ( StandardCharsets.UTF_8));
		System.out.println("Port String: " + port);
		int portNumber = Integer.parseInt(port);
		System.out.println("destination port number is: " + portNumber);
		return portNumber;
	}
	
	/**
	 * 
	 * @param dataArray
	 * @param startOfWordIndex
	 * @return
	 */
	public byte[] checkSecondString(byte[] dataArray, int startOfWordIndex) {
		byte[] ret = new byte[4];
		boolean atEndOfWord = false;	//set to true if at the end of the word
		int x=0;
		while(!atEndOfWord) {	//While not at the end of the string
			if(dataArray[3+x+startOfWordIndex]!=0) { //Checks 3+x+startOfWordIndex index for end of string 
													 //(3+x+startOfWordIndex because the first 2 indexes are the read/write prefix + length of first string + 0 between strings
				ret[x] = dataArray[3+x+startOfWordIndex];
				x++;		//If not, increment
			}else {
				atEndOfWord=true;	//If so, set end of word flag
			}
		}
		return ret;	//Return length of second string
	}
	
	/**
	 * 
	 * @param dataArray
	 * @return
	 */
	public static int checkFirstString(byte[] dataArray) {
		boolean atEndOfWord = false;	//set to true if at the end of the word
		int x=0;	
		while(!atEndOfWord) {		//While not at the end of the string
			if(dataArray[2+x]!=0) {	//Checks 2+xth index for end of string (2+x because the first 2 indexes are the read/write prefix
				x++;	//If not increment
			}else {
				atEndOfWord=true;	//If so, set end of word flag
			}
		}
		return x;	//Return length of first string
	}

}
