package View;
/**
 * Calls appropreate event handeling methoids in 
 * elevator and floors to reflect each the entry in the 
 * input file at the apropreate time.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author madelynkrasnay danish1371
 *
 */
public class InputFileReader {
	
	
	//parses Input file into an a
	public ArrayList<Object> readInFile(File file) throws FileNotFoundException 
	{ 
		
		//File file = new File("./InputFile.txt");
		Scanner scan = new Scanner(file);

		ArrayList<Object> information = new ArrayList<Object>();

		while (scan.hasNextLine()) {
			
			ArrayList<Object> info = new ArrayList<>();

			String line = scan.nextLine();
			String[] fileInfo = line.split(" ");
		
				if (fileInfo.length == 4) {

					//Time
					String time = fileInfo[0];
					String[] timeValues = time.split(":");
					if(timeValues.length == 4) {
						int hours = Integer.parseInt(timeValues[0]);
						int minutes = Integer.parseInt(timeValues[1]);
						int seconds = Integer.parseInt(timeValues[2]);
						
						int timeInSeconds = (hours*60*60) +(minutes*60) +seconds;
						info.add(timeInSeconds);
						
					}else {
						System.out.println("Incorrect input for time");
					}
					
					
					//Original Floor Number
					int intialFloorNumber = Integer.parseInt(fileInfo[1]);
					info.add(intialFloorNumber);
					
					//Direction
					String value = fileInfo[2];
					boolean direction;
					
					if(value=="Up") {
						direction=true;
					}else {
						direction = false;
					}
					info.add(direction);
					
					//Destination Floor
					int destinationFloorNumber = Integer.parseInt(fileInfo[3]);
					info.add(destinationFloorNumber);

					information.add(info);

				} else {
					System.out.print("Correct inputs not receieved. \n");
					
				}
		
		}
		scan.close();
		return information;
	}
	
}


