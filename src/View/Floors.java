package View;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Controller.Scheduler;
import assignment3Package.Client;



/**
 * The floor subsystem is used to simulate the arrival of passengers to the elevators and for simulating all button presses and lamps.
 * 
 */

/**
 * @author madelynkrasnay IfiokUdoh YasinJaamac
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
	private ArrayList<SimulatedArrival> arrivals;
	
	//Collection of people waiting for elevators to arrive
	//this is checked when an elevator arrives to see where the people waiting want to go
	//the index of the first array represents the floor (index 0 -> first floor)
	//the second array is to allow multiple people to wait on the same floor
	//the integers stored in the second array represent destinations those waiting at that floor
	//those integers do not need to be unique even though elevators only need to respond to unique values in those arrays
	//as this allows us to track the number of people boarding the elevator later when we consider elevator capacity.
	private ArrayList<Integer>[] waiting; 
	public BlockingQueue<String> rcvqueue = new ArrayBlockingQueue<String>(10);
	public String portNumber;
	public Client client;

	
	public Floors(int numberOfFloors, int portNumber) {
		lamps = new boolean [numberOfFloors] [2];
		this.arrivals = new ArrayList<SimulatedArrival>();
		client = new Client(portNumber);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public Floors(int numberOfFloors, int numberOfElevators, ArrayList<SimulatedArrival> arrivals, int portNumber) {
		lamps = new boolean [numberOfFloors] [2];
		this.arrivals = arrivals;
		this.waiting= new ArrayList[numberOfFloors];

		for(int i=0; i<numberOfFloors; i++) {
			this.waiting[i] = new ArrayList<Integer>();
		}
		for(int i=0; i<numberOfElevators; i++) {
			this.elevatorDirectionIndicator.add(new Boolean(false));
			this.elevatorFloorIndicator.add(0);
		}
		client = new Client(portNumber); //nitialize client for sending and receiving remote procedure calls

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
		
		String data = "FloorButtonPress" + "," + floor + "," + Boolean.toString(direction);
		try {
			this.client.sendData(data,3001); //send remote procedure call to scheduler receive socket
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//elevator arrived
	public void elevatorArrived(int elevatorNumber, int floor, boolean direction) {
		System.out.println("An elevator arrived at " + floor 
				+ " going " + (direction? "up": "down"));
		setLamp(floor, direction, false);
		
		//call elevator button press in scheduler for each of those waiting 
		//on this floor and going the appropriate direction
		for (int i=0; i < waiting[floor-1].size(); i++) {
				String data = "elevatorButtonPressed" + "," + waiting[floor-1].get(i) + "," + Boolean.toString(direction) + "," + floor;
				try {
					this.client.sendData(data, 3001);//send remote procedure call to scheduler receive socket
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
	
	public class RcvProcess implements Runnable{
		private Floors f;
		public RcvProcess(Floors f) {
			this.f = f;
		}
		public void run() {
			while(true) {
				f.client.recv(f.rcvqueue);
				f.processRcvQueue();
			}
		}
	}
	
	public void processRcvQueue() {
		while(!this.rcvqueue.isEmpty()) {
			String mssg = this.rcvqueue.remove();
			String[] param = mssg.trim().split(",");
			switch(param[0]) {
				case "elevatorDirectionUpdated":
					elevatorDirectionUpdated(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]));
					break;
				case "elevatorLocationUpdated":
					elevatorLocationUpdated(Integer.parseInt(param[1]), Integer.parseInt(param[2]));
					break;
				case "elevatorArrived":
					elevatorArrived(Integer.parseInt(param[1]), Integer.parseInt(param[2]), Boolean.parseBoolean(param[3]));
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
		Thread rcvProccessThread = new Thread(new RcvProcess(this));//Create and start thread to process received remote procedure calls
		rcvProccessThread.start();
		try {
			Thread.sleep(50);//sleep long enough for all threads to set up and initialize (should be done cleaner)
			//capture the time
			Long startTime = System.currentTimeMillis();
			System.out.println("Simulation starting...");
			
			for(SimulatedArrival arrival : arrivals) {
				
					//sleep until the next arrival is scheduled
		        	Thread.sleep(arrival.getTime() - (System.currentTimeMillis() - startTime));

		        	//simulate someone at the specified floor pressing the button in the appropriate direction
		        	buttonPress(arrival.getOriginFloor(), arrival.isDirection());
		        	
		        	//add person to collection of waiting people
		        	waiting[arrival.getOriginFloor()-1].add(arrival.getDestinationFloor());
     	
			}	
			System.out.println("done");
		} catch (InterruptedException e) {
		
		}
	}



}
