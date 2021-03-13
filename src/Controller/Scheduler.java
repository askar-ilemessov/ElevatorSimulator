package Controller;
/**
 
* this is removed from the schedule since multiple schedulers may be needed for multiple elevators.
 * 
 * each elevator would need their own scheduler.
 */

import java.util.ArrayList;

import View.Elevator;
import View.Floors;

/**
 * @author madelynkrasnay
 *
 */
public class Scheduler extends Thread {
	
	private ArrayList<Elevator> elevator = new ArrayList<>();
	private Floors floors;
	ArrayList<ArrayList<Integer>> schedule = new ArrayList<ArrayList<Integer>>();
	
	public Scheduler(ArrayList<Elevator> elevator, Floors floors, ArrayList<ArrayList<Integer>>  schedule) {
		this.elevator = elevator;
		this.floors = floors;
		this.schedule = schedule;
	}
	
	private void addFloorButtonPressedToSchedule(int originFloor, boolean direction) {
		int sendRequestToElevator=0;
		int difference = 20;
		
		synchronized(this.schedule) {
			for(int i=0; i<3; i++) {
				
				if(Math.abs(elevator.get(i).getCurrentFloor() - originFloor) < difference) {
					difference = Math.abs(elevator.get(i).getCurrentFloor() - originFloor);
					sendRequestToElevator = i;
				}
			}
			
			schedule.get(sendRequestToElevator).add(originFloor);
			System.out.println("Elevator " + elevator.get(sendRequestToElevator).getId() + " has received this request to go to" + originFloor);
			schedule.notifyAll();
		}
	}
	
	private void addElevatorButtonPressedToSchedule(int destinationFloor, boolean direction, int currentFloor) {
		int sendRequest = 0;
		synchronized(this.schedule) {
			for(int i=0; i<3;i++) {
				if(elevator.get(i).getCurrentFloor()== currentFloor) {
					sendRequest = i;
				}	
			}
			schedule.get(sendRequest).add(destinationFloor);
			System.out.println("Elevator " + elevator.get(sendRequest).getId() + " is going to" + destinationFloor);
			schedule.notifyAll();
		}
	}
	
	
	
//	private void addToSchedule(int destinationFloor, boolean direction, int currentFloor) {
//		synchronized(this.schedule){
//			
//			if(schedule.size()==0||schedule.size()==1) {//case for where there is zero one location in the queue
//				schedule.add(destinationFloor);
//			}
//			else {//case for more than one location in the queue:
//				//go through collection and add at first index where the 
//				//the diffrence between the floors that would be on either 
//				//side of the potential stop location indicates travel in 
//				//the right direction
//				for(int i = 1; i < schedule.size(); i++) {
//					if((schedule.get(i-1)-schedule.get(i)) >=0 && direction) {
//						schedule.add(i+1,floor);
//						break;
//					}
//					else if(schedule.get(i-1)-schedule.get(i) <=0 && !direction) {
//						schedule.add(i,floor);
//						break;
//					}
//					else {//if the elevator never passes this floor going in the right direction, 
//						//just add it at the end of the queue
//						schedule.add(floor);
//					}
//				}
//			}
//			schedule.notifyAll();
//		}
//	}
//	
	//Event Handeling_________________________________________________
	//Performs nessacary tasks in response to events
	//will be overhauled with the UDP update
	
	//elevator stop request from floor
	//direction true = up
	public void FloorButtonPress(int originFloor, boolean direction) {
		//update schedule
		this.addFloorButtonPressedToSchedule(originFloor, direction);
	}
	
	public ArrayList<ArrayList<Integer>> getQueue() {
		return schedule;
	}
	
	//elevator stop request from elevator
	public void elevatorButtonPressed(int destinationFloor, boolean direction, int currentFloor) {
		this.addElevatorButtonPressedToSchedule(destinationFloor, direction, currentFloor);
	}
	
	//State updates from elevator:
	//notify floors / elevator where applicable
	
	//location updated
	//update floors
	public void elevatorLocationUpdated(int floor) {
		floors.elevatorLocationUpdated(floor);
	}
	
	
	//direction updated
	//update floors
	public void elevatorDirectionUpdated(boolean direction) {
		floors.elevatorDirectionUpdated(direction);
	}
	
	//elevator stopped
	//let floors know
	public void elevatorStopped(int floor, boolean direction) {
		floors.elevatorArrived(floor, direction);
	}
	
	public void elevatorRequestsWork() {
		//do nothing for now, that might change with multi. elevators
	}
	
	//_________________________________________________________________
	
	public void run() {
		//give floors and elevator a reference to you
		elevator.setScheduler(this);
		floors.setScheduler(this);
		while(true) {
			
			
		}
	}

}

