package View;
/**
 * Calls appropreate event handeling methoids in 
 * elevator and floors to reflect each the entry in the 
 * input file at the apropreate time.
 */

/**
 * @author madelynkrasnay
 *
 */
public class InputFileReader {
<<<<<<< HEAD
	
	
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

=======
>>>>>>> bdc4f42b6d1d0809d5d094f363be4ed7b64b4a97

}
