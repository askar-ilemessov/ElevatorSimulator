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
		int numberOfElevators = 3;
		

		// floor numbers in order the elevator is to visit them
		// make an array of queues for multiple elevators, one for each elevator
		ArrayList<ArrayList<Integer>> elevatorSchedules = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<= numberOfElevators; i++) {
			elevatorSchedules.add(new ArrayList<Integer>());
		}
		
		
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

		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		ArrayList<Thread> elevatorThreads = new ArrayList<Thread>();
		for(int i=0; i<= numberOfElevators; i++) {
			elevators.add(new Elevator(numberOfFloors, elevatorSchedules.get(i), i));
			elevatorThreads.add(new Thread(elevators.get(i), "Elevator "+i));
		}

		Floors floors = new Floors(numberOfFloors, numberOfElevators, list);
		Thread floorsThread = new Thread(floors, "Floors");

		Scheduler scheduler = new Scheduler(elevators, floors, elevatorSchedules);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		
		
		for(int i=0; i<= numberOfElevators; i++) {
			elevatorThreads.get(i).start();
		}
		floorsThread.start();
		schedulerThread.start();

	}

}
