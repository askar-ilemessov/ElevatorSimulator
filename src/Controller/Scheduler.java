package Controller;
/**
 
* this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
	public BlockingQueue<String> rcvqueue = new ArrayBlockingQueue<String>(10);;
	
	public Scheduler(int portNUmber) {
		this.client = new Client(portNUmber);
	}
	public Scheduler(ArrayList<Elevator> elevators, Floors floors, ArrayList<ArrayList<Integer>>   schedules, int portNUmber) {
		this.elevators = elevators;
		this.floors = floors;
		this.schedules = schedules;
		this.client = new Client(portNUmber);
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
		String data = "elevatorLocationUpdated" + "," + elevatorNumber + "," + floor ;
		try {
//			this.client.sndqueue.add(data + ":" +  floors.portNumber); //should append to client queue
			this.client.sendData(data, 3003);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//direction updated
	//update floors
	public void elevatorDirectionUpdated(int elevatorNumber, boolean direction) {
//		floors.elevatorDirectionUpdated(direction);
		String data = "elevatorDirectionUpdated" + "," + elevatorNumber + "," + Boolean.toString(direction);
		try {
//			this.client.sndqueue.add(data + ":" +  floors.portNumber); //should append to client queue
			this.client.sendData(data, 3003);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//elevator stopped
	//let floors know
	public void elevatorStopped(int elevatorNumber, int floor, boolean direction) {
//		floors.elevatorArrived(floor, direction);
		String data = "elevatorArrived" + "," + elevatorNumber + "," + floor + "," + Boolean.toString(direction);
		try {
//			this.client.sndqueue.add(data + ":" +  floors.portNumber); //should append to client queue
			this.client.sendData(data, 3003);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void elevatorRequestsWork(int elevatorNumber) {
		//do nothing for now, that might change with multi. elevators
//		
	}
	
	public class RcvProcess implements Runnable{
		private Scheduler sch;
		public RcvProcess(Scheduler sch) {
			this.sch = sch;
		}
		public void run() {
			while(true) {
				sch.client.recv(sch.rcvqueue);
				sch.processRcvQueue();
			}
		}
	}
	
	public void processRcvQueue() {
		while(!this.rcvqueue.isEmpty()) {
			
			String mssg = this.rcvqueue.remove();
			String[] param = mssg.trim().split(",");
			switch(param[0]) {
				case "FloorButtonPress":
//					System.out.println("Rcv floor button press");
					FloorButtonPress(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]));
					break;
				case "elevatorButtonPressed":
					elevatorButtonPressed(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]), Integer.parseInt(param[3]));
					break;
				case "elevatorStopped":
					elevatorStopped(Integer.parseInt(param[1]),Integer.parseInt(param[2]), Boolean.parseBoolean(param[3]));
					break;
				case "elevatorRequestsWork":
//					System.out.println("elevatorRequestsWork" + param[1].trim() + " testiing");
					elevatorRequestsWork(Integer.parseInt(param[1]));
					break;
				case "elevatorLocationUpdated":
					elevatorLocationUpdated(Integer.parseInt(param[1]), Integer.parseInt(param[2]));
					break;
				default:
					System.out.println("Not1 : " + param[0] + " test");
					break;
			}
			
		}
	}
	//_________________________________________________________________
	
	public void run() {
		Thread rcvProccessThread = new Thread(new RcvProcess(this));
		rcvProccessThread.start();
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
		
	}
	

}

