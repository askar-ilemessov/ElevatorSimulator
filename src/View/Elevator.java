package View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import Controller.Scheduler;
import assignment3Package.Client;

/**
 * @author madelynkrasnay, Danish Butt, ifiok udoh, yasin Jaamac
 *
 */
public class Elevator implements Runnable {
	
	private Scheduler scheduler;
//	private String scheduler;
	//elevator state variables
	//lamps (true = on) (index = floor)
	private boolean [] lamps;
	//motor (0=stopped, 1=moving up, 2=moving down)
	private int motor;
	//door (true = open)
	private boolean door;
	//indicators
	private int currentFloor;
	private boolean currentDirection;
	private int id;
	
	ArrayList<ArrayList<Integer>> schedule = new ArrayList<ArrayList<Integer>>();
	private Integer destination = null;
	
	private Client client;
	public String portNumber;
//	public Poll pollreceive;
//	public Thread pollReceiveThread;
	
	private int elevatorNumber;
	
	enum State {
		WAITING,
		MOVING,
		ARRIVED
	}
	
	
	public Elevator(int numberOfFloors, ArrayList<Integer> schedule, int elevatorNumber) {
		lamps = new boolean[numberOfFloors];
		motor = 0; //Stationary
		door = false; //door closed
		this.schedule = schedule;
		this.elevatorNumber = elevatorNumber;
		this.portNumber = (elevatorNumber + 2005) + "";
		this.client = new Client(portNumber, 3008 + elevatorNumber, 3009 + elevatorNumber);
//		this.pollreceive = new Poll(this.client);
////		
//		this.pollReceiveThread = new Thread(pollreceive);
	}

	public int getId() {
		return id;
	}
	
	public void setSchedule(ArrayList<Integer>  schedule) {
		this.schedule = schedule;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	
	
	//floor is index in lamps array
	//state (true=on)
	private void setLamp(int floor, boolean state) {
		lamps[floor]=state;
	}
	
	
	//(0=stoped, 1=moving up, 2=moving down)
	public void setMotor(int state) {
		motor = state;
		if(state == 1) {
			setDirection(true);
		}
		else if (state == 2) {
			setDirection(false);
		}
		
	}
	
	public int getMotor() {
		return motor;
	}
	
	//(true=open)
	private void setDoor(boolean state) {
		door = state;
		System.out.println("Elevator " + elevatorNumber + " door is now "+ (state? "open": "closed"));
		
	}
	
	public void setCurrentFloor(int floor) {
		currentFloor = floor;
		System.out.println("Elevator " + elevatorNumber + " is now at floor "+ floor);
	}
	
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	//set by motor
	//true = up
	public void setDirection(boolean direction) {
		currentDirection = direction;
		System.out.println("Elevator " + elevatorNumber + " is going "+ (direction? "up": "down"));
	}
	
	public boolean getDirection() {
		return currentDirection;
	}
	
	public void setDesination(int floor) {
		destination = floor;
		System.out.println("Elevator " + elevatorNumber + " has the new destination of floor " + destination);
	}
	
	public Integer getDesination() {
		return destination;
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//stop requested
	//updates scheduler of an interternal floor button was pressed
	//call in floors according to input file

	public void buttonPress(int destinationFloor) {
		setLamp(destinationFloor, true);
//		scheduler.elevatorButtonPressed(destinationFloor, currentDirection);
//		String data = "elevatorButtonPressed" + "," + destinationFloor + "," + Boolean.toString(currentDirection) + ":" + scheduler.portNumber;
//		String data = "elevatorButtonPressed" + "," + destinationFloor + "," + Boolean.toString(currentDirection) + ":" + "3001";
		String data = "elevatorButtonPressed" + "," + destinationFloor + "," + Boolean.toString(currentDirection);
		try {
			this.client.sndqueue.add(data + ":3001"); //should append to client queue
//			this.client.sendData(data, "3001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//called periodically when in motion
	//floor change as a result of regular motion (
	//location = floor number)
	public void locationUpdate(int location) {
		setCurrentFloor(location);
		//update scheduler
//		scheduler.elevatorLocationUpdated(elevatorNumber,location);
//		String data = "elevatorLocationUpdated" + "," + elevatorNumber + "," + location +  ":" + scheduler.portNumber;
//		String data = "elevatorLocationUpdated" + "," + elevatorNumber + "," + location +  ":" + "3001";
		String data = "elevatorLocationUpdated" + "," + elevatorNumber + "," + location;
		try {
			this.client.sndqueue.add(data + ":3001"); //should append to client queue
//			this.client.sendData(data, "3001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//stopped at a floor 
	//opens the doors, lets people load and closes them again
	//(location = floor number)
	public void stopped(int location) {
		setMotor(0);
		System.out.println("Elevator" + elevatorNumber + " stopped at floor " + location);
		//update scheduler
//		scheduler.elevatorStopped(location, currentDirection);
//		String data = "elevatorStopped" + "," + elevatorNumber + "," + location + "," + Boolean.toString(currentDirection) + ":" + scheduler.portNumber;
//		String data = "elevatorStopped" + "," + elevatorNumber + "," + location + "," + Boolean.toString(currentDirection) + ":" + "3001";
		String data = "elevatorStopped" + "," + elevatorNumber + "," + location + "," + Boolean.toString(currentDirection);
		try {
			this.client.sndqueue.add(data + ":3001"); //should append to client queue
//			this.client.sendData(data, "3001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//turn off lamp
		setLamp(location, false);
		//open doors
		setDoor(true);
		//wait to load
		try {
			System.out.println("Loading elevator" + elevatorNumber + "'s car...\n");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//close doors
		setDoor(false);
	}
	
	//_________________________________________________________________
	
	private void scheduleNewDestination() {
		//set Destination to the top of the queue and pop
		synchronized(schedule){
			if(!schedule.isEmpty()) {
				setDesination(schedule.remove(0));
			} else {
				requestWork();
				try {
					schedule.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("here!!!");
			}
		}
	}
	
	//request new destination from scheduler
	//will be updated when there is new work on the queue
	private void requestWork() {
//		scheduler.elevatorRequestsWork();
//		String data = "elevatorRequestsWork" + "," + elevatorNumber +  ":" + scheduler.portNumber;
//		String data = "elevatorRequestsWork" + "," + elevatorNumber +  ":" + "3001";
		String data = "elevatorRequestsWork" + "," + elevatorNumber;
		try {
			this.client.sndqueue.add(data + ":3001"); //should append to client queue
			System.out.println("Elevator " + elevatorNumber + " is on standby");
//			this.client.sendData(data, "3001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private void travelToDestination() {
		while(currentFloor != destination) {
			if (currentFloor < destination){
				 //go up a floor
				setMotor(1);
				try {
					Thread.sleep(500);//however long we decide it take for an elevator to climb a floor
				} catch (InterruptedException e) {
				}
				locationUpdate(currentFloor + 1);
				
			}
			else if(currentFloor > destination){
				//go down a floor
				setMotor(2);
				try {
					Thread.sleep(500);//however long we decide it take for an elevator to descend a floor
				} catch (InterruptedException e) {
				}
				locationUpdate(currentFloor - 1);
			}
		}
	}
	
	public void processRcvQueue() {
		if(!this.client.rcvqueue.isEmpty()) {
			String mssg = this.client.rcvqueue.remove();
			String[] param = mssg.split(",");
			switch(param[0]) {
				case "requestWork":
					requestWork();
					break;
				default:
					break;
			}
			
		}
	}
	
	//run()
	public void run() {
	
		//give the Scheduler a second to set the scheduler
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		
		Thread ClientThread = new Thread(client, "Floors");
		ClientThread.start();
		State state = State.WAITING; //default state is waiting
//		this.client.sndqueue.add("request:none");
//		Thread ElevatorClientThread = new Thread(this.client);
//		ElevatorClientThread.setName("ElevatorClient");
//		ElevatorClientThread.start();
		while(true) {
			processRcvQueue();
			switch(state){
			
				//State 1: Waiting for a Request
				case WAITING: 
					System.out.println("State: Waiting");
					while(destination==null) {
						scheduleNewDestination();  //Get request 
					}
					
					//request received
//					try {
//						this.client.sendData("request", "3001");
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					state = State.MOVING;
					

				//State 2: Elevator is going to destination	
				case MOVING: 
					
					//not at destination
					if (currentFloor != destination) {
						
						travelToDestination(); //go to destination
					}
					else {
						state = State.ARRIVED;  
					}
					
					
				
				//State 3: Elevator has arrived at destination	
				case ARRIVED: 
					
					this.stopped(currentFloor);  //destination reached
			
					this.destination=null;
					
					state = State.WAITING; //Go back and wait for another request
					
				default:
					break;
				}
			}
		}



	public int getNumber() {
		return elevatorNumber;
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
//		Elevator elevator1 = new Elevator(numberOfFloors, schedule,"3000", "3002");
//		Thread elevator1Thread = new Thread(elevator1, "Elevator 1");
//		elevator1Thread.start();
//		
//	}
	}