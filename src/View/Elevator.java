package View;

import java.util.Queue;
import Controller.Scheduler;

/**
 * @author madelynkrasnay
 *
 */
public class Elevator extends Thread {
	
	private Scheduler scheduler;
	
	//elevator state variables
	//lamps (true = on) (index = floor)
	private boolean [] lamps;
	//motor (0=stopped, 1=moving up, 2=moving down)
	private int motor;
	//door (true = open)
	private boolean door;
	//indicators
	private int currentFloor;
	private boolean currentDirection;
	
	private Queue<Integer>  schedule;
	private Integer destination = null;
	
	
	public Elevator(int numberOfFloors, Queue<Integer> schedule) {
		lamps = new boolean[numberOfFloors];
		motor = 0; //Stationary
		door = false; //door closed
		this.schedule = schedule;
	}
	
	public void setSchedule(Queue<Integer>  schedule) {
		this.schedule = schedule;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	
	
	//floor is index in lamps array
	//state (true=on)
	private void setLamp(int floor, boolean state) {
		lamps[floor]=state;
	}
	
	//(0=stoped, 1=moving up, 2=moving down)
	private void setMotor(int state) {
		motor = state;
		//System.out.println("Elevator motor is in state " + state);
		
	}
	
	public int getMotor() {
		return motor;
	}
	
	//(true=open)
	private void setDoor(boolean state) {
		door = state;
		System.out.println("Elevator door state changed");
		
	}
	
	private void setCurrentFloor(int floor) {
		currentFloor = floor;
		System.out.println("Elevator is now at floor "+ floor);
	}
	
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	//true = up
	private void setDirection(boolean direction) {
		currentDirection = direction;
		System.out.println("Elevator is going "+ (direction? "up": "down"));
	}
	
	private void setDesination(int floor) {
		destination = floor;
		System.out.println("Elevator has the new destination of floor " + destination);
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//stop requested
	//updates scheduler of an interternal floor button was pressed
	//call in floors according to input file
	public void buttonPress(int destinationFloor) {
		
	}
	
	//called periodically when in motion
	//floor change as a result of regular motion (
	//location = floor number)
	public void locationUpdate(int location) {
		setCurrentFloor(location);
		//update scheduler
		
	}
	
	//stopped at a floor 
	//opens the doors, lets people load and closes them again
	//(location = floor number)
	public void stopped(int location) {
		setMotor(0);
		System.out.println("Elevator stopped at floor " + location);
		//update scheduler
		scheduler.elevatorStopped(location, currentDirection);
		
		//open doors
		//wait to load
		try {
			sleep(5000);
			System.out.println("Loading elevator car...\n");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//_________________________________________________________________
	
	private void scheduleNewDestination() {
		//set Destination to the top of the queue and pop
		synchronized(schedule){
			if(!schedule.isEmpty()) {
				setDesination(schedule.remove());
			} else {
				System.out.println("empty schedule");
				try {
					schedule.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	//request new destination from scheduler
	//will be updated when there is new work on the queue
	private void requestWork() {
		scheduler.elevatorRequestsWork();
		System.out.println("Elevator is on standby");
	}
	
	
	private void travelToDestination() {
		while(currentFloor != destination) {
			if (currentFloor < destination){
				 //go up a floor
				setDirection(true);
				setMotor(1);//move this into setter once wont cause a merge conflict with tests
				try {
					sleep(500);//however long we decide it take for an elevator to climb a floor
				} catch (InterruptedException e) {
				}
				locationUpdate(currentFloor + 1);
				
			}
			else if(currentFloor > destination){
				//go down a floor
				setDirection(false);
				setMotor(2);
				try {
					sleep(500);//however long we decide it take for an elevator to descend a floor
				} catch (InterruptedException e) {
				}
				locationUpdate(currentFloor - 1);
			}
		}
	}
	
	//run()
	public void run() {
		//give the Scheduler a second to set the scheduler
		try {
			sleep(500);
		} catch (InterruptedException e) {
		}
		while(true) {
				if(destination == null){
					//tell the scheduler
					requestWork();
					//and get a destination
					while(destination == null){
						scheduleNewDestination();
					}
				}
				else if(currentFloor == destination) {
					//if you are at your destination
					//get new destination
						//stop
					this.stopped(currentFloor);
					while(currentFloor == destination) {
						this.scheduleNewDestination();
					}
				}
				//go to destination
				travelToDestination();
		}
	}
}
