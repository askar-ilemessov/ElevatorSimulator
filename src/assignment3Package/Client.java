/*
 * $Id: Client.java 14511 2021-03-02 20:43:37Z $
 */

package assignment3Package;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * @author yasin, Ifiok
 * this class represents the client class for the elevator system
 */

public class Client implements Runnable{
	//variable declarations
	private String portNumber;
	public byte[] msg;
	public BlockingQueue<String> sndqueue ;
	public BlockingQueue<String> rcvqueue ;
	DatagramSocket socket;
	boolean conditional = false;
	private int toServer;
	private int fromServer;
	
	/**
	 * 
	 * construct for the client class
	 * @param port for the String used
	 */
	public Client(String port, int toServer, int fromServer) {
		sndqueue = new ArrayBlockingQueue<String>(10);
		rcvqueue = new ArrayBlockingQueue<String>(10);
		portNumber = port;
		this.toServer = toServer;
		this.fromServer = fromServer;
	
		try {
			socket = new DatagramSocket(Integer.parseInt(port));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			System.out.println(port);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("Client initialized");
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 * This method returns a split message
	 */
	public String[] processString(String message) {
		return message.split(":");
	}
	
	private void sendData(String mssg, String destinationPort) throws Exception {
//		String result = null;
		try {
			 //Creates a new socket. This will be used for sending and recieving packets
			//			socket.setSoTimeout(5000); //Sets the timeout value to 5 seconds. If 5 seconds elapses and no packet arrives on receive, an exception will be thrown
			InetAddress local = InetAddress.getLocalHost(); //Gets the local address of the computer 
			boolean receieved = false; //defines a flag to check for receieving a actual packet vs a nothing to report packet ("null")
			DatagramPacket receivedPacket = new DatagramPacket(new byte[100], 100); //Creates a new packet for receiving
			if(!mssg.equals("request")) {
			
				byte[] dataArray = generateByteArray(mssg, destinationPort);

				DatagramPacket packetToSend = new DatagramPacket(dataArray, dataArray.length, local, toServer); //Creates a packet from the dataArray, to be sent to the intermediate host
				DatagramPacket replyPacket = new DatagramPacket(new byte[17], 17); //Creates a packet to recieve the acknowledgement in.

//				printPacket(packetToSend, true); //Prints the contents of the packet to be sent

				socket.send(packetToSend); //Sends the packetToSend
				socket.receive(replyPacket); //Receive the ack from the intermediate host
//				printPacket(replyPacket, false);
				
				
				
			}
			byte[] requestByteArray = "request".getBytes(); //Convert "request" into a byte array to send
//				while (!receieved) { //Loop until a not null packet is recieved
					DatagramPacket requestPacket = new DatagramPacket(requestByteArray, requestByteArray.length, local, fromServer);
					try{
						socket.send(requestPacket); //Send a request to the intermediate server
					}catch(NullPointerException e) {
						System.out.println("problem!!");
						e.printStackTrace();
						System.exit(-1);
					}
//										printPacket(requestPacket, true);
					socket.receive(receivedPacket); //Recieve the response
//						  				printPacket(receivedPacket, false);
					if (!(new String(receivedPacket.getData()).trim().equals("NA") || new String(receivedPacket.getData()).trim().equals("ack"))) {//If the response is not null, ie. a actual response
//						printPacket(receivedPacket, false);
						 //Break out of loop
						msg = Arrays.copyOfRange(receivedPacket.getData(), 0, receivedPacket.getLength());
						System.out.println(Arrays.toString(receivedPacket.getData()));
					    rcvqueue.add(ExtractMessage(msg));
					    
					  
					}
					Thread.sleep(1000);
//				}
//				printPacket(receivedPacket, false); //Prints the packet recieved
//			System.out.println("Client is finished. It has receievd " + numberOfSuccessfulPackets + " successful packets. (Should be 10)");
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sndrecv() {
		while(!sndqueue.isEmpty()) {
			String[] res = process(sndqueue.remove());
			try {
				System.out.println("Sending: "+ res[0]);
				sendData(res[0], res[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}try {
				sendData("request", "null");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	public String[] process(String input) {
		String[] hold = input.split(":");
		return hold;
	}
//	public void sendData(String mssg, String destinationPort) throws Exception {
////		String result = null;
//		conditional = true;
//		try {
//			 //Creates a new socket. This will be used for sending and recieving packets
//			//			socket.setSoTimeout(5000); //Sets the timeout value to 5 seconds. If 5 seconds elapses and no packet arrives on receive, an exception will be thrown
//			InetAddress local = InetAddress.getLocalHost(); //Gets the local address of the computer 
//			boolean receieved = false; //defines a flag to check for receieving a actual packet vs a nothing to report packet ("null")
//			DatagramPacket receivedPacket = new DatagramPacket(new byte[100], 100); //Creates a new packet for receiving
//			if(!mssg.equals("request")) {
//			
//				byte[] dataArray = generateByteArray(mssg, destinationPort);
//
//				DatagramPacket packetToSend = new DatagramPacket(dataArray, dataArray.length, local, 2003); //Creates a packet from the dataArray, to be sent to the intermediate host
//				DatagramPacket replyPacket = new DatagramPacket(new byte[17], 17); //Creates a packet to recieve the acknowledgement in.
//
////				printPacket(packetToSend, true); //Prints the contents of the packet to be sent
//
//				socket.send(packetToSend); //Sends the packetToSend
//				socket.receive(replyPacket); //Receive the ack from the intermediate host
////				printPacket(replyPacket, false);
//				
//				
//				
//			}
//			byte[] requestByteArray = "request".getBytes(); //Convert "request" into a byte array to send
//				while (!receieved) { //Loop until a not null packet is recieved
//					DatagramPacket requestPacket = new DatagramPacket(requestByteArray, requestByteArray.length, local, 2004);
//					try{
//						socket.send(requestPacket); //Send a request to the intermediate server
//					}catch(NullPointerException e) {
//						System.out.println("problem!!");
//						e.printStackTrace();
//						System.exit(-1);
//					}
////										printPacket(requestPacket, true);
//					socket.receive(receivedPacket); //Recieve the response
////						  				printPacket(receivedPacket, false);
//					if (!(new String(receivedPacket.getData()).trim().equals("NA") || new String(receivedPacket.getData()).trim().equals("ack"))) {//If the response is not null, ie. a actual response
////						printPacket(receivedPacket, false);
//						receieved = true; //Break out of loop
//						msg = Arrays.copyOfRange(receivedPacket.getData(), 0, receivedPacket.getLength());
//						System.out.println(Arrays.toString(receivedPacket.getData()));
//					    rcvqueue.add(ExtractMessage(msg));
//					    
//					  
//					}
//					Thread.sleep(1000);
//				}
////				printPacket(receivedPacket, false); //Prints the packet recieved
////			System.out.println("Client is finished. It has receievd " + numberOfSuccessfulPackets + " successful packets. (Should be 10)");
//			
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
//		conditional = false;
//	}

	

	public byte[] checkFirstString(byte[] dataArray) {
		boolean atEndOfWord = false;	//set to true if at the end of the word
		int x=0;
		byte[] res = new byte[100];
		while(!atEndOfWord) {		//While not at the end of the string
			if(dataArray[2+x]!=0) {	//Checks 2+xth index for end of string (2+x because the first 2 indexes are the read/write prefix
				res[x] = dataArray[2+x];
				x++;	//If not increment
			}else {
				atEndOfWord=true;	//If so, set end of word flag
			}
		}
		return res;	//Return length of first string
	}
	
	/**
	 * 
	 * @param mssg
	 * @return
	 */
	public String ExtractMessage(byte[] mssg) {
		String msg = new String(checkFirstString(mssg), ( StandardCharsets.UTF_8)); 
		System.out.println("Extracted mssg is: " + msg);
		return msg;
	}
	/**
	 * This method prints the information in recievedPacket, formatted according to if it was sent or recieved
	 * 
	 * @param receivedPacket takes in the packet to be printed
	 * @param sending        Boolean value that indicates if the packet is to be sent, or was recieved
	 */

	public static void printPacket(DatagramPacket receivedPacket, boolean sending) {
		if (!sending) { //If the packet was recieved
			System.out.println("Client: Received the following packet (String): " + new String(receivedPacket.getData())); //Print data as string (Binary values will not appear correctly in the string, 
			System.out.println("Recived the following packet (Bytes): "); //but this is what the assignment said to do)
			for (int z = 0; z < receivedPacket.getData().length - 1; z++) { //Prints the byte array one index at a time
				System.out.print(receivedPacket.getData()[z] + ", ");
			}
			System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
			System.out.println("From:" + receivedPacket.getAddress() + " on port: " + receivedPacket.getPort()); //Prints the address and port the packet was recieved on
			System.out.println(""); //Adds a newline between packet sending and receiving
		} else { //The packet is being sent
			System.out.println("Client: Sending the following packet (String): " + new String(receivedPacket.getData()));//Print data as string (Binary values will not appear correctly in the string, 
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
	 * @param mssg
	 * @param destinaationPort
	 * @return
	 */
	public static byte[] generateByteArray(String mssg, String destinaationPort) {
		byte[] dataArray = new byte[100]; //Creates the byte array that contains the data to send.
		dataArray[0] = 0;
		dataArray[1] = 1;
		byte[] fileNameByteArray = mssg.getBytes(); //Converts test.txt to an array of bytes
		int x = 0;
		while (x < fileNameByteArray.length) {
			try{//puts the new array of bytes into the working dataArray. (x is saved for future use)
				dataArray[2 + x] = fileNameByteArray[x]; //Starts at index 2, and iterates through the fileName byte array
				x++;
			}catch(ArrayIndexOutOfBoundsException e) {
//				System.out.println("DataArray Length: " + dataArray.length);
//				System.out.println("fileNameByteArray Length: " + fileNameByteArray.length);
				e.printStackTrace();
				System.exit(-1);
			}
		}
		dataArray[3 + x] = 0; //Adds a 0 in between the two strings
		byte[] modeByteArray = destinaationPort.getBytes(); //Converts to byte array
		int y = 0;
		while (y < modeByteArray.length) {
			dataArray[3 + x + y] = modeByteArray[y]; //Starting at the 3+xth index, copy the mode byte array into the dataArray
			y++;
		}
		dataArray[x + y + 3] = 0;//Ends the byte array with a 0
		return dataArray;
	}
	
	@Override
	public void run() {
//		System.out.println("Here4");
		while(true) {
			sndrecv();
		}
	}
	
//	public static void main(String[] str) {
//		Client c = new Client(2002);
//		String m = "testing,1,2";
//		try {
//			c.sendData(m, "2002");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
