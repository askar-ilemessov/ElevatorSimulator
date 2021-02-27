package View;

import java.util.ArrayList;
import Controller.Scheduler;

/**
 * @author madelynkrasnay, Danish Butt
 *
 */
public class Elevator implements Runnable {
	
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
	
	private ArrayList<Integer>  schedule;
	private Integer destination = null;
	
	
	public Elevator(int numberOfFloors, ArrayList<Integer> schedule) {
		lamps = new boolean[numberOfFloors];
		motor = 0; //Stationary
		door = false; //door closed
		this.schedule = schedule;
	}
	
	public void setSchedule(ArrayList<Integer>  schedule) {
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
	public void setMotor(int state) {
		motor = state;
		if(state == 1) {
			setDirection(true);
		}
		else if (state == 2) {
			setDirection(false);
		}
		
	}
	
	public int getMotor() {
		return motor;
	}
	
	//(true=open)
	private void setDoor(boolean state) {
		door = state;
		System.out.println("Elevator door is now "+ (state? "open": "closed"));
		
	}
	
	public void setCurrentFloor(int floor) {
		currentFloor = floor;
		System.out.println("Elevator is now at floor "+ floor);
	}
	
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	//set by motor
	//true = up
	public void setDirection(boolean direction) {
		currentDirection = direction;
		System.out.println("Elevator is going "+ (direction? "up": "down"));
	}
	
	public boolean getDirection() {
		return currentDirection;
	}
	
	public void setDesination(int floor) {
		destination = floor;
		System.out.println("Elevator has the new destination of floor " + destination);
	}
	
	public int getDesination() {
		return destination;
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//stop requested
	//updates scheduler of an interternal floor button was pressed
	//call in floors according to input file
	public void buttonPress(int destinationFloor) {
		setLamp(destinationFloor, true);
		scheduler.elevatorButtonPressed(destinationFloor, currentDirection);
		
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
		//turn off lamp
		setLamp(location, false);
		//open doors
		setDoor(true);
		//wait to load
		try {
			System.out.println("Loading elevator car...\n");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//close doors
		setDoor(false);
	}
	
	//_________________________________________________________________
	
	private void scheduleNewDestination() {
		//set Destination to the top of the queue and pop
		synchronized(schedule){
			if(!schedule.isEmpty()) {
				setDesination(schedule.remove(0));
			} else {
				requestWork();
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
				setMotor(1);
				try {
					Thread.sleep(500);//however long we decide it take for an elevator to climb a floor
				} catch (InterruptedException e) {
				}
				locationUpdate(currentFloor + 1);
				
			}
			else if(currentFloor > destination){
				//go down a floor
				setMotor(2);
				try {
					Thread.sleep(500);//however long we decide it take for an elevator to descend a floor
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
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		
		String state = "WAITING";
		while(true) {
		
			//Initial State
			if(state.equals("WAITING")) {
				System.out.println("got to waiting");
				
				
				scheduleNewDestination();
				
				if (currentFloor == destination) {
					state = "ARRVIVED";
					
				}else if (currentFloor != destination) {
					state = "MOVING";
				}

			//Elevator moving state
			}else if(state.equals("MOVING")) {
				
				System.out.println("GOT to moving");
				if (currentFloor != destination) {
					
					travelToDestination();
				}else {
					state ="ARRIVED";
				}
			
			//Elevator arrived state	
			}else if (state.equals("ARRIVED")) {
				
			
				this.stopped(currentFloor);
		
		
				
				state = "WAITING";
			}
		}
		}
	}
	
	
//	// This method is the state machine for the elevator subsystem
//	public void elevatorStateMachine(String state) {
//		
//		
//		//Initial State
//		if(state.equals("WAITING")) {
//			
//			while(destination == null) {
//				scheduleNewDestination();
//			}
//			
//			if (currentFloor == destination) {
//				state = "ARRVIVED";
//				
//			}else if (currentFloor != destination) {
//				state = "MOVING";
//			}
//
//		//Elevator moving state
//		}else if(state.equals("MOVING")) {
//			
//			System.out.println("GOT to moving");
//			if (currentFloor != destination) {
//				
//				travelToDestination();
//			}else {
//				state ="ARRIVED";
//			}
//		
//		//Elevator arrived state	
//		}else if (state.equals("ARRIVED")) {
//			
//			if(currentFloor == destination) {
//				this.stopped(currentFloor);
//	
//				state = "WAITING";
//			}
//	
//		}
//	}
//		
//}
	
//	public void run() {
//	//give the Scheduler a second to set the scheduler
//	try {
//		Thread.sleep(500);
//	} catch (InterruptedException e) {
//	}
//	while(true) {
//			if(destination == null){
//				//tell the scheduler
//				requestWork();
//				//and get a destination
//				while(destination == null){
//					scheduleNewDestination();
//				}
//			}
//			else if(currentFloor == destination) {
//				//if you are at your destination
//				//get new destination
//					//stop
//				this.stopped(currentFloor);
//				while(currentFloor == destination) {
//					this.scheduleNewDestination();
//				}
//			}
//			//go to destination
//			travelToDestination();
//	}
//}	


