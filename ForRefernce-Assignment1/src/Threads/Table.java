/**
 * 
 */
package Threads;

/**
 * Table is the common domain that stores the ingredients the threads can act on. It also has the methods that 
 * act on them, stock an to set it to true and take an ingredient to set it to false.
 * 
 * @author madelynkrasnay
 *
 */
public class Table {
	
		//potential ingredients to be put on the table
		//(true represents in stock/available)
		static Boolean peanutButter;
		static Boolean jam;
		static Boolean bread;

		//construct a table, set all ingredients to unavailable
		public Table() {
			peanutButter = false;
			jam = false;
			bread = false;
		}
		
		//stock ingredient (make true)--------------------------------------------------------------------------------- 
		public void stockPeanutButter() {
			
			if (peanutButter == false) {
				peanutButter = true;
			}
			else {
				System.err.println("Tryed to stock peanut butter when peanut butter was already in stock.");
			}

		}
		
		public void stockJam() {
			
			if (jam == false) {
				jam = true;
			}
			else {
				System.err.println("Tryed to stock jam when jam was already in stock.");
			}		
		}
		
		public void stockBread() {
			
			if (bread == false) {
				bread = true;
			}
			else {
				System.err.println("Tryed to stock bread when bread was already in stock.");
			}
		}
		
		//take Ingredient (make false)--------------------------------------------------------------------------------- 
		public void takePeanutbutter() {
			
			if (peanutButter == true) {
				peanutButter = false;
			}
			else {
				System.err.println("Tryed to take peanut butter but it was out of stock.");
			}
			
		}
		
		public void takeJam() {
			
			if (jam == true) {
				jam = false;
			}
			else {
				System.err.println("Tryed to take jam but it was out of stock.");
			}
			
		}

		public void takeBread() {
		
			if (bread == true) {
				bread = false;
			}
			else {
				System.err.println("Tryed to take bread but it was out of stock.");
			}
		
		}

}
