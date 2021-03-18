package Controller;
/**
 
* this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import View.Elevator;
import View.Floors;
import View.InputFileReader;
import View.SimulatedArrival;

import assignment3Package.Client;

/**
 * @author madelynkrasnay
 *
 */
public class Scheduler extends Thread {
	
	private ArrayList<Elevator> elevators;
	private Floors floors;
	private ArrayList<ArrayList<Integer>>  schedules = new ArrayList<ArrayList<Integer>> ();
	private Client client;
	public String portNumber;
//	public Poll pollreceive;
//	public Thread pollReceiveThread;
	
	public Scheduler(ArrayList<Elevator> elevators, Floors floors, ArrayList<ArrayList<Integer>>   schedules, String portNUmber) {
		this.elevators = elevators;
		this.floors = floors;
		this.schedules = schedules;
		this.portNumber = portNumber;
		this.client = new Client(portNUmber, 3005, 3006);
//		this.pollreceive = new Poll(this.client);
////		
//		this.pollReceiveThread = new Thread(pollreceive);
	}
	
//	public Scheduler(ArrayList<Integer>  schedule, String portNumber, String elevator, String floors) {
//		this.elevator = elevator;
//		this.floors = floors;
//		this.schedule = schedule;
//		this.portNumber = portNumber;
//		this.client = new Client(portNumber);
//		Thread SchedulerClientThread = new Thread(this.client);
//		SchedulerClientThread.setName("SchedulerClient");
//		SchedulerClientThread.start();
//	}
	
	private void addFloorButtonPressedToSchedule(int originFloor, boolean direction) {
		int sendRequestToElevator=30;
		int difference = 20;
		
		for(int i=0; i<3; i++) {
			System.out.println("Elevator: "+ i);
			System.out.println("Current Floor: " + elevators.get(i).getCurrentFloor());
			System.out.println("difference: " + Math.abs(elevators.get(i).getCurrentFloor() - originFloor));
			
			if((Math.abs(elevators.get(i).getCurrentFloor() - originFloor) < difference) && (elevators.get(i).getDesination()==null)) {
				difference = Math.abs(elevators.get(i).getCurrentFloor() - originFloor) ;
				
				sendRequestToElevator = i;
			}
		
		}
		
		
		
		synchronized(schedules.get(sendRequestToElevator)) {
			schedules.get(sendRequestToElevator).add(originFloor);
			schedules.get(sendRequestToElevator).notifyAll();
			
//			System.out.println("Elevator " + (sendRequestToElevator) + " has received this request to go to " + originFloor);
		}
	}
	
	private void addElevatorButtonPressedToSchedule(int destinationFloor, boolean direction, int currentFloor) {
		int sendRequest = 0;
		
		for(int i=0; i<3;i++) {
			if(elevators.get(i).getCurrentFloor()== currentFloor) {
				System.out.println("Elevator sending to :" + elevators.get(i).getNumber());
				sendRequest = i;
			}	
		}
	
		synchronized(this.schedules.get(sendRequest)) {
			schedules.get(sendRequest).add(destinationFloor);
//			System.out.println("Elevator " + (sendRequest) + " is going to " + destinationFloor);
			schedules.get(sendRequest).notifyAll();
		
		}
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	//will be overhauled with the UDP update
	
	//elevator stop request from floor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction) {
		//update schedule
		this.addFloorButtonPressedToSchedule (originFloor, direction);
	}
	
	public ArrayList<Integer> getQueue(int elevatorNumber) {
		return schedules.get(elevatorNumber);
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int destinationFloor, boolean direction, int currentFloor) {
		this.addElevatorButtonPressedToSchedule(destinationFloor, direction, currentFloor);
	}
	
	//State updates from elevator:
	//notify floors / elevator where applicable
	
	//location updated
	//update floors
	public void elevatorLocationUpdated(int elevatorNumber, int floor) {
//		floors.elevatorLocationUpdated(floor);
//		String data = "elevatorLocationUpdated" + "," + floor + ":" + floors.portNumber;
		String data = "elevatorLocationUpdated" + "," + elevatorNumber + "," + floor ;
		try {
			this.client.sndqueue.add(data + ":" +  floors.portNumber); //should append to client queue
//			this.client.sendData(data, floors.portNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//direction updated
	//update floors
	public void elevatorDirectionUpdated(boolean direction) {
//		floors.elevatorDirectionUpdated(direction);
//		String data = "elevatorDirectionUpdated" + "," + Boolean.toString(direction) + ":" + floors.portNumber;
		String data = "elevatorDirectionUpdated" + "," + Boolean.toString(direction);
		try {
			this.client.sndqueue.add(data + ":" +  floors.portNumber); //should append to client queue
//			this.client.sendData(data, floors.portNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//elevator stopped
	//let floors know
	public void elevatorStopped(int floor, boolean direction) {
//		floors.elevatorArrived(floor, direction);
//		String data = "elevatorArrived" + "," + floor + "," + Boolean.toString(direction) + ":" + floors.portNumber;
		String data = "elevatorArrived" + "," + floor + "," + Boolean.toString(direction);
		try {
			this.client.sndqueue.add(data + ":" +  floors.portNumber); //should append to client queue
//			this.client.sendData(data, floors.portNumber);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void elevatorRequestsWork() {
		//do nothing for now, that might change with multi. elevators
//		for(Elevator elevator : elevators) {
//			try {
//				this.client.sendData("requestWork,", elevator.portNumber);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	public void processRcvQueue() {
//		System.out.println("rcvqueue size in scheduler is: " + this.client.rcvqueue.size());
		while(!this.client.rcvqueue.isEmpty()) {
			System.out.println("Recvqueue is not empty");
			String mssg = this.client.rcvqueue.remove();
			String[] param = mssg.split(",");
			switch(param[0]) {
				case "FloorButtonPress":
					System.out.println("Rcv floor button press");
					FloorButtonPress(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]));
					break;
				case "elevatorButtonPressed":
					elevatorButtonPressed(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]), Integer.parseInt(param[3]));
					break;
				case "elevatorStopped":
					elevatorStopped(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]));
					break;
				case "elevatorRequestsWork":
					System.out.println("elevatorRequestsWork");
					elevatorRequestsWork();
					break;
				case "elevatorLocationUpdated":
					elevatorLocationUpdated(Integer.parseInt(param[1]), Integer.parseInt(param[2]));
					break;
				default:
					break;
			}
			
		}
	}
	//_________________________________________________________________
	
	public void run() {
		//give floors and elevators a reference to you
		Thread ClientThread = new Thread(client, "Floors");
		ClientThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Elevator elevator : elevators) {
			elevator.setScheduler(this);
		}
		floors.setScheduler(this);
//				Thread schedulerClientThread = new Thread(this.client);
//				schedulerClientThread.setName("schedulerClient");
//				schedulerClientThread.start();
		
		while(true) {			
//			System.out.println("About to Process recvqueue");
			processRcvQueue();
			
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
//		Scheduler scheduler = new Scheduler(schedule, "3002", "3000", "3001");
//		Thread schedulerThread = new Thread(scheduler, "Scheduler");
//		schedulerThread.start();
//		
//	}

}

