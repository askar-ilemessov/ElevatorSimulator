package View;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ElevatorTest {

	//Create elevator object
	private Elevator elevator = new Elevator(7);
	
	//This method tests locationUpdate
		@Test
		public void testlocationUpdate() {
			elevator.locationUpdate(3);
			assertEquals(3, elevator.getCurrentFloor());
		}
		
	//This method tests stopped
		@Test
		public void testStopped() {
			elevator.stopped(2);
			assertEquals(0,elevator.getMotor());
		}
}
