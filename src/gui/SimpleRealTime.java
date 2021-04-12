package gui;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import View.Elevator;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ifiok
 * Creates a simple real-time graph 
 */
public class SimpleRealTime implements Runnable{
	private ArrayList<Elevator> elev; //Array list of elevator objects to poll current location
	private ArrayList<double[][]> intdataAr = new ArrayList<double[][]>();
    private double[][] yaxes;
    private String[] series;
    private final XYChart chart;
    private List<XYChart> a = new ArrayList<XYChart>();
    
    public final SwingWrapper<XYChart> sw;
    
	public SimpleRealTime(ArrayList<Elevator> elevators) {
		elev = elevators;
		double[][] initHold = {{0},{0}};
		String[] shold = new String[elevators.size()];
		double[][] yhold = new double[elevators.size()][];
		int i = 0;
		for(Elevator e : elevators) {
			double[][] initdataHold = {{0},{0}};
			intdataAr.add(initdataHold);
			shold[i] = "Elevator" + e.getNumber();
			yhold[i] = initdataHold[1];
			i++;
		}
		yaxes = yhold;
		series = shold;
		chart = QuickChart.getChart("ElevatorSystem", "Time(s)", "Floor Level", series  , initHold[0], yaxes);
		a.add(chart);
		sw = new SwingWrapper<XYChart>(a);
	}
	
	//Display graph GUI
	public void display() {
		sw.displayChart();
	}
	
	//Update Array containing current and previous elevator positions every 1 second
	public void update() {
		while (true) {
	      try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      for(int i = 0; i<intdataAr.size(); i++ ) {
	    	  double position = (double)elev.get(i).getCurrentFloor();
	    	  double[][] data0 = getElevatorPostion(intdataAr.get(i), position);
	    	  intdataAr.set(i, data0);
	    	  chart.updateXYSeries(series[i], data0[0], data0[1], null);
//	    	  System.out.println(series[i] + " is at position " + position + printArD(data0));
	      }
	      
	      sw.repaintChart();
	    }
	}
	
	//Prints 2D array
	public String printAr(double[] arr) {
		String s = "{ ";
		for(int i = 0; i < arr.length; i++) {
			s += arr[i];
			s += ", ";
		}
		s+= " }";
		return s;
	}
	
	//Prints 1D array
	public String printArD(double[][] arr) {
		String s = " { ";
		for(int i = 0; i < arr.length; i++) {
			s += printAr(arr[i]);
			s += ", ";
		}
		s+= " }";
		return s;
	}
	
	//Get and store elevator positions
 	public double[][] getElevatorPostion(double[][] data, double elevatorPosition) {

	    double[] time;
	    double[] Floors;
	    //gotten positions less than 20
	    if(data[1].length < 20) {
	    	Floors = new double[data[1].length+1];
	    	time = new double[data[0].length+1];
	    	int i;
	    	for(i = 0; i < data[0].length; i++) {
	    		Floors[i] = data[1][i];
	    		time[i] = i;
	    	}
	    	Floors[i] = elevatorPosition;
	    	time[i] = i;
	    	
	    }else {
	    	Floors = new double[20];
	    	time = new double[20];

		    for(int i = 0; i < 19; i++) {
		    	Floors[i] = data[1][i+1];
		    	time[i] = data[0][i+1];
		    }
		    Floors[19] = elevatorPosition;
		    time[19] = data[0][19] + 1;
	    }
	    data[0] = time;
	    data[1] = Floors;
	    return data;
	  }

	@Override
	public void run() {
		update();
		
	}

}
