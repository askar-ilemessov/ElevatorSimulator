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
	
	//elevator state varibles
	//lamps (true = on) (index = floor)
	private array<boolean> lamps;
	//motor (0=stoped, 1=moving up, 2=moving down)
	private int motor  = new int;
	//door (true = open)
	private boolean door = new boolean;
	//indicators
	private int floorIndicator;
	private boolean directionIndicator;
	
	//constructor, takes # of lamps
	
	//state management_______________________________________________
	
	//floor is index in lamps array
	//state (true=on)
	private void setLamp(int Floor, bool state) {
		
	}
	
	//(0=stoped, 1=moving up, 2=moving down)
	private void setMotor(int state) {
		
	}
	
	//(true=open)
	private void setDoor(bool state) {
		
	}
	
	//Event Handeling_________________________________________________
	//proforms nessacary tasks in responce to events
	
	//request new destination
	
	//stop requested
	//updates sceduler of an interternal floor button was pressed
	private void buttonPress(int destinationFloor) {
		
	}
	
	//floor change as a resault of regular motion (
	//location = floor number)
	private void locationUpdate(int location) {
		
	}
	
	//stoped at a floor 
	//(location = floor number)
	private void stopped(int location) {
		
	}
	
	//resuming motion
	
	
	//_________________________________________________________________
	
	//run()
	public void run() {
	}

}
