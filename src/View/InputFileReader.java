package View;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;
/**
 * Calls appropriate event handling methods in 
 * elevator and floors to reflect each the entry in the 
 * input file at the appropriate time.
 */

/**
 * @author madelynkrasnay
 *
 */
public class InputFileReader {
	
	
	//parses Input file into an a
	public void readInFile(File file) 
	{ 
		
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
			       String [] line = (sc.nextLine().split(" "));
			       
			       String[] timeString = line[0].split(":");
			       //long instrTime = Integer.parseInt(timeString[1]) + 
			    	//	   Integer.parseInt(timeString[2])*60 + 
			    	//	   Integer.parseInt(timeString[0])*60*60;
			       
			       int originFloor = Integer.parseInt(line[1]);
			       
			       if (line[2]=="up") {
			    	   boolean direction = true;
			       }
			       else if (line[2]=="down") {
			    	   boolean direction = false;
			       }
			       else{
			    	   //break nicely
			       }
			       
			       int destinationFloor = Integer.parseInt(line[3]);
			       
			       //put instrtime, origin floor, direction, and destination as 
			       //an instruction into some kind of queue
			       
			  } 
		} catch (FileNotFoundException e) {
		} 
		  
	} 
}


