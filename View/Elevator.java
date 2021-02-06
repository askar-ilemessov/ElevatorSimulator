import Threads.Table;

/**
 * @author madelynkrasnay
 *
 */
public class Elevator extends Thread {
	
	private Scheduler scheduler;
	
	//elevator state varibles
	//lamps (true = on) (index = floor)
	private boolean [] lamps;
	//motor (0=stoped, 1=moving up, 2=moving down)
	private int motor;
	//door (true = open)
	private boolean door;
	//indicators
	private int currentFloor;
	private boolean currentDirection;
	
	private Schedule schedule;
	private Integer destination = null;
	
	
	public Elevator(int numberOffloors) {
		lamps = new boolean[numberOfLamps];
		motor = 0; //sttionary
		door = false; //door closed
	}
	
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	public void setScheduler(Scheduer scheduler) {
		this.scheduler = scheduler;
	}
	
	
	//floor is index in lamps array
	//state (true=on)
	private void setLamp(int floor, bool state) {
		lamps[floor]=state;
	}
	
	//(0=stoped, 1=moving up, 2=moving down)
	private void setMotor(int state) {
		motor = state;
		
	}
	
	//(true=open)
	private void setDoor(boolean state) {
		door = state;
		System.out.println("Elevator door state changed");
		
	}
	
	private void setFloor(int floor) {
		currentFloor = floor;
		
	}
	
	//true = up
	private void setDirection(boolean direction) {
		currentDirection = direction;
	}
	
	private void setDesination(int floor) {
		destination = floor;
	}
	
	//Event Handeling_________________________________________________
	//proforms nessacary tasks in responce to events
	
	//stop requested
	//updates sceduler of an interternal floor button was pressed
	private void buttonPress(int destinationFloor) {
		
	}
	
	//called perodically when in motion
	//floor change as a resault of regular motion (
	//location = floor number)
	private void locationUpdate(int location) {
		setCurrentLocation(location):
		System.out.println("Elevator now at floor "+ location);
		//update scheduler
		
	}
	
	//stoped at a floor 
	//opens the doors, lets people load and closes them again
	//(location = floor number)
	private void stopped(int location) {
		setMotor(0);
		System.out.println("Elevator stopped at floor " + location);
		//update scheduler
		scheduler.stopped(location, CurrentDirection);
		
		//open doors
		//wait to load
		//close doors
	}
	
	//resuming motion
	//calls direction update if nessacary
	//direction true=up
	private void resumingMotion(boolean direction) {
		System.out.println("Elevator resuming motion");
		
	}
	
	
	//_________________________________________________________________
	
	private void scheduleNewDestination() {
		//set Destination to the top of the queue and pop
		this.destination = schedule.remove;
	}
	
	//request new destination from sechduler
	//will be updated when there is new work on the queue
	//kinda redundant as the  but nessacary as per assignment doc
	private void requestWork() {
		scheduler.elevatorRequestsWork();
		System.out.println("Elevator is on standby");
	}
	
	
	private void travelToDestination() {
		while(currentfloor != destination) {
			if (currentFloor < destination){
				 //go up a floor
				sleep(500);//however long we decide it take for an elevator to climb a floor
				setFloor(currentFloor++);
				
			}
			else if(currentFloor > destination){
				//go down a floor
				sleep(500);//however long we decide it take for an elevator to decend a floor
				setFloor(currentFloor--);
			}
			System.out.println("Elevator is now at floor "+ destination);
		}
	}
	
	//run()
	public void run() {
		while(true) {
				if(destination == null){
					//tell the seheduler
					requestWork();
					//wait() on schedule object
					synchronized(this.schedule){
						while(this.schedule.peek() == null) {
							try{
								this.schedule.wait();
							}
							catch(Exception e){	
							}
						}
						scheduleNewDestination();
						this.schedule.notifyAll();
				}
			else {
				//go to destination
				//get new destination
				if(currentFloor = destination) {
					//stop
					this.destination = null;
					this.stopped(currentFloor);
					this.scheduleNewDestination();
				}
				else {
					 //go up
					travelToDestination();
				}
			}
		}
	}
}
