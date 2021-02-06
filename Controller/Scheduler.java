/**
 * this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.util.Queue;
import java.util.LinkedList;

/**
 * @author madelynkrasnay
 *
 */
public class Scheduler extends Thread {
	
	private Elevator elevator;
	private Floors floors;
	private Schedule schedule;
	
	public Scheduler(Elevator elevator, Floors floors, Schedule schedule) {
		this.elevator = elevator;
		this.floors = floors;
		this.schedule = schedule;
	}
	
	//notify elevator of schedule change
	
	//Event Handeling_________________________________________________
	//proforms nessacary tasks in responce to events
	
	//elevator stop request from foor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction) {
		//update schedule
		synchronized(this.schedule){
			schedule.add(originFloor);//for now just taging it to the end of the schedule
			//notify elevator
			//notify floors the elevator is on its way
			schedule.NotifyAll();
		}
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int floor) {
		///update schedule
		schedule.add(floor);
		//notify elevator
		//schedule.NotifyAll();
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
	public void elevatorStopped(int floor) {
		floors.elevatorArrived(floor, direction);
	}
	
	public void elevatorRequestsWork() {
		//do nothing for now
	}
	
	//resuming motion
	
	//_________________________________________________________________
	
	public void run() {
		//give floors and elevator a refrence to you
		elevator.setScheduler(this);
		elevator.setSchedule(this.schedule);
		floors.setScheduler(this);
		while(true) {
			
		}
	}

}
