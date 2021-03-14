package Controller;
/**
 
* this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.util.ArrayList;

import View.Elevator;
import View.Floors;

/**
 * @author madelynkrasnay
 *
 */
public class Scheduler extends Thread {
	
	private ArrayList<Elevator> elevators;
	private Floors floors;
	private ArrayList<ArrayList<Integer>>  schedules = new ArrayList<ArrayList<Integer>> ();
	
	public Scheduler(ArrayList<Elevator> elevators, Floors floors, ArrayList<ArrayList<Integer>>   schedules) {
		this.elevators = elevators;
		this.floors = floors;
		this.schedules = schedules;
	}
	
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
		floors.elevatorLocationUpdated(elevatorNumber, floor);
	}
	
	
	//direction updated
	//update floors
	public void elevatorDirectionUpdated(int elevatorNumber, boolean direction) {
		floors.elevatorDirectionUpdated(elevatorNumber, direction);
	}
	
	//elevator stopped
	//let floors know
	public void elevatorStopped(int elevatorNumber, int floor, boolean direction) {
		floors.elevatorArrived(elevatorNumber, floor, direction);
	}
	
	public void elevatorRequestsWork(int elevatorNumber) {
		//do nothing for now, that might change with multi. elevators
	}
	
	//_________________________________________________________________
	
	public void run() {
		//give floors and elevators a reference to you
		for(Elevator elevator : elevators) {
			elevator.setScheduler(this);
		}
		floors.setScheduler(this);
		while(true) {
			
			
		}
	}

}

