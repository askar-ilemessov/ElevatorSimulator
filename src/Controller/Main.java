package Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import View.Elevator;
import View.Floors;
import View.InputFileReader;
import View.SimulatedArrival;

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
		

		// floor numbers in order the elevator is to visit them
		// make an array of queues for multiple elevators, one for each elevator
		ArrayList<Integer> schedule = new ArrayList<>();
		
		
		//take in the file of arrivals to be simulated and store them to be 
		//initialized in floors
		InputFileReader ifr = new InputFileReader();
		File file = new File("src/Model/InputFile.txt");
		ArrayList<SimulatedArrival> list = new ArrayList<SimulatedArrival>();
		try {
			list = ifr.readInFile(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//initialize and start threads
		Elevator elevator1 = new Elevator(numberOfFloors, schedule);
		Thread elevator1Thread = new Thread(elevator1, "Elevator 1");

		Floors floors = new Floors(numberOfFloors, list);
		Thread floorsThread = new Thread(floors, "Floors");

		Scheduler scheduler = new Scheduler(elevator1, floors, schedule);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		
		elevator1Thread.start();
		floorsThread.start();
		schedulerThread.start();

	}

}
