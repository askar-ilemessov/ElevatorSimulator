/*
 * $Id: Server.java 14511 2021-03-02 20:43:37Z $
 */

package assignment3Package;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;


/**
 * 
 * @author Ifiok, Yasin
 * This class represents the Server class
 *
 */
/**
 * 
 * constructor 
 *
 */
public class Server{
	public static byte[] msg = new byte[100]; 
	public Server() {

	}

	public void execute() {
		Thread ServerToScheduler = new Thread(new IntermediateHost(3006));
		ServerToScheduler.setName("ServerToScheduler");
		ServerToScheduler.start();
		Thread SchedulerToServer = new Thread(new IntermediateHost(3005));//
		SchedulerToServer.setName("SchedulerToServer");
		SchedulerToServer.start();
		
		Thread ServerToFloors = new Thread(new IntermediateHost(3008));
		ServerToFloors.setName("ServerToFloors");
		ServerToFloors.start();
		Thread FloorsToServer = new Thread(new IntermediateHost(3007));//
		FloorsToServer.setName("FloorsToServer");
		FloorsToServer.start();
		
		Thread ServerToElevator1 = new Thread(new IntermediateHost(3010));
		ServerToElevator1.setName("ServerToElevator1");
		ServerToElevator1.start();
		Thread Elevator1ToServer = new Thread(new IntermediateHost(3009));//
		Elevator1ToServer.setName("Elevator1ToServer");
		Elevator1ToServer.start();
		
		Thread ServerToElevator2 = new Thread(new IntermediateHost(3012));
		ServerToElevator2.setName("ServerToElevator2");
		ServerToElevator2.start();
		Thread Elevator2ToServer = new Thread(new IntermediateHost(3011));//
		Elevator2ToServer.setName("Elevator2ToServer");
		Elevator2ToServer.start();
		
		Thread ServerToElevator3 = new Thread(new IntermediateHost(3014));//
		ServerToElevator3.setName("ServerToElevator3");
		ServerToElevator3.start();
		Thread Elevator3ToServer = new Thread(new IntermediateHost(3013));//
		Elevator3ToServer.setName("Elevator3ToServer");
		Elevator3ToServer.start();
		
		try {
			DatagramSocket socket = new DatagramSocket(6009);	//Creates socket bound to port 69
			
//			while(true) {
				byte[] requestByteArray = "request".getBytes();
				boolean receieved = false; //defines a flag to check for receieving a actual packet vs a nothing to report packet ("null")
				DatagramPacket recievedPacket = new DatagramPacket(new byte[100], 100);	//Creates a packet to recieve into
				
				DatagramPacket requestPacketScheduler = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 3005);
				DatagramPacket requestPacketFloors = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 3007);
				DatagramPacket requestPacketElevator1 = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 3009);
				DatagramPacket requestPacketElevator2 = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 3011);
				DatagramPacket requestPacketElevator3 = new DatagramPacket(requestByteArray, requestByteArray.length, InetAddress.getLocalHost(), 3013);

				while(!receieved) {	//Loop until a non null packet is recieved
//					printPacket(requestPacket, true);
					socket.send(requestPacketScheduler);	//Send a request to the intermediate server
					socket.send(requestPacketFloors);
					socket.send(requestPacketElevator1);
					socket.send(requestPacketElevator2);
					socket.send(requestPacketElevator3);
					socket.receive(recievedPacket);	//Receive the response
					printPacket(recievedPacket, false);
					if(!(new String(recievedPacket.getData()).trim().equals("NA"))) {//If the response is not null, ie. a actual response
						receieved=true;	//Break out of loop
					}
					Thread.sleep(1000);
				}
				
				byte[] dataArray = recievedPacket.getData();	//get the data from the packet to analyze
				if(dataArray[0]==0 && (dataArray[1]== 1 || dataArray[1]== 2)) {	//If the prefix is not invalid
						int lengthOfFirstWord = checkFirstString(dataArray);	//Get the length of the first string
						int lengthOfSecondWord = checkSecondString(dataArray, lengthOfFirstWord);	//get the length of the second string
						if(dataArray[3+lengthOfFirstWord+lengthOfSecondWord] != 0) {	//if the last byte is not a trailing 0
							throw new IOException("Bad Packet (No trailing 0)");	//Throw exception telling the user that the packet was bad
						}
					}else {
						throw new IOException("Bad Packet (Invalid Request)");		//If the prefix was invalid tell the user the request was invalid
					}
					printPacket(recievedPacket, false);	//Print the packet  recieved
					String data = new String(processFirstString(recievedPacket.getData()), ( StandardCharsets.UTF_8));
					System.out.println("Server received: " + data);
					DatagramPacket packetToSend = null;
					switch(recievedPacket.getPort()) {
						case(3005):
							packetToSend = new DatagramPacket(recievedPacket.getData(), recievedPacket.getLength(), InetAddress.getLocalHost(), 3006);	//Creates a packet to send
							break;
						case(3007):
							packetToSend = new DatagramPacket(recievedPacket.getData(), recievedPacket.getLength(), InetAddress.getLocalHost(), 3008);	//Creates a packet to send
							break;
						case(3009):
							packetToSend = new DatagramPacket(recievedPacket.getData(), recievedPacket.getLength(), InetAddress.getLocalHost(), 3010);	//Creates a packet to send
							break;
						case(3011):
							packetToSend = new DatagramPacket(recievedPacket.getData(), recievedPacket.getLength(), InetAddress.getLocalHost(), 3012);	//Creates a packet to send
							break;
						case(3013):
							packetToSend = new DatagramPacket(recievedPacket.getData(), recievedPacket.getLength(), InetAddress.getLocalHost(), 3014);	//Creates a packet to send
							break;
							
						
					}
					
//					printPacket(packetToSend, true);	//Prints the packet to be send
						
//					socket.send(packetToSend);	//Sends the packet
					socket.send(packetToSend);	//Sends the packet
					socket.receive(recievedPacket);
				
//			}
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
		
}
/**
 * 	This method looks at the first string in the packet and determines it's length
 * @param dataArray takes in the packet's dataArray to be analyzed
 * @return the length of the first string
 */
	public static int checkFirstString(byte[] dataArray) {
		boolean atEndOfWord = false;	//set to true if at the end of the word
		int x=0;	
		while(!atEndOfWord) {		//While not at the end of the string
			if(dataArray[2+x]!=0) {	//Checks 2+xth index for end of string (2+x because the first 2 indexes are the read/write prefix
				msg[x] = dataArray[2+x];
				x++;	//If not increment
			}else {
				atEndOfWord=true;	//If so, set end of word flag
			}
		}
		return x;	//Return length of first string
	}
	
	public byte[] processFirstString(byte[] dataArray) {
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
 * This method looks at the second string in the packet and determines it's length
 * @param dataArray takes in the packet's dataArray to be analyzed
 * @param startOfWordIndex The index where the first word ended
 * @return the length of the first string
 */
	public static int checkSecondString(byte[] dataArray, int startOfWordIndex) {
		boolean atEndOfWord = false;	//set to true if at the end of the word
		int x=0;
		while(!atEndOfWord) {	//While not at the end of the string
			if(dataArray[3+x+startOfWordIndex]!=0) { //Checks 3+x+startOfWordIndex index for end of string 
													 //(3+x+startOfWordIndex because the first 2 indexes are the read/write prefix + length of first string + 0 between strings
			x++;		//If not, increment
			}else {
				atEndOfWord=true;	//If so, set end of word flag
			}
		}
		return x;	//Return length of second string
	}
	/**
	 * This method prints the information in recievedPacket, formatted according to if it was sent or recieved
	 * @param receivedPacket takes in the packet to be printed
	 * @param sending Boolean value that indicates if the packet is to be sent, or was recieved
	 */
	public static void printPacket(DatagramPacket receivedPacket, boolean sending) {
		if(!sending) {	//If the packet was recieved
		System.out.println("Server: Received the following packet (String): " + new String(receivedPacket.getData())); //Print data as string (Binary values will not appear correctly in the string, 
		System.out.println("Recived the following packet (Bytes): ");											//but this is what the assignment said to do)
		for (int z = 0; z < receivedPacket.getData().length - 1; z++) {					//Prints the byte array one index at a time
			System.out.print(receivedPacket.getData()[z] + ", ");
		}
		System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
		System.out.println("From:" + receivedPacket.getAddress() + " on port: "+ receivedPacket.getPort()); 	//Prints the address and port the packet was recieved on
		System.out.println("");	//Adds a newline between packet sending and receiving
		}else {			//The packet is being sent
			System.out.println("Server: Sending the following packet (String): " + new String(receivedPacket.getData()));//Print data as string (Binary values will not appear correctly in the string, 
			System.out.println("Sending the following packet (Bytes): ");										//but this is what the assignment said to do)
			for (int z = 0; z < receivedPacket.getData().length - 1; z++) {			//Prints the byte array one index at a time
				System.out.print(receivedPacket.getData()[z] + ", ");
			}
			System.out.println(receivedPacket.getData()[receivedPacket.getData().length - 1]);
			System.out.println("To:" + receivedPacket.getAddress() + " on port: "+ receivedPacket.getPort());	//Prints the address and port the packet is being sent to
			System.out.println("");	//Adds a newline between packet sending and receiving

		}
	}
	
	public static byte[] generateByteArray(String mssg, String port) {
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
				System.out.println("DataArray Length: " + dataArray.length);
				System.out.println("fileNameByteArray Length: " + fileNameByteArray.length);
				e.printStackTrace();
				System.exit(-1);
			}
		}
		dataArray[3 + x] = 0; //Adds a 0 in between the two strings
		String mode = "octet"; //Defines the mode as octet
		byte[] modeByteArray = mode.getBytes(); //Converts to byte array
		int y = 0;
		while (y < modeByteArray.length) {
			dataArray[3 + x + y] = modeByteArray[y]; //Starting at the 3+xth index, copy the mode byte array into the dataArray
			y++;
		}
		dataArray[x + y + 3] = 0;//Ends the byte array with a 0
		return dataArray;
	}
	
	public static void main(String[] str) {
		Server serv = new Server();
		serv.execute();
		
	}

}
