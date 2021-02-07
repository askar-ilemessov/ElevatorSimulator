package View;

import Controller.Scheduler;



/**
 * The floor subsystem is used to simulate the arrival of passengers to the elevators and for simulating all button presses and lamps.
 * 
 */

/**
 * @author madelynkrasnay
 *
 */
public class Floors extends Thread {
	
	
	//local state variables to reflect our outputs:
	//an array of lamps for all floors where each 
	//floor had 2 lamps: one for going up, one for going down.
	private boolean [] [] lamps;
	private boolean elevatorDirectionIndicator;
	private int elevatorFloorIndicator;
	
	private Scheduler scheduler;
	
	
	public Floors(int numberOfFloors) {
		lamps = new boolean [numberOfFloors] [2];
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	//state management_______________________________________________
	
	//floor is index in lamps array
	//direction true = going up, false = going down
	//state true=on
	private void setLamp(int floor, boolean direction, boolean state) {
		lamps[floor][(direction? 1 : 0)] = state;
		System.out.println("The lamp on floor "+ floor 
				+ " for the direction " + state
				+ " is now" + state);
	}
	
	public boolean getLampValue(int floor, boolean direction) {
		return lamps[floor][(direction? 1 : 0)];
	}
	
	private void setElevatorFloorIndicator(int floor) {
		elevatorFloorIndicator = floor;
		System.out.println("Floors' elevator location indicator now reads " + elevatorFloorIndicator);
	}
	
	
	public int getElevatorFloorIndicator() {
		return elevatorFloorIndicator;
	}
	
	private void setElevatorDirectionIndicator(boolean direction) {
		elevatorDirectionIndicator = direction;
		System.out.println("Floors' direction location indicator now reads " + elevatorDirectionIndicator);
	}
	
	public boolean getElevatorDirectionIndicator() {
		return elevatorDirectionIndicator;
	}
	
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//a request for an elevator to visit this floor
	//(true = up, false = down)
	//floor = floor number the button is on
	private void buttonPress( int floor, boolean direction) {
		scheduler.FloorButtonPress(floor, direction);
		System.out.println("Floor " + floor 
				+ " requested an elevator going " + direction);
		
	}
	
	//elevator arrived
	public void elevatorArrived( int floor, boolean direction) {
		System.out.println("An elevator arrived at " + floor 
				+ " going " + direction);
		setLamp(floor, direction, false);
		
	}
	
	public void elevatorLocationUpdated(int floor) {
		setElevatorFloorIndicator(floor);
	}
	
	public void elevatorDirectionUpdated(boolean direction) {
		setElevatorDirectionIndicator(direction);
	}
	
	//_________________________________________________________________
	
	
	//run()
	//should wait to be notified by the scheduler
	//should notify arrivals as they appear in the floor input file
	public void run() {
		while(true) {
			
		}
	}

}
