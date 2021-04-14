package View;
//This class tests the methods in the elevator class' local variable management.
//It's tests ensure that the states of elevator resources (such as lights and doors) 
//respond as expected when the elevator's state is changed. The fact that the elevator 
//behaves as expected in the system is ensured threw integration tests 
//(found in Controller/IntegrationTests). These integration tests also check the 
//state of the local variables, but unit tests allow for easier debugging in the 
//event an integration test fails.
//@author Danish Butt, Madelyn Krasnay

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class ElevatorTest {
	
	//Create Elevator object
	private ArrayList <Integer> schedule = new ArrayList<>();
	private Elevator elevator = new Elevator(7, schedule, 0, 0);
	
	//Setters
	//_______________________________________________________________
	
	//This methods tests setMotor
		@Test
		public void testSetMotor() {
			
			//Check stopped case
			elevator.setMotor(0);
			assertEquals(0,elevator.getMotor());
			
			//Check moving up case
			elevator.setMotor(1);
			assertEquals(1,elevator.getMotor());
			
			//Check moving down case
			elevator.setMotor(2);
			assertEquals(2,elevator.getMotor());
			
		}
		
		//This method tests setCurrentFloor
		@Test
		public void testSetCurrentFloor() {
			
			//Set current floor to 5
			elevator.setCurrentFloor(5);
			//Check if floor is 5
			elevator.getCurrentFloor();
		}
		
		//This method tests setDirection
		@Test
		public void testSetDirection() {
			
			//Check going up case (true)
			elevator.setDirection(true);
			assertEquals(true, elevator.getDirection());
			
			//Check going down case (false)
			elevator.setDirection(false);
			assertEquals(false, elevator.getDirection());
			
		}
		
		
		//This method tests setDesination
		@Test
		public void testSetDesination() {
			//Set destination to floor 5
			elevator.setDesination(5);
			//Check if destination is set to floor 5
			assertEquals(5, elevator.getDesination());
		}
		
	
		//This method tests locationUpdate
		@Test
		public void testlocationUpdate() {
			//Set new location (floor 3)
			elevator.locationUpdate(3);
			//Check if location is set to 3
			assertEquals(3, elevator.getCurrentFloor());
		}
		
		//More complex local state changing functions
		//_______________________________________________________________
		
		@Test
		public void testTravelToDestination() {
			elevator.setCurrentFloor(3);
			elevator.setDesination(5);
			elevator.travelToDestination();
			
			assertEquals(5, elevator.getCurrentFloor());
		}
	
}
