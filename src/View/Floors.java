package View;

import java.util.ArrayList;

import Controller.Scheduler;



/**
 * The floor subsystem is used to simulate the arrival of passengers to the elevators and for simulating all button presses and lamps.
 * 
 */

/**
 * @author madelynkrasnay
 *
 */
public class Floors implements Runnable {
	
	
	//local state variables to reflect our outputs:
	//an array of lamps for all floors where each 
	//floor had 2 lamps: one for going up, one for going down.
	private boolean [] [] lamps;
	private boolean elevatorDirectionIndicator;
	private int elevatorFloorIndicator;
	
	private Scheduler scheduler;
	private ArrayList<SimulatedArrival> arrivals;
	
	//Collection of people waiting for elevators to arrive
	//this is checked when an elevator arrives to see where the people waiting want to go
	//the index of the first array represents the floor (index 0 -> first floor)
	//the second array is to allow multiple people to wait on the same floor
	//the integers stored in the second array represent destinations those waiting at that floor
	//those integers do not need to be unique even though elevators only need to respond to unique values in those arrays
	//as this allows us to track the number of people boarding the elevator later when we consider elevator capacity.
	private ArrayList<Integer>[] waiting; 
	
	public Floors(int numberOfFloors) {
		lamps = new boolean [numberOfFloors] [2];
		this.arrivals = new ArrayList<SimulatedArrival>();
	}
	
	public Floors(int numberOfFloors, ArrayList<SimulatedArrival> arrivals) {
		lamps = new boolean [numberOfFloors] [2];
		this.arrivals = arrivals;
		this.waiting= new ArrayList[numberOfFloors];
		for(int i=0; i<numberOfFloors; i++) {
			this.waiting[i] = new ArrayList<Integer>();
		}
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	//state management_______________________________________________
	
	//floor is index in lamps array
	//direction true = going up, false = going down
	//state true=on
	public void setLamp(int floor, boolean direction, boolean state) {
		lamps[floor][(direction? 1 : 0)] = state;
		System.out.println("The lamp on floor "+ floor 
				+ " for the direction " + (direction? "up": "down")
				+ " is now " + (state? "on": "off"));
	}
	
	public boolean getLampValue(int floor, boolean direction) {
		return lamps[floor][(direction? 1 : 0)];
	}
	
	public void setElevatorFloorIndicator(int floor) {
		elevatorFloorIndicator = floor;
		System.out.println("Floors' elevator location indicator now reads " + 
		elevatorFloorIndicator);
	}
	
	
	public int getElevatorFloorIndicator() {
		return elevatorFloorIndicator;
	}
	
	public void setElevatorDirectionIndicator(boolean direction) {
		elevatorDirectionIndicator = direction;
		System.out.println("Floors' direction location indicator now reads " + 
		(elevatorDirectionIndicator? "up": "down"));
	}
	
	public boolean getElevatorDirectionIndicator() {
		return elevatorDirectionIndicator;
	}
	
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//a request for an elevator to visit this floor
	//(true = up, false = down)
	//floor = floor number the button is on
	public void buttonPress( int floor, boolean direction) {
		System.out.println("Floor " + floor 
				+ " requested an elevator going " + (direction? "up": "down"));
		setLamp(floor, direction, true);
		scheduler.FloorButtonPress(floor, direction);
	}
	
	//elevator arrived
	public void elevatorArrived( int floor, boolean direction) {
		System.out.println("An elevator arrived at " + floor 
				+ " going " + (direction? "up": "down"));
		setLamp(floor, direction, false);
		
		//call elevator button press in scheduler for each of those waiting 
		//on this floor and going the appropriate direction
		for (int i=0; i < waiting[floor-1].size(); i++) {
				scheduler.elevatorButtonPressed(waiting[floor-1].get(i), direction, floor);
				waiting[floor-1].remove(i);
		}
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
		try {
			Thread.sleep(50);//sleep long enough for all threads to set up and initialize (should be done cleaner)
			//capture the time
			Long startTime = System.currentTimeMillis();
			System.out.println("Simulation starting...");
			
			for(SimulatedArrival arrival : arrivals) {
					//sleep until the next arrival is scheduled
	            	Thread.sleep(arrival.getTime() - (System.currentTimeMillis() - startTime));
	            	
	            	//simulate someone at the specified floor pressing the button in the appropriate direction
	            	buttonPress(arrival.getOriginFloor(), arrival.isDirection());
	            	
	            	//add person to collection of waiting people
	            	waiting[arrival.getOriginFloor()-1].add(arrival.getDestinationFloor());
	            	
			}
		} catch (InterruptedException e) {
			
		}
	}

}
