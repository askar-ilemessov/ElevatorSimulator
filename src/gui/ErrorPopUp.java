package gui;

import javax.swing.JOptionPane;
/**
 * 
 * @author Ifiok
 * Creates a popUp GUI for error 
 */
public class ErrorPopUp implements Runnable
{
	private String infoMessagein;
	private String titleBarin;
	public ErrorPopUp(String infoMessage) {
		infoMessagein = infoMessage;
		titleBarin = "Error!!!";
		
	}

    public void infoBox()
    {
        JOptionPane.showMessageDialog(null, infoMessagein, "" + titleBarin, JOptionPane.INFORMATION_MESSAGE);
    }

	@Override
	public void run() {
		infoBox();
		
	}
}
