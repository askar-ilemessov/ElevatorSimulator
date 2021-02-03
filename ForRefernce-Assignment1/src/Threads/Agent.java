package Threads;

import java.util.Random;


/**
 * An Agent makes two ingredients from the table class and notifies everyone. It then waits to
 *  be notified when those ingredients are gone. It repeats this loop 20 times.
 * 
 * 
 * @author madelynkrasnay
 *
 */

public class Agent extends Thread{
	
	protected static Table table;
	
	/**
	 * @author madelynkrasnay
	 *
	 */
	public Agent(Table table1) {
		table = table1;
		
	}
	
	private static Random rand = new Random();
	
	/**
	 * @author madelynkrasnay
	 *
	 */
	public void makeTwoIngredientsAvalable() {
		
		synchronized(table){
		
			switch(rand.nextInt(3)){
				case 0:
					//put peanut butter and jam on the table
					table.stockPeanutButter();
					table.stockJam();
					table.notifyAll();
					System.out.println("Agent put peanut butter and jam on the table.");
					break;
				case 1:
					//put peanut butter and bread on table
					table.stockPeanutButter();
					table.stockBread();
					table.notifyAll();
					System.out.println("Agent put peanut butter and bread on the table.");
					break;
				case 2:
					//put jam and bread on table
					table.stockJam();
					table.stockBread();
					table.notifyAll();
					System.out.println("Agent put jam and bread on the table.");
					break;
				default:
					//error
					System.err.println("Agent failed to put ingredents on the table.");
			}
			
			//wait to somebody does something to the table
			try {
				table.wait();
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	/**
	 * @author madelynkrasnay
	 *
	 */
	public void run() {
		for(int i=20; i>0; i--) {
			this.makeTwoIngredientsAvalable();
		}
	}
	
}
