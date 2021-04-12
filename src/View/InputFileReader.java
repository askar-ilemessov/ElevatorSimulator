package View;
/**
 * Calls appropriate event handling methods in 
 * elevator and floors to reflect each the entry in the 
 * input file at the appropriate time.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author madelynkrasnay, Danish Butt
 *
 */
public class InputFileReader {

	//parses Input file into an a
	public ArrayList<SimulatedArrival> readInFile(File file) throws FileNotFoundException 
	{ 
		
		Scanner scan = new Scanner(file);

		ArrayList<SimulatedArrival> arrivals = new ArrayList<SimulatedArrival>();

		while (scan.hasNextLine()) {

			String line = scan.nextLine();
			String[] fileInfo = line.split(" ");
//			for(int i = 0; i< fileInfo.length; i++) {
//				System.out.println(fileInfo[i]);
//			}
//			System.out.println("fileInfo Length is : " + fileInfo.length);
//		
				if (fileInfo.length == 5) {

					//Time
					String time = fileInfo[0];
					String[] timeValues = time.split(":");
					long timeInMilliseconds = -1;//this way if it fails to intitilize it doesn't try to run with it...
					//TODO: handle this more gracefully
					if(timeValues.length == 3) {
						int hours = Integer.parseInt(timeValues[0]);
						int minutes = Integer.parseInt(timeValues[1]);
						int seconds = Integer.parseInt(timeValues[2]);
						
						timeInMilliseconds = ((hours*3600) +(minutes*60) +seconds)*1000;
						
					}else {
						System.out.println("Incorrect input for time");
					}
					
					
					//Original Floor Number
					int intialFloorNumber = Integer.parseInt(fileInfo[1]);
					
					//Direction
					String value = fileInfo[2];
					boolean direction;
					
					if(value.equals("Up")) {
						direction=true;
					}else {
						direction = false;
					}
					
					//Destination Floor
					int destinationFloorNumber = Integer.parseInt(fileInfo[3]);
					
					//Error Code
					int errorCode = Integer.parseInt(fileInfo[4]);
					
					//create a simulated arrival object and add it to my ArrayList of arrivals
					SimulatedArrival arrival = new SimulatedArrival(timeInMilliseconds, intialFloorNumber, direction, destinationFloorNumber, errorCode);
					arrivals.add(arrival);

				} else {
					System.out.print("Correct inputs not receieved. \n");
					
				}
		
		}
		scan.close();
		return arrivals;
	}
	
}


