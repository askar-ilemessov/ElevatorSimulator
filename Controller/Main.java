

/**
 * 
 */

/**
 * @author madelynkrasnay
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int numberOfFloors = 7;
		
		Agent elevator1 = new elevator(numberOfFloors);
		Thread elevator1Thread = new Thread(elevator1, "Elevator 1");
		
		Chef floors = new Floors(numberOfFloors);
		Thread floorsThread = new Thread(floors, "Floors");
		
		Chef scheduler = new Scheduler(elevator1, floors);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		
		elevator1Thread.start();
		floorsThread.start();
		schedulerThread.start();

	}

}
