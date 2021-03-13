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
	
	private void addToSchedule(int floor, boolean direction) {
		synchronized(this.schedules){
			
			//To be implemented by Askar and Danish
			
			schedules.notifyAll();
		}
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	//will be overhauled with the UDP update
	
	//elevator stop request from floor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction) {
		//update schedule
		this.addToSchedule(originFloor, direction);
	}
	
	public ArrayList<Integer> getQueue(int elevatorNumber) {
		return schedules.get(elevatorNumber);
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int floor, boolean direction) {
		this.addToSchedule(floor, direction);
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

