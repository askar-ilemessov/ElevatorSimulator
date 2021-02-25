package Controller;
/**
 
* this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.util.Queue;
import java.util.LinkedList;

import View.Elevator;
import View.Floors;

/**
 * @author madelynkrasnay
 *
 */
public class Scheduler extends Thread {
	
	private Elevator elevator;
	private Floors floors;
	private Queue<Integer> schedule = new LinkedList<>();
	
	public Scheduler(Elevator elevator, Floors floors, Queue<Integer>  schedule) {
		this.elevator = elevator;
		this.floors = floors;
		this.schedule = schedule;
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//elevator stop request from floor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction) {
		//update schedule
		synchronized(this.schedule){
			schedule.add(originFloor);//for now just tagging it to the end of the schedule
			//this should look at the queue(s) and place it in an optimal location
			schedule.notifyAll();
		}
	}
	
	public Queue<Integer> getQueue() {
		return schedule;
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int floor) {
		synchronized(this.schedule){
			///update schedule
			schedule.add(floor);
			//notify elevator
			schedule.notifyAll();
		}
	}
	
	//State updates from elevator:
	//notify floors / elevator where applicable
	
	//location updated
	//update floors
	public void elevatorLocationUpdated(int floor) {
		floors.elevatorLocationUpdated(floor);
	}
	
	
	//direction updated
	//update floors
	public void elevatorDirectionUpdated(boolean direction) {
		floors.elevatorDirectionUpdated(direction);
	}
	
	//elevator stopped
	//let floors know
	public void elevatorStopped(int floor, boolean direction) {
		floors.elevatorArrived(floor, direction);
	}
	
	public void elevatorRequestsWork() {
		//do nothing for now
	}
	
	//resuming motion
	
	//_________________________________________________________________
	
	public void run() {
		//give floors and elevator a reference to you
		elevator.setScheduler(this);
		floors.setScheduler(this);
		while(true) {
			
		}
	}

}

