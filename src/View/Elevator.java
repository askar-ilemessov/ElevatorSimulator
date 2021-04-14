package View;

import java.util.ArrayList;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Controller.Scheduler;
import assignment3Package.Client;
import gui.ErrorPopUp;

/**
 * @author Madelyn Krasnay, Danish Butt, ifiok udoh, yasin Jaamac
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
	private Client client; //Client for remote procedure call over UDP
	public int portNumber;
	public BlockingQueue<String> rcvqueue = new ArrayBlockingQueue<String>(10);
	public int error = 0;
	public int errorCode = 0;
	
	private int elevatorNumber;
	
	
	//Different states for State Machine
	enum State {
		WAITING,
		MOVING,
		ARRIVED,
		STOPPED
	}

	private State state = State.WAITING;
	
	
	public Elevator(int numberOfFloors, ArrayList<Integer> schedule, int elevatorNumber, int portNumber) {
		lamps = new boolean[numberOfFloors];
		motor = 0; //Stationary
		door = false; //door closed
		this.schedule = schedule;
		this.elevatorNumber = elevatorNumber;
		this.client = new Client(2010 + portNumber); //creates client with port number based on elevator number

	}
	
	//Getters and Setters_________________________________________________________________
	
	public int getNumber() {
		return elevatorNumber;
	}
	
	public void setSchedule(ArrayList<Integer>  schedule) {
		this.schedule = schedule;
	}
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int code) {
		this.errorCode=code;
		
	}
	
	//floor is index in lamps array
	//state (true=on)
	private void setLamp(int floor, boolean state) {
		lamps[floor]=state;
	}
	
	
	//(0=stopped, 1=moving up, 2=moving down)
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
	//Performs nesacary tasks in response to events
	
	//stop requested
	//updates scheduler of an internal floor button was pressed
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
		//Wait 1 seconds to simulate doors opening
		 try {
			 Thread.sleep(1000);
           } catch (InterruptedException e)  {
              System.out.println("Error while opening door.");
           }
		this.destination=null;
		//open doors
		setDoor(true);
		
		//wait to load
		try {
			System.out.println("Loading elevator " + elevatorNumber + "'s car...\n");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Wait 1 seconds to simulate doors closing
		 try {
			 Thread.sleep(1000);
           } catch (InterruptedException e)  {
              System.out.println("Error while closing door.");
           }
		this.destination=null;
		//close doors
		setDoor(false);
	}
	
	//Elevator Actions_________________________________________________________________
	

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
					e.printStackTrace();
				}
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
	
	
	//This method is used to travel to a destination
	protected void travelToDestination() {
		while(currentFloor != destination) {
			if (currentFloor < destination){
				
				long startTime= System.currentTimeMillis();   //This is used to get the start time of the elevator moving
				
				 //go up a floor
				setMotor(1);
				
				//Simulating elevator moving up
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				
				locationUpdate(currentFloor + 1);
				
				long endTime = System.currentTimeMillis();    //This is used to get the end time of the elevator moving
				long totalTime = endTime-startTime;
				
				if(totalTime >9) {                           //If the total time taken is 10 or higher trigger door sensor error
					raiseError(33);
				}
			}
			else if(currentFloor > destination){
				
				long startTime= System.currentTimeMillis();  //This is used to get the start time of the elevator moving
				//go down a floor
				setMotor(2);
				
				//Simulating elevator moving down
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				locationUpdate(currentFloor - 1);
				 
				long endTime = System.currentTimeMillis();   //This is used to get the end time of the elevator moving
				long totalTime = endTime-startTime;
				
				if(totalTime>9) {                         //If the total time taken is 10 or higher trigger door sensor error
					raiseError(33);
				}
			}
		}
	}
	
	//RCV procesing_________________________________________________________________
	
	public class RcvProcess implements Runnable{
		private Elevator e;
		public RcvProcess(Elevator e) {
			this.e = e;
		}
		public void run() {

				while(client.isOpen()) {
					e.client.recv(e.rcvqueue);
					e.processRcvQueue();
			}
		}
	}
	
	//Process remote procedure calls received into rcv queue
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
	
	//Error handeling_________________________________________________________________
	
	//Handles different error codes 
	public void handleError(int error) {
		String s;
		ErrorPopUp e;
		if(error == 31) {
			s = "Elevator " + this.getNumber() + " has a COMPLETE SYSTEM FAILURE\n";
			System.out.println(s);
			raiseError(31);
			e = new ErrorPopUp(s);
			Thread err = new Thread(e);
			err.start();
		}else if (error == 32) {
			s = "Elevator " + this.getNumber() + " has a DOOR SENSOR ERROR";
			System.out.println(s);
			raiseError(32);
			e = new ErrorPopUp(s);
			Thread err = new Thread(e);
			err.start();
		}else if (error ==33) {
			s = "Elevator " + this.getNumber() + " has a FLOOR SENSOR ERROR\n" + "The elevator took too long to reach the floor\n"
							+ "Check if the elevator is stuck or if the arrival sensor has failed";
			System.out.println(s);
			setErrorCode(error);
			e = new ErrorPopUp(s);
			Thread err = new Thread(e);
			err.start();
		}
	}
	
	
	private void raiseError(int error) {
		this.error = error;
		
	}
	
	//Checks if an error code has happened
	private void checkError() {
		if(error == 31 || error == 32 || error == 33) {
			this.setStateStopped();
		}
	}

	//Run and state machiene_________________________________________________________________

	//run()
	public void run() {
			Thread rcvProccessThread = new Thread(new RcvProcess(this));//Create and start thread to process received remote procedure calls
			rcvProccessThread.start();
			//give the Scheduler a second to set the scheduler
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			
			elevatorStateMachine();
	}
	
	public void setStateStopped() {
		raiseError(11);
		this.state = State.STOPPED;
		System.out.println("Elevator " + this.getNumber()+ " has been sent to the ground floor and brought off line.");
	}
	
	public void elevatorStateMachine() {
		
	while(true) {
			
			if(state == State.WAITING) {
				while(destination==null) {
					scheduleNewDestination();  //Get request 
				}
				
				//request received
				state = State.MOVING;
				checkError();
				
			}else if(state ==State.MOVING) {
				//not at destination
				if (currentFloor != destination) {
					
					if(getErrorCode()==33) {
						raiseError(33);
						setErrorCode(0);
						checkError();	
					}
					
					travelToDestination(); //go to destination
				}
				else {
					state = State.ARRIVED;  
				}
				checkError();
				
			}else if(state == State.ARRIVED) {
				this.stopped(currentFloor);  //destination reached 
				
				
				state = State.WAITING; //Go back and wait for another request
				checkError();
					
				
			}else if(state == State.STOPPED) {
				System.out.println("Elevator " + this.getNumber() + " has been sent to the ground floor and brought off line.");
				
				this.destination=0; //Set destination to Floor 0
				travelToDestination(); //Travel to Floor 
				this.setDoor(true);//open door
				this.destination=null; //Set destination to null
				
				//create error request code for repair complete?
				
				System.out.println("The issue with elevator " + this.getNumber() + " has been fixed");
				state = State.WAITING; //Go back and wait for another request
				
				}
			}	
	}

	public void closeClient() {
		this.client.close();
	}
}