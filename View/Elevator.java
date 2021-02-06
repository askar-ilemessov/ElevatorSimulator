/**
 * The  elevator  subsystem  consists  of  the  buttons  and  lamps 
 * inside  of  the  elevator  used  to  select  floors  and indicate 
 * the floors selected, and to indicate the location of the elevator 
 * itself.   
 * 
 * The elevator subsystem is also used to operate the motor and to 
 * open and close the doors.  Each elevator has its own elevator 
 * subsystem. 
 * 
 * For  the  purpose  of  this  project,  the  elevator  subsystem 
 * listens  for  packets  from  the  scheduler  to  control  the 
 * motor and to open the doors.   The elevator subsystem also has 
 * to monitor the floor subsystem for destination requests (button 
 * presses inside of the elevator car, rather than button presses at 
 * the floors) from the input file.  Button presses are to be rerouted 
 * to the scheduler system.       Lamp (floor indications) from button 
 * pushes do not have to originate from the scheduler.   Rather, when 
 * the elevator subsystem detects a button request, it can then  light  
 * the  corresponding  lamp.   When  the  elevator  reaches  a  floor,  
 * the  scheduling  subsystem  signals  the elevator subsystem to turn 
 * the lamp off.
 */

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
		System.out.println("Elevator now at floor %d", location);
		//update scheduler
		
	}
	
	//stoped at a floor 
	//opens the doors, lets people load and closes them again
	//(location = floor number)
	private void stopped(int location) {
		setMotor(0);
		System.out.println("Elevator stopped at floor %d", location);
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
		//set Destination to the top of the queue
	}
	
	//request new destination from sechduler
	//will be updated when there is new work on the queue
	//kinda redundant as the  but nessacary as per assignment doc
	private void requestWork() {
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
			System.out.println("Elevator is now at floor %d", destination);
		}
		
	}
	
	//run()
	public void run() {
		while(true) {
			if(destination == null) {
				scheduleNewDestination();
				//if its still null
				if(destination == null){
					//tell the seheduler and wait
					requestWork();
					//wait() on schedule object
				}
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
