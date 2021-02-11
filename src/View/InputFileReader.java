package View;
/**
 * Calls appropreate event handeling methoids in 
 * elevator and floors to reflect each the entry in the 
 * input file at the apropreate time.
 */

import java.io.File;
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
	public void readInFile(File file) 
	{ 
		
		File file = new File("./InputFile.txt");
		Scanner scan = new Scanner(file);

		ArrayList<Object> schedule = new ArrayList<Object>();

		while (scan.hasNextLine()) {
			
			ArrayList<Object> info = new ArrayList<>();

			String line = scan.nextLine();
			String[] fileInfo = line.split(" ");
		
				if (fileInfo.length == 4) {

					//Time
					info.add(fileInfo[0]);
					
					//Original Floor Number
					info.add(fileInfo[1]);
					
					//Direction
					info.add(fileInfo[2]);
					
					//Destination Floor
					info.add(fileInfo[3]);

					schedule.add(info);

				} else {
					System.out.print("Correct inputs not receieved. \n");
					
				}
		
		}
		scan.close();
		
	}
}


