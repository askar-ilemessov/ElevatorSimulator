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
 * @author madelynkrasnay IfiokUdoh
 *
 */
class Main {

	/**
	 * @param args
	 * @throws  
	 */
	public static void main(String[] args){

		int numberOfFloors = 7;
		int numberOfElevators = 3;
		

		// floor numbers in order the elevator is to visit them
		// make an array of queues for multiple elevators, one for each elevator
		ArrayList<ArrayList<Integer>> elevatorSchedules = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<numberOfElevators; i++) {
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
		for(int i=0; i< numberOfElevators; i++) {
			elevators.add(new Elevator(numberOfFloors, elevatorSchedules.get(i), i, i*2)); //set port to i*2 to accommodate for 2 ports per elevator, ! for sending and 1 for receiving
			elevatorThreads.add(new Thread(elevators.get(i), "Elevator "+i));
		}

		//Floor Subsytem Thread on port 3002
		Floors floors = new Floors(numberOfFloors, numberOfElevators, list, 3002);
		Thread floorsThread = new Thread(floors, "Floors");

		//Scheduler thread on port 3000
		Scheduler scheduler = new Scheduler(elevators, floors, elevatorSchedules, 3000);
		Thread schedulerThread = new Thread(scheduler, "Scheduler");
		
		
		for(int i=0; i<numberOfElevators; i++) {
			elevatorThreads.get(i).start();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		floorsThread.start();
		schedulerThread.start();


	}
}
