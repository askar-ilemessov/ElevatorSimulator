import java.util.Queue;
import java.util.LinkedList;

/**
 * 
 */

/**
 * @author madelynkrasnay
 *
 */
class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int numberOfFloors = 7;
		
		Agent elevator1 = new elevator(numberOfFloors);
		Thread elevator1Thread = new Thread(elevator1, "Elevator 1");
		
		//floor numbers in order the elevator is to visit them
		//make an array of queues for multiple elevators, one for each elevator
		Queue<Integer> schedule = new LinkedList<>();
		
		Chef floors = new Floors(numberOfFloors);
		Thread floorsThread = new Thread(floors, "Floors");
		
		Chef scheduler = new Scheduler(elevator1, floors, schedule);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		
		elevator1Thread.start();
		floorsThread.start();
		schedulerThread.start();

	}

}
