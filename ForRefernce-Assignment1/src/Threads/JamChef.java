/**
 * 
 */
package Threads;

/**
 * Needs peanutbutter and bread to be available on the table to make a sandwich
 * 
 * @author madelynkrasnay
 *
 */
public class JamChef extends Chef {

	public JamChef(Table table1) {
		super();
		table = table1;
		
	}

	@Override
	/**
	 * Waits for jam and bread, then requires a lock on the table and 'makes a sandwich',
	 * and sets the ingredients it used back to unavailable. Then notifies all.
	 * 
	 * @author madelynkrasnay
	 *
	 */
	public void makeSandwich() {
		
		synchronized(table){
			
			while(!(Table.peanutButter && Table.bread)) {
				try{
					table.wait();
				}
				catch(Exception e){	
				}
			}
			
			table.takePeanutbutter();
			table.takeBread();
			System.out.println("JamChef is making a sandwich.");
			table.notifyAll();
			
		}

	}


}
