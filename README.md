# ElevatorSimulator
README:
April 14, 2021
Sysc 3303 Iteration 5
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

## Execution Instructions
Run the JAR file "ElevatorSimulator.jar" from the command line with "java -jar ElevatorSimulator.jar. This JAR reads the input file at "src/Model/InputFile.txt" so this file can be edited to test different cases.

## Install XChart Java library
The GUI uses an external dependency called chart. The JAR for this is included in the submission, but can also be downloaded [here](https://knowm.org/open-source/xchart/xchart-change-log/)

### Add Xchart Jar to Eclipse Workspace:
While the dependency is included in the packaged JAR for our project, running our code in eclipse will require you add the chart JAR to your classpath.
- In open eclipse project, click on **project** located at the top on the tool bar 
- From the presented dropdown menu click on **properties**
- From the displayed window, locate and click on **Java Build Path**
- Select the **Libraries** tab and select **classpath** and click **Add External JARs**
- From the displaled window, navigate to the folder containing the Xchart JAR, select it and click open
- Select **Apply and Close**

## Class Descriptions:

- src/Controller/Main.java
	Contains main() which creates an instance of elevator, floor and schedule, and corresponding thread classes 
	and starts them.

- src/Controller/Scheduler.java 	
	Acts as the system server and manages interactions between the elevator and floors.
	
- src/View/Elevator.java 	
	Keeps the state of the elevator, buttons, lamps. Handle's its own movement. Sends this information to Scheduler. 

- src/View/Floors.java 
	Instantiates floor in separate thread. Keeps the state of floor and its attributes (floor buttons, lamps). Sends this	
	information Scheduler.

- src/View/SimulatedArrival.java
	This is a type to store each instruction in the input file to save on the casting of types. And prints them.

- src/View/InputFileReader.java
	Reads data from InputFile.txt, properly stores it and sends it to scheduler.

- src/Controller/SchedulerTest.java
	Tests for Scheduler class functions	

- src/View/ElevatorTest.java
	Tests for Elevator class functions

- src/View/FloorsTest.java
	Tests for Elevator class functions

- src/Model/InputFile.txt
	contains input data in the format a time stamp, white space, an integer representing the floor on which the passenger 
	is making a request, white space, a string consisting of either ???Up??? or ???Down???, more white space, then an integer 
	representing floor button  within  the  elevator  which  is  providing  service  to  the  passenger.

- src/assignment3Package/Client.java
	Provides communication between subsystems over UDP

- src/gui/SimpleRealTime.java
	Displays graph on GUI showing position of elevators in system

- src/gui/ErrorPopUp
	Displays pop up  GUI containing information on detected errors

Error codes:
- 31-system failure
- 32- door sensor error
- 33- floor timer errorSYSC 3303
