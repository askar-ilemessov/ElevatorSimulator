package View;

import java.util.TimerTask;

public class Error1  extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("The elevator took too long to reach the floor");
		System.out.println("Check if the elevator is stuck or if the arrival sensor has failed");
	}

	
}
