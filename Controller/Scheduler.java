/**
 * this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

/**
 * @author madelynkrasnay
 *
 */
public class Scheduler extends Thread {
	
	private Elevator elevator;
	private Floors floors;
	private Schedule schedule;
	
	public Scheduler(Elevator elevator, Floors floors) {
		this.elevator = elevator;
		this.floors = floors;
		this.schedule = new Schedule();
	}
	
	//notify elevator of schedule change
	
	//Event Handeling_________________________________________________
	//proforms nessacary tasks in responce to events
	
	//elevator stop request from foor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction) {
		//update schedule
		//notify appropreate elevator
		//notify floors the elevator is on its way
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int Floor) {
		///update schedule
		//notify appropreate elevator
	
	}
	
	//State updates from elevator:
	//notify floors / elevator where applicable
	
	//location updated
	//update floors
	
	//direction updated
	//update floors
	
	//elevator stopped
	//let floors know
	public void stopped(int floor) {
		floors.elevatorArrived(floor, direction);
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
