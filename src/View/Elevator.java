package View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Controller.Scheduler;
import View.Floors.RcvProcess;
import assignment3Package.Client;

/**
 * @author madelynkrasnay, Danish Butt, ifiok udoh, yasin Jaamac
 *
 */
public class Elevator implements Runnable {
	
	private Scheduler scheduler;
//	private String scheduler;
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
	
	private Client client;
	public int portNumber;
	public BlockingQueue<String> rcvqueue = new ArrayBlockingQueue<String>(10);

	
	private int elevatorNumber;
	
	enum State {
		WAITING,
		MOVING,
		ARRIVED,
		STOPPED
	}
	//Changed
	State state = State.WAITING;
	
	
	public Elevator(int numberOfFloors, ArrayList<Integer> schedule, int elevatorNumber, int portNumber) {
		lamps = new boolean[numberOfFloors];
		motor = 0; //Stationary
		door = false; //door closed
		this.schedule = schedule;
		this.elevatorNumber = elevatorNumber;
		this.client = new Client(2010 + portNumber); //creates client with port number based on elevator number

	}
	

	
	public void setSchedule(ArrayList<Integer>  schedule) {
		this.schedule = schedule;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	

	public void setStateStopped() {
		System.out.println("Elevator " + elevatorNumber +" has stopped");
		this.currentFloor=0;
		this.destination=null;
		System.out.println("Elevetor #" + this.getNumber() + " has been set to default state and has been send to floor #" + this.getCurrentFloor());
		state = State.WAITING; //Go back and wait for another request
		//this.state= State.STOPPED;
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
		System.out.println("Elevator " + elevatorNumber + " door is now "+ (state? "open": "closed"));
		
	}
	
	public void setCurrentFloor(int floor) {
		currentFloor = floor;
		System.out.println("Elevator " + elevatorNumber + " is now at floor "+ floor);
	}
	
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	//set by motor
	//true = up
	public void setDirection(boolean direction) {
		currentDirection = direction;
		System.out.println("Elevator " + elevatorNumber + " is going "+ (direction? "up": "down"));
	}
	
	public boolean getDirection() {
		return currentDirection;
	}
	
	public void setDesination(int floor) {
		destination = floor;
		System.out.println("Elevator " + elevatorNumber + " has the new destination of floor " + destination);
	}
	
	public Integer getDesination() {
		return destination;
	}
	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	
	//stop requested
	//updates scheduler of an interternal floor button was pressed
	//call in floors according to input file
	public void buttonPress(int destinationFloor) {
		setLamp(destinationFloor, true);
		String data = "elevatorButtonPressed" + "," + destinationFloor + "," + Boolean.toString(currentDirection);
		try {
			this.client.sendData(data, 3001);//send remote procedure call to Scheduler receive socket
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//called periodically when in motion
	//floor change as a result of regular motion (
	//location = floor number)
	public void locationUpdate(int location) {
		setCurrentFloor(location);
		//update scheduler
		String data = "elevatorLocationUpdated" + "," + elevatorNumber + "," + location;
		try {
			this.client.sendData(data, 3001);//send remote procedure call to Scheduler receive socket
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//stopped at a floor 
	//opens the doors, lets people load and closes them again
	//(location = floor number)
	public void stopped(int location) {
		setMotor(0);
		System.out.println("Elevator" + elevatorNumber + " stopped at floor " + location);
		//update scheduler
		String data = "elevatorStopped" + "," + elevatorNumber + "," + location + "," + Boolean.toString(currentDirection);
		try {
			this.client.sendData(data, 3001);//send remote procedure call to Scheduler receive socket
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//turn off lamp
		setLamp(location, false);
		//open doors
		setDoor(true);
		//wait to load
		try {
			System.out.println("Loading elevator" + elevatorNumber + "'s car...\n");
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
				//System.out.println("here!!!");
			}
		}
	}
	
	//request new destination from scheduler
	//will be updated when there is new work on the queue
	private void requestWork() {
		String data = "elevatorRequestsWork" + "," + elevatorNumber;
		try {
			System.out.println("Elevator " + elevatorNumber + " is on standby");
			this.client.sendData(data, 3001);//send remote procedure call to Scheduler receive socket
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
	
	public class RcvProcess implements Runnable{
		private Elevator e;
		public RcvProcess(Elevator e) {
			this.e = e;
		}
		public void run() {
			while(true) {
				e.client.recv(e.rcvqueue);
				e.processRcvQueue();
			}
		}
	}
	
	public void processRcvQueue() {
		if(!this.rcvqueue.isEmpty()) {
			String mssg = this.rcvqueue.remove();
			String[] param = mssg.trim().split(",");
			switch(param[0]) {
				case "requestWork":
					requestWork();
					break;
				case "handleError":
					handleError(Integer.parseInt(param[1]));
					break;
				default:
					break;
			}
			
		}
	}
	
	
	public void handleError(int error) {
		
		if(error == 31) {
			System.out.println("Error with Elevator #" + this.getNumber() + " error #" + error);
			elevatorStateMachine(State.STOPPED);
		}
		
	}
	
	//run()
	public void run() {
		Thread rcvProccessThread = new Thread(new RcvProcess(this));//Create and start thread to process received remote procedure calls
		rcvProccessThread.start();
		//give the Scheduler a second to set the scheduler
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		
		elevatorStateMachine(State.WAITING);
		}



	public int getNumber() {
		return elevatorNumber;
	}
	
	public void elevatorStateMachine(State state) {
		
	while(true) {
			
			if(state == State.WAITING) {
				System.out.println("State: Waiting");
				while(destination==null) {
					scheduleNewDestination();  //Get request 
				}
				
				//request received

				state = State.MOVING;
				
			}else if(state ==State.MOVING) {
				//not at destination
				if (currentFloor != destination) {
					
					travelToDestination(); //go to destination
				}
				else {
					state = State.ARRIVED;  
				}
				
			}else if(state == State.ARRIVED) {
				this.stopped(currentFloor);  //destination reached
				
				this.destination=null;
				
				state = State.WAITING; //Go back and wait for another request
				
			}else if(state == State.STOPPED) {
				
				//this.stopped(currentFloor);  //destination reached
				//System.out.println("Elevator #"+this.getNumber() + " has been stucked between floors: " + (this.getCurrentFloor()-1) + " and " + this.getCurrentFloor());
				System.out.println("Tehcnitians already working on the problem");
				this.currentFloor=0;
				this.destination=null;
				System.out.println("Elevetor #" + this.getNumber() + " has been set to default state and has been send to floor #" + this.getCurrentFloor());
				state = State.WAITING; //Go back and wait for another request
				System.out.println("The issue with "+"Elevetor #" + this.getNumber() + " has been fixed and Elevator setted to Waiting state");
				}
		
			}
		
	}

}