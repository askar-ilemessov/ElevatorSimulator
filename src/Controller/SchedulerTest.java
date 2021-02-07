package Controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Queue;

import org.junit.jupiter.api.Test;


import View.Elevator;
import View.Floors;


class SchedulerTest {
	
	private Elevator elevator;
	private Floors floors;
	private Queue <Integer> schedule;
	private Scheduler scheduler = new Scheduler(elevator,floors,schedule);


		//This method tests elevatorArrived
		@Test
		public void testFloorButtonPress() {
			scheduler.FloorButtonPress(2,true);
			assertTrue(scheduler.getQueue().contains(2));
		}
	
		//This method tests elevatorButtonPressed
		@Test
		public void testElevatorButtonPressed() {
			scheduler.elevatorButtonPressed(3);
			assertTrue(schedule.contains(3));
		}
		
		//This method tests elevatorLocationUpdated
		@Test
		public void testElevatorLocationUpdated() {
			scheduler.elevatorLocationUpdated(5);
			assertEquals(5, floors.getElevatorFloorIndicator());
		}
		
		//This method tests elevatorDirectionUpdated
		@Test
		public void testElevatorDirectionUpdated() {
			scheduler.elevatorDirectionUpdated(true);
			assertEquals(true, floors.getElevatorDirectionIndicator());
		}
		
		//This method tests elevatorStopped
		@Test
		public void testElevatorStopped() {
			scheduler.elevatorStopped(5, false);
			assertEquals(false,floors.getLampValue(5, false));
		}
		
		
}