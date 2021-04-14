
//This class tests the methods in the floors class' local variable management.
//It's tests ensure that the states of elevator resources (such as lights) 
//respond as expected when the floor's state is changed. The fact that the floors 
//behave as expected in the system is ensured threw integration tests 
//(found in Controller/IntegrationTests). These integration tests also check the 
//state of the local variables, but unit tests allow for easier debugging in the 
//event an integration test fails.
//@author Danish Butt, Madelyn Krasnay


package View;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

class FloorsTest {

		//Create Floors object
		private Floors floor = new Floors(7, 2002);
		
		
		
		//This method tests setLamp
		@Test
		public void testSetLamp() {
			
			//At floor 4 set the direction to go down and state to on
			floor.setLamp(4,false, true);
			
			//Checks that in the lamps array at floor 4 and direction 
			//going down that state is set to on (true)
			assertEquals(true,floor.getLampValue(4,false));
			
			
			//At floor 3 set the direction to go up and state to off
			floor.setLamp(3,true, false);
			
			//Checks that in the lamps array at floor 3 and direction 
			//going up that state is set to off (false)
			assertEquals(false,floor.getLampValue(3,true));
		}
		
		//This method tests setElevatorFloorIndicator
		@Test
		public void testSetElevatorFloorIndicator() {
			
			//Set the floor indicator to floor 3
			floor.setElevatorFloorIndicator(3, 0);
			
			//Checks that floor indicator reads floor 3
			assertEquals(3,floor.getElevatorFloorIndicator(0));
		}
		
		//This method tests setElevatorDirectionIndicator
		@Test
		public void testSetElevatorDirectionIndicator() {
			
			//Set the elevator direction to up(true)
			floor.setElevatorDirectionIndicator(0, true);
			
			//Checks that elevator direction is up (true)
			assertEquals(true, floor.getElevatorDirectionIndicator(0));
			
			//Set the elevator direction to down (false)
			floor.setElevatorDirectionIndicator(0, false);
			
			//Checks that elevator direction is down (false)
			assertEquals(false, floor.getElevatorDirectionIndicator(0));
			
		}
			
		//This method tests elevatorLocationUpdated
		@Test
		public void testElevatorLocationUpdated() {
			//Update elevator location to floor 5
			floor.elevatorLocationUpdated(5, 0);
			//Checks that new location is 5
			assertEquals(5,floor.getElevatorFloorIndicator(0)); 
		}
				
		
		//This method tests elevatorDirectionUpdated
		@Test
		public void testElevatorDirectionUpdated() {
			//Update elevator direction to up
			floor.elevatorDirectionUpdated(0, true);
			//Checks that elevator direction has been changed to up
			assertEquals(true,floor.getElevatorDirectionIndicator(0));
		}
			
		
}