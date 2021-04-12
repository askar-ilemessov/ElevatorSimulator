package gui;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

/** Creates a simple real-time chart */
public class GuiTest {

  public static void main(String[] args) throws Exception {
	  int init = 0;
    double[][] initdata = getSineData(0);

    // Create Chart
    final XYChart chart =
        QuickChart.getChart(
            "Simple XChart Real-time Demo", "Radians", "Sine", "sine", initdata[0], initdata[1]);

    // Show it
    final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
    sw.displayChart();

    while (true) {
    	init++;
      Thread.sleep(1000);

      final double[][] data = getSineData(init);

      chart.updateXYSeries("sine", data[0], data[1], null);
      sw.repaintChart();
    }
  }

private static double[][] getSineData(int start) {

    double[] xData = new double[10];
    double[] yData = new double[10];
    for (int i = 0; i < 5; i++) {
      xData[i] = start + i;
      yData[i] = start + i;
    }
    return new double[][] {xData, yData};
  }
}
