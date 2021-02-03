package Threads;

/**
 * 
 * 
 * 
 * @author madelynkrasnay
 *
 */
public abstract class Chef extends Thread{
	
	protected Table table;
	
	/**
	 * Should be implemented something like:
	 * 
	 * Waits for any necessary ingredients to all be on the table at once, then requires a lock on the table and 'makes a sandwich',
	 * and sets the ingredients it used back to unavailable. Then notifies all.
	 * 
	 * @author madelynkrasnay
	 *
	 */
	public abstract void makeSandwich();
	
	/**
	 * Perpetually attempts to make a sandwich as long as the thread is alive.
	 * 
	 * @author madelynkrasnay
	 *
	 */
	public void run() {
		while(true){
			this.makeSandwich();
		}
	}
	

}
