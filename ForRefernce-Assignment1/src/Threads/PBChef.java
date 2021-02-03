package Threads;

/**
 * Needs jam and bread to be available on the table to make a sandwich.
 * 
 * @author madelynkrasnay
 *
 */
public class PBChef extends Chef {
	
	public PBChef(Table table1) {
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
			
			while(!(Table.jam && Table.bread)) {
				try{
					table.wait();
				}
				catch(Exception e){	
				}
			}
			
			table.takeJam();
			table.takeBread();
			System.out.println("PBChef is making a sandwich.");
			table.notifyAll();
			
		}

	}

}
