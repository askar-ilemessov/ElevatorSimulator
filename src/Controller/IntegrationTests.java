package Controller;
import Controller.Main;
import View.SimulatedArrival;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class IntegrationTests {
	
    private  ByteArrayOutputStream outContent;
    private  PrintStream originalOut;
    
	@Before
	public void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
	}
	
	@After
    public void restoreStreams() {
        System.setOut(originalOut);
    }
	
	private String readFileIntoString(String filename) {
		StringBuilder sb = new StringBuilder();
		try {
			Scanner scan = new Scanner(new File(filename));
			while (scan.hasNextLine())
				sb.append(scan.nextLine());
		}catch(Exception ignore){}
		return sb.toString();
	}
	
	//Test elevator works in a normal use case
	@Test
	public void general() {
		int numberOfFloors = 7;
		int numberOfElevators = 2;
		File file = new File("src/Model/InputFile-GeneralTest.txt");

		Main.startSimulation(numberOfFloors, numberOfElevators, file);
		
		assertEquals(outContent.toString(), readFileIntoString("src/Model/ExpectedOutput-GeneralTest.txt"));
	}
	
	/*Test elevator services more distant floors when there are constant 
	requests closer to the elevators.
	
	In this case the elevators are busy with many people traveling between the 
	bottom few floors when a request is made on floor 6 for an elevator. We want 
	to make sure that an elevator visits floor 6 while receiving requests closer to it.
	 */
	@Test
	public void distantRequest() {
		int numberOfFloors = 7;
		int numberOfElevators = 2;
		File file = new File("src/Model/InputFile-DistantRequestTest.txt");

		Main.startSimulation(numberOfFloors, numberOfElevators, file);
		
		assertEquals(outContent.toString(), readFileIntoString("src/Model/ExpectedOutput-DistantRequestTest.txt"));
	}
	
	/*Tests to ensure that elevators balance the passenger load even when the requests 
	 * are closer to a full elevator
	 * 
	 */
	@Test
	public void distributedPansengers() {
		int numberOfFloors = 7;
		int numberOfElevators = 3;
		File file = new File("src/Model/InputFile-DistrubutedPassengersTest.txt");

		Main.startSimulation(numberOfFloors, numberOfElevators, file);	
		
		assertEquals(outContent.toString(), readFileIntoString("src/Model/ExpectedOutput-DistrubutedPassengersTest.txt"));
	}
	
	/*
	 * Tests elevator errors from iteration4 via their error codes
	 */
	@Test
	public void elevatorErrors() {
		int numberOfFloors = 7;
		int numberOfElevators = 3;
		File file = new File("src/Model/InputFile-ElevatorErrorsTest.txt");

		Main.startSimulation(numberOfFloors, numberOfElevators, file);	
		
		assertEquals(outContent.toString(), readFileIntoString("src/Model/ExpectedOutput-ElevatorErrorsTest.txt"));
	}
}
