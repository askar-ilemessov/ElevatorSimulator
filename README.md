# ElevatorSimulator
README:
February 27, 2021
Sysc 3303 Iteration 4
Professor Franks

List of Team Members and Responsibilities:
Testing- Madelyn
Error handling/inputs/errors- Danish, Ifiok, Yasin, Askar
UML-Yasin
ElevatorSimulator program:

The Floor subsystem and the Elevators are the clients in the system; the Scheduler is the server.  
Each line of input is to be sent to the Scheduler. The elevators will then make calls to the Scheduler which will then reply when there is work to be done.
The Elevator will then send the data back to the Scheduler who will then send it back to the Floor.   
The Scheduler is only being used as a communication channel from the Floor thread to the Elevator thread and back again.   
 
Class Descriptions:

src/Controller/Main.java
	Contains main() which creates an instance of elevator, floor and schedule, and corresponding thread classes 
	and starts them.

src/Controller/Scheduler.java 	
	Acts as the system server and manages interactions between the elevator and floors. 	
src/View/Elevator.java 	
	Keeps the state of the elevator, buttons, lamps. Handle's its own movement. Sends this information to Scheduler. 
src/View/Floors.java 
	Instantiates floor in separate thread. Keeps the state of floor and its attributes (floor buttons, lamps). Sends this	
	information Scheduler.
src/View/SimulatedArrival.java
	This is a type to store each instruction in the input file to save on the casting of types. And prints them.

src/View/InputFileReader.java
	Reads data from InputFile.txt, properly stores it and sends it to scheduler.

src/Controller/SchedulerTest.java
	Tests for Scheduler class functions	
src/View/ElevatorTest.java
	Tests for Elevator class functions
src/View/FloorsTest.java
	Tests for Elevator class functions
src/Model/InputFile.txt
	contains input data in the format a time stamp, white space, an integer representing the floor on which the passenger 
	is making a request, white space, a string consisting of either “Up” or “Down”, more white space, then an integer 
	representing floor button  within  the  elevator  which  is  providing  service  to  the  passenger.
Error codes:
31-system failure
32- door sensor error
33- floor timer errorSYSC 3303
