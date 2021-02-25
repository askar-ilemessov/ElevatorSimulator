/**
 * 
 */
package View;

/**
 * This is a type to store each instruction in the input file to save on the casting of types. 
 * 
 * Each instruction is comprised of 
 * - An long representing the milliseconds since the start of the program this instruction will run
 * - A int representing the floor the simulated person arrives at
 * - A boolean representing the direction of the button they pressed (1=Up, 0=down)
 * - A int represent the floor they'd like to end up at
 * 
 * @author madelynkrasnay
 *
 */
public class SimulatedArrival {
	private final long time;
	private final int originFloor;
	private final boolean direction;
	private final int destinationFloor;
	
	public SimulatedArrival(long time, int originFloor, boolean direction, int destinationFloor) {
		this.time = time;
		this.originFloor = originFloor;
		this.direction = direction;
		this.destinationFloor = destinationFloor;
	}
	
	public long getTime() {
		return time;
	}

	public int getOriginFloor() {
		return originFloor;
	}
	public boolean isDirection() {
		return direction;
	}
	public int getDestinationFloor() {
		return destinationFloor;
	}

	@Override
	public String toString() {
		return "SimulatedArrival [time=" + time + ", originFloor=" + originFloor + ", direction=" + direction
				+ ", destinationFloor=" + destinationFloor + "]";
	}

}
