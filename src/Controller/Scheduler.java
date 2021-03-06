package Controller;
/**
 
* this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import View.Elevator;
import View.Floors;
import assignment3Package.Client;

/**
 * @author madelynkrasnay IfiokUdoh YasinJaamac DanishButt
 *
 */
public class Scheduler extends Thread {
	
	private ArrayList<Elevator> elevators;
	private Floors floors;
	private ArrayList<ArrayList<Integer>>  schedules = new ArrayList<ArrayList<Integer>> ();
	private Client client; //Client for remote procedure call over UDP
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
	
	
	/*
	 * This method is used to add a floor button request to the scheduler
	 */
	
	private void addFloorButtonPressedToSchedule(int originFloor, boolean direction, int error) {
		int sendRequestToElevator=30;
		int difference = 20;
		
		
		//Find the closest elevator to the floor
		for(int i=0; i<3; i++) {
			if((Math.abs(elevators.get(i).getCurrentFloor() - originFloor) < difference) && (elevators.get(i).getDesination()==null)) {
				difference = Math.abs(elevators.get(i).getCurrentFloor() - originFloor) ;
				
				sendRequestToElevator = i;
			}
		}
		
		synchronized(schedules.get(sendRequestToElevator)) {
			schedules.get(sendRequestToElevator).add(originFloor);
			schedules.get(sendRequestToElevator).notifyAll();
			
			//Get first digit of error code
			int firstDigit = firstDigit(error);
			

				if(firstDigit==2) {
				String data = "handleError" + "," + error;
				try {
					
					this.client.sendData(data, (2010 + sendRequestToElevator + 1)); //send remote procedure call to elevator recv socket
					//elevators.get(sendRequest).handleError(error);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
	}
	
	

	
	/*
	 * This method is used to add an elevator button request to the specific elevator it came from	
	 */
	private void addElevatorButtonPressedToSchedule(int destinationFloor, boolean direction, int currentFloor, int error) {
		int sendRequest = 0;
		
		//Finds which elevator the request came from
		for(int i=0; i<3;i++) {
			if(elevators.get(i).getCurrentFloor()== currentFloor) {
				sendRequest = i;
			}	
		}
	
		//Adds request to the elevator schedule
		synchronized(this.schedules.get(sendRequest)) {
			schedules.get(sendRequest).add(destinationFloor);
			schedules.get(sendRequest).notifyAll();
			
			//Get first digit of error code
			int firstDigit = firstDigit(error);

			//Check if elevator error
			if(firstDigit==3) {
				//send to elevator
				String data = "handleError" + "," + error;
				try {
					this.client.sendData(data, (2010 + sendRequest + 1)); //send remote procedure call to elevator recv socket
					//elevators.get(sendRequest).handleError(error);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	}
	
	//This method is used to get the first digit of a number (used in error handling)
	  public int firstDigit(int n) 
	    { 
	        while (n >= 10)  
	            n = n/ 10; 
	        return n; 
	    }

	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	//will be overhauled with the UDP update
	
	//elevator stop request from floor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction, int error) {
		//System.out.println("FLOOR BUTTON PRESS" + " ERROR: "+error);
		
		
		//update schedule
		this.addFloorButtonPressedToSchedule (originFloor, direction, error);
	}
	
	public ArrayList<Integer> getQueue(int elevatorNumber) {
		return schedules.get(elevatorNumber);
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int destinationFloor, boolean direction, int currentFloor, int error) {
		//System.out.println("ELEVATOR BUTTON PRESS" + " ERROR: "+error);
		this.addElevatorButtonPressedToSchedule(destinationFloor, direction, currentFloor, error);
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
		String data = "elevatorDirectionUpdated" + "," + elevatorNumber + "," + Boolean.toString(direction);
		try {
			this.client.sendData(data, 3003); //send remote procedure call to Floor receive socket
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//elevator stopped
	//let floors know
	public void elevatorStopped(int elevatorNumber, int floor, boolean direction) {
		String data = "elevatorArrived" + "," + elevatorNumber + "," + floor + "," + Boolean.toString(direction);
		try {
			this.client.sendData(data, 3003); //send remote procedure call to Floor receive socket
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
			while(client.isOpen()) {
				sch.client.recv(sch.rcvqueue);
				sch.processRcvQueue();
			}
		}
	}
	
	//Process remote procedure calls received into rcv queue
	public void processRcvQueue() {
		while(!this.rcvqueue.isEmpty()) {
			
			String mssg = this.rcvqueue.remove();
			String[] param = mssg.trim().split(",");
			
			switch(param[0]) {
				case "FloorButtonPress":
					//System.out.println(Integer.parseInt(param[3]));
					FloorButtonPress(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]), Integer.parseInt(param[3]));
					break;
				case "elevatorButtonPressed":
					//System.out.println(Integer.parseInt(param[4]));
					elevatorButtonPressed(Integer.parseInt(param[1]), Boolean.parseBoolean(param[2]), Integer.parseInt(param[3]),Integer.parseInt(param[4]));
					break;
				case "elevatorStopped":
					elevatorStopped(Integer.parseInt(param[1]),Integer.parseInt(param[2]), Boolean.parseBoolean(param[3]));
					break;
				case "elevatorRequestsWork":
					elevatorRequestsWork(Integer.parseInt(param[1]));
					break;
				case "elevatorLocationUpdated":
					elevatorLocationUpdated(Integer.parseInt(param[1]), Integer.parseInt(param[2]));
					break;
//				case "handleError":
//					handleError(Integer.parseInt(param[1]));
				default:
					break;
			}
			
		}
	}
	//_________________________________________________________________
	
	public void run() {
		Thread rcvProccessThread = new Thread(new RcvProcess(this));//Create and start thread to process received remote procedure calls 
		rcvProccessThread.start();

		for(Elevator elevator : elevators) {
			elevator.setScheduler(this);
		}
		floors.setScheduler(this);		
		
	}
	
	public void releaseResourses() {
		for(Elevator elevator : elevators){
			elevator.closeClient();
		}
		floors.closeClient();
		this.client.close();
		
	}
	

}

