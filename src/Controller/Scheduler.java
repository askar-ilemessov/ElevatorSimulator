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
 * @author madelynkrasnay IfiokUdoh YasinJaamac
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
	
	
	private void addFloorButtonPressedToSchedule(int originFloor, boolean direction, int error) {
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
			
			//Get first digit of error code
			int firstDigit = firstDigit(error);

			//Check if floor error
			if(firstDigit==1) {
				//send error to floor
				//floors.handleError(error);
				String data = "handleError" + "," + error;
				try {
					this.client.sendData(data, 3003); //send remote procedure call to Floor receive socket
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(firstDigit==2) {
				//send error to scheduler
				handleError(error);
			}
		}
	}
	
	public void handleError(int error) {
		// TODO Auto-generated method stub
		System.out.print("Scheduler error: " + error);
	}
	private void addElevatorButtonPressedToSchedule(int destinationFloor, boolean direction, int currentFloor, int error) {
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
			
			//Get first digit of error code
			int firstDigit = firstDigit(error);

			//Check if elevator error
			if(firstDigit==3) {
				//send to elevator
				
				System.out.println("WAS HERE ");
				String data = "handleError" + "," + error;
				try {
					this.client.sendData(data, (2010 + sendRequest + 1)); //send remote procedure call to elevator recv socket
					elevators.get(sendRequest).handleError(error);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}	
		
		}
	}
	
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
		System.out.println("FLOOR BUTTON PRESS" + " ERROR: "+error);
		
		
		//update schedule
		this.addFloorButtonPressedToSchedule (originFloor, direction, error);
	}
	
	public ArrayList<Integer> getQueue(int elevatorNumber) {
		return schedules.get(elevatorNumber);
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int destinationFloor, boolean direction, int currentFloor, int error) {
		System.out.println("ELEVATOR BUTTON PRESS" + " ERROR: "+error);
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
				case "handleError":
					handleError(Integer.parseInt(param[1]));
				default:
					break;
			}
			
		}
	}
	//_________________________________________________________________
	
	public void run() {
		Thread rcvProccessThread = new Thread(new RcvProcess(this));//Create and start thread to process received remote procedure calls 
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

