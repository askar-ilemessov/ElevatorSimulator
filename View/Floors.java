/**
 * The floor subsystem is used to simulate the arrival of passengers to the elevators and for simulating all button presses and lamps.
 * 
 */

/**
 * @author madelynkrasnay
 *
 */
public class Floors extends Thread {
	
	
	//local state varables to reflect our ouputs:
	//an array of lamps for all floors
	
	public Floors() {
		
	}
	
	public void setScheduler(Scheduer scheduler) {
		this.scheduler = scheduler;
	}
	
	//state management_______________________________________________
	
	//floor is index in lamps array
	//direction true = going up, false = going down
	//state true=on
	private void setLamp(int Floor, bool direction, bool state) {
			
	}
	
	//Event Handeling_________________________________________________
	//proforms nessacary tasks in responce to events
	
	//a request for an elevator to visit this floor
	//(true = up, false = down)
	//floor = floor number the button is on
	private void buttonPress( int floor, bool direction) {
		System.out.println("Floor %d requested an elevator going %d", floor, direction);
		
	}
	
	//elevator arived
	private void elevatorArrived( int floor, bool direction) {
		System.out.println("Floor %d elevator has arrived going %d", floor, direction);
		setLamp(floor, direction, false);
		
	}
	
	//_________________________________________________________________
	
	
	//run()
	//should wait to be notified by the scheduler
	//should notify arivals as they appear in the floor input file
	public void run() {
		while(true) {
			
		}
	}

}
