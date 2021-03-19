package View;


import java.util.ArrayList;

import Controller.Scheduler;

import assignment3Package.Client;
import assignment3Package.IntermediateHost;



/**
 * The floor subsystem is used to simulate the arrival of passengers to the elevators and for simulating all button presses and lamps.
 * 
 */

/**
 * @author madelynkrasnay
 *
 */
public class Floors implements Runnable {
//public class Floors  {
	
	
	//local state variables to reflect our outputs:
	//an array of lamps for all floors where each 
	//floor had 2 lamps: one for going up, one for going down.
	private boolean [] [] lamps;
	private ArrayList<Boolean> elevatorDirectionIndicator = new ArrayList<>();
	private ArrayList<Integer> elevatorFloorIndicator = new ArrayList<>();
	
	private Scheduler scheduler;
//	private String scheduler;
	private ArrayList<SimulatedArrival> arrivals;
	
	//Collection of people waiting for elevators to arrive
	//this is checked when an elevator arrives to see where the people waiting want to go
	//the index of the first array represents the floor (index 0 -> first floor)
	//the second array is to allow multiple people to wait on the same floor
	//the integers stored in the second array represent destinations those waiting at that floor
	//those integers do not need to be unique even though elevators only need to respond to unique values in those arrays
	//as this allows us to track the number of people boarding the elevator later when we consider elevator capacity.
	private ArrayList<Integer>[] waiting; 
	private Client client;
	public String portNumber;
//	public Poll pollreceive;
//	public Thread pollReceiveThread;
	
	public Floors(int numberOfFloors, String portNumber) {
		lamps = new boolean [numberOfFloors] [2];
		this.arrivals = new ArrayList<SimulatedArrival>();
		this.portNumber = portNumber;
		this.client = new Client(portNumber , 3007, 3008);
//		this.pollreceive = new Poll(this.client);
////		
//		this.pollReceiveThread = new Thread(pollreceive);
		
	}
	
	public Floors(int numberOfFloors, int numberOfElevators, ArrayList<SimulatedArrival> arrivals, String portNumber) {
		lamps = new boolean [numberOfFloors] [2];
		this.arrivals = arrivals;
		this.waiting= new ArrayList[numberOfFloors];
		this.client = new Client(portNumber, 3007, 3008);
		for(int i=0; i<numberOfFloors; i++) {
			this.waiting[i] = new ArrayList<Integer>();
		}
		for(int i=0; i<numberOfElevators; i++) {
			this.elevatorDirectionIndicator.add(new Boolean(false));
			this.elevatorFloorIndicator.add(0);
		}
		
//		this.pollreceive = new Poll(this.client);
////		
//		this.pollReceiveThread = new Thread(pollreceive);
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	//state management_______________________________________________
	
	//floor is index in lamps array
	//direction true = going up, false = going down
	//state true=on
	public void setLamp(int floor, boolean direction, boolean state) {
		lamps[floor][(direction? 1 : 0)] = state;
		System.out.println("The lamp on floor "+ floor 
				+ " for the direction " + (direction? "up": "down")
				+ " is now " + (state? "on": "off"));
	}
	
	public boolean getLampValue(int floor, boolean direction) {
		return lamps[floor][(direction? 1 : 0)];
	}
	
	public void setElevatorFloorIndicator(int elevatorNumber, int floor) {
		elevatorFloorIndicator.set(elevatorNumber, floor);
		System.out.println("Floors' location indicator for " + elevatorNumber +
				" now reads " + elevatorFloorIndicator.get(elevatorNumber));
	}
	
	
	public int getElevatorFloorIndicator(int elevatorNumber) {
		return elevatorFloorIndicator.get(elevatorNumber);
	}
	
	public void setElevatorDirectionIndicator(int elevatorNumber, boolean direction) {
		elevatorDirectionIndicator.set(elevatorNumber, direction);
		System.out.println("Floors' direction indicator for elevator" + elevatorNumber
		+ "now reads " + (elevatorDirectionIndicator.get(elevatorNumber)? "up": "down"));
	}
	
	public boolean getElevatorDirectionIndicator(int elevatorNumber) {
		return elevatorDirectionIndicator.get(elevatorNumber);
	}
	
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//a request for an elevator to visit this floor
	//(true = up, false = down)
	//floor = floor number the button is on
	public void buttonPress( int floor, boolean direction) {
		System.out.println("Floor " + floor 
				+ " requested an elevator going " + (direction? "up": "down"));
		setLamp(floor, direction, true);
		
//		String data = "FloorButtonPress" + "," + floor + "," + Boolean.toString(direction) + ":" + scheduler.portNumber;
		String data = "FloorButtonPress" + "," + floor + "," + Boolean.toString(direction);
		try {
			this.client.sndqueue.add(data + ":3001"); //should append to client queue
//			this.client.sendData(data, "3001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//elevator arrived
	public void elevatorArrived( int floor, boolean direction) {
		System.out.println("An elevator arrived at " + floor 
				+ " going " + (direction? "up": "down"));
		setLamp(floor, direction, false);
		
		//call elevator button press in scheduler for each of those waiting 
		//on this floor and going the appropriate direction
		for (int i=0; i < waiting[floor-1].size(); i++) {
//				scheduler.elevatorButtonPressed(waiting[floor-1].get(i), direction);
//				String data = "elevatorButtonPressed" + "," + waiting[floor-1].get(i) + "," + Boolean.toString(direction) + ":" + scheduler.portNumber;
				String data = "elevatorButtonPressed" + "," + waiting[floor-1].get(i) + "," + Boolean.toString(direction);
				try {
					this.client.sndqueue.add(data + ":3001"); //should append to client queue
//					this.client.sendData(data, "3001");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				waiting[floor-1].remove(i);
		}
	}
	
	public void elevatorLocationUpdated(int elevatorNumber, int floor) {
		setElevatorFloorIndicator(elevatorNumber, floor);
	}
	
	public void elevatorDirectionUpdated(int elevatorNumber,boolean direction) {
		setElevatorDirectionIndicator(elevatorNumber, direction);
	}
	
	public void processRcvQueue() {
//		System.out.println("rcvqueue size in Floors is: " + this.client.rcvqueue.size());
		while(!this.client.rcvqueue.isEmpty()) {
			String mssg = this.client.rcvqueue.remove();
			String[] param = mssg.split(",");
			System.out.println(param[0]);
			switch(param[0]) {
				case "elevatorDirectionUpdated":
					elevatorDirectionUpdated(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]));
					break;
				case "elevatorLocationUpdated":
					elevatorLocationUpdated(Integer.parseInt(param[1]), Integer.parseInt(param[2]));
					break;
				case "elevatorArrived":
					elevatorArrived(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]));
					break;
				default:
					;
			}
			
		}
	}
	//_________________________________________________________________
	
	
	//run()
	//should wait to be notified by the scheduler
	//should notify arrivals as they appear in the floor input file
	public void run() {
		Thread ClientThread = new Thread(client, "Floors");
		ClientThread.start();
		try {
			Thread.sleep(50);//sleep long enough for all threads to set up and initialize (should be done cleaner)
			//capture the time
			Long startTime = System.currentTimeMillis();
			System.out.println("Simulation starting...");
			
			for(SimulatedArrival arrival : arrivals) {
				
					//sleep until the next arrival is scheduled
		        	Thread.sleep(arrival.getTime() - (System.currentTimeMillis() - startTime));
//		        	System.out.println("Here");
		        	//simulate someone at the specified floor pressing the button in the appropriate direction
		        	buttonPress(arrival.getOriginFloor(), arrival.isDirection());
		        	System.out.println("FBP!!");
		        	
		        	//add person to collection of waiting people
		        	waiting[arrival.getOriginFloor()-1].add(arrival.getDestinationFloor());
		//        	
			}
//			Thread floorClientThread = new Thread(this.client);
//			floorClientThread.setName("FloorClient");
//			floorClientThread.start();
			System.out.println("done");
			while(true) {
				processRcvQueue(); //Process remote procedure calls in rcvqueue
			}
		} catch (InterruptedException e) {
		
		}
	}

//	class Poll implements Runnable{
//		private Client c;
//		
//		public Poll(Client c) {
//			this.c =c;
//		}
//		@Override
//        public void run() {
//			try {
//				c.recieve();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	public static void main(String[] str) {
//		int numberOfFloors = 7;
//		
//
//		// floor numbers in order the elevator is to visit them
//		// make an array of queues for multiple elevators, one for each elevator
//		ArrayList<Integer> schedule = new ArrayList<>();
//		
//		
//		//take in the file of arrivals to be simulated and store them to be 
//		//initialized in floors
//		InputFileReader ifr = new InputFileReader();
//		File file = new File("src/Model/InputFile.txt");
//		ArrayList<SimulatedArrival> list = new ArrayList<SimulatedArrival>();
//		try {
//			list = ifr.readInFile(file);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		Floors floors = new Floors(numberOfFloors, list, "3001", "3002");
//		Thread floorsThread = new Thread(floors, "Floors");
//		floorsThread.start();
//		
//	}

}
