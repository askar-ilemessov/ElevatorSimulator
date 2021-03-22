package Controller;
//@author Danish Butt
//This class tests the scheduler class

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import View.Elevator;
import View.Floors;


class SchedulerTest {
	
	private ArrayList<Elevator> elevator;
	private Floors floors;
	private ArrayList<ArrayList<Integer>>  schedules = new ArrayList<ArrayList<Integer>> ();
	private Scheduler scheduler;
	
	//Intialize values
	//Intialize values
		public SchedulerTest() {
			
			
			elevator = new ArrayList<Elevator>();
			floors = new Floors(7);
			
			for(int i=0; i<3; i++) {
				schedules.add(new ArrayList<Integer>());
			}
			
			for(int i=0; i< 3; i++) {
				elevator.add(new Elevator(7, schedules.get(i), i));
			}
			
			scheduler = new Scheduler (elevator, floors, schedules, "3000");
		}
	 


		//This method tests elevatorLocationUpdated
		@Test
		public void testElevatorLocationUpdated() {
			//Set new location of elevator 1 to floor 5
			scheduler.elevatorLocationUpdated(1, 5);
			//Check that the elevator location is 5
			assertEquals(5, floors.getElevatorFloorIndicator(1));
		}
		
		//This method tests elevatorDirectionUpdated
				@Test
				public void testElevatorDirectionUpdated() {
					//Set new direction for elevator(up=true)
					scheduler.elevatorDirectionUpdated(1, true);
					//Check that new direction is up (true)
					assertEquals(true, floors.getElevatorDirectionIndicator(1));
					
					//Set new direction for elevator(down=false)
					scheduler.elevatorDirectionUpdated(1, false);
					//Check that new direction is down (false)
					assertEquals(false, floors.getElevatorDirectionIndicator(1));
					
				}
		
}