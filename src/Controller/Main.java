package Controller;

import java.util.Queue;
import java.util.LinkedList;
import java.io.File;

import View.Elevator;
import View.Floors;
import View.InputFileReader;

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
		
		Elevator elevator1 = new Elevator(numberOfFloors);
		Thread elevator1Thread = new Thread(elevator1, "Elevator 1");
		
		//floor numbers in order the elevator is to visit them
		//make an array of queues for multiple elevators, one for each elevator
		Queue<Integer> schedule = new LinkedList<>();
		
		Floors floors = new Floors(numberOfFloors);
		Thread floorsThread = new Thread(floors, "Floors");
		
		Scheduler scheduler = new Scheduler(elevator1, floors, schedule);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		
		elevator1Thread.start();
		floorsThread.start();
		schedulerThread.start();
		
		
		//read in file and send out new arrivals
		File file = new File("src/model/InputFile.txt");
		InputFileReader inputReader = new InputFileReader();
		inputReader.readInFile(file);

	}

}
