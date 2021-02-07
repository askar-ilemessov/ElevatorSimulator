package View;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test class for Floors
 * 
 */

import org.junit.jupiter.api.Test;

class FloorsTest {

	//Create Floors object
		private Floors floor = new Floors(7);

		//This method tests elevatorArrived
		@Test
		public void testElevatorArrived() {
			floor.elevatorArrived(5, false);
			assertEquals(false,floor.getLampValue(5, false));
		}

		//This method tests elevatorLocationUpdated
		@Test
		public void testElevatorLocationUpdated() {
			floor.elevatorLocationUpdated(5);
			assertEquals(5,floor.getElevatorFloorIndicator());
		}
				
		
		//This method tests elevatorDirectionUpdated
		@Test
		public void testElevatorDirectionUpdated() {
			floor.elevatorDirectionUpdated(true);
			assertEquals(true,floor.getElevatorDirectionIndicator());
		}
			
		
}