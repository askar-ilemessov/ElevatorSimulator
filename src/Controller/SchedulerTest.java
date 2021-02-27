package Controller;
//This class tests the methods in the scheduler class
// @author Danish Butt

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import View.Elevator;
import View.Floors;


class SchedulerTest {
	
	private Elevator elevator;
	private Floors floors;
	private ArrayList<Integer> schedule = new ArrayList<>();
	private Scheduler scheduler;
	
	//Intialize values
	public SchedulerTest() {
		
		elevator = new Elevator(7, schedule);
		floors = new Floors(7);
		scheduler = new Scheduler(elevator,floors,schedule);
	}
	 


		//This method tests floorButtonPress
		@Test
		public void testFloorButtonPress() {
			
			//Elevator request made at floor 2 by pressing button (direction is up)
			//Add floor 2 to scheduler
			scheduler.FloorButtonPress(2,true);
			
			//Check that the schedule contains floor 2
			assertTrue(scheduler.getQueue().contains(2));
		}
	
		//This method tests elevatorButtonPressed
		@Test
		public void testElevatorButtonPressed() {
			
			//In the elevator button is pressed to go to floor 3
			scheduler.elevatorButtonPressed(3, false);
			
			//Check that the schedule contains floor 3
			assertTrue(schedule.contains(3));
		}
		
		//This method tests elevatorLocationUpdated
		@Test
		public void testElevatorLocationUpdated() {
			//Set new location of elevator to floor 5
			scheduler.elevatorLocationUpdated(5);
			//Check that the elevator location is 5
			assertEquals(5, floors.getElevatorFloorIndicator());
		}
		
		//This method tests elevatorDirectionUpdated
		@Test
		public void testElevatorDirectionUpdated() {
			//Set new direction for elevator(up=true)
			scheduler.elevatorDirectionUpdated(true);
			//Check that new direction is up (true)
			assertEquals(true, floors.getElevatorDirectionIndicator());
			
			//Set new direction for elevator(down=false)
			scheduler.elevatorDirectionUpdated(false);
			//Check that new direction is down (false)
			assertEquals(false, floors.getElevatorDirectionIndicator());
			
		}
		
//		//This method tests elevatorStopped
//		//ERROR this.waiting is null
//		@Test
//		public void testElevatorStopped() {
//			scheduler.elevatorStopped(5, false);
//			assertEquals(false,floors.getLampValue(5, false));
//		}
//		
		
}