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
		ArrayList<Integer> schedule1 = new ArrayList<>();
		ArrayList<Integer> schedule2 = new ArrayList<>();
		ArrayList<Integer> schedule3 = new ArrayList<>();
		
		//Add them to schedule arraylist that will handle all of them
		ArrayList<ArrayList<Integer>> schedule = new ArrayList<ArrayList<Integer>>();
		schedule.add(schedule1);
		schedule.add(schedule2);
		schedule.add(schedule3);
		
		
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
		
		//Create 3 elevators
		Elevator elevator1 = new Elevator(1, numberOfFloors, schedule);
		Thread elevator1Thread = new Thread(elevator1, "Elevator 1");
		
		Elevator elevator2 = new Elevator(2, numberOfFloors, schedule);
		Thread elevator2Thread = new Thread(elevator2, "Elevator 2");
		
		Elevator elevator3 = new Elevator(3, numberOfFloors, schedule);
		Thread elevator3Thread = new Thread(elevator3, "Elevator 3");
		
		ArrayList<Elevator> elevator = new ArrayList<Elevator>();
		
		elevator1Thread.start();
		elevator2Thread.start();
		elevator3Thread.start();
		
		elevator.add(elevator1);
		elevator.add(elevator2);
		elevator.add(elevator3);
		
		

		Floors floors = new Floors(numberOfFloors, list);
		Thread floorsThread = new Thread(floors, "Floors");

		Scheduler scheduler = new Scheduler(elevator, floors, schedule);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
	
		floorsThread.start();
		schedulerThread.start();

	}

}
