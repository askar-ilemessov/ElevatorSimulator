package Controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class IntegrationTests {
	
    private  ByteArrayOutputStream outContent;
    private  PrintStream originalOut;
    private InputStream originalIn;
    
	@Before
	public void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        originalIn = System.in;
	}
	
	@After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
	
	//Test elevator works in a normal use case
	@Test
	public void general() {
		
	}
	
	/*Test elevator services more distant floors when there are constant 
	requests closer to the elevators.
	
	In this case the elevators are busy with many people traveling between the 
	bottom few floors when a request is made on floor 6 for an elevator. We want 
	to make sure that an elevator visits floor 6 while receiving requests closer to it.
	 */
	@Test
	public void serviceingDistantFloorsTimley() {
			
	}
	
	/*Tests to ensure that elevators balance the passenger load even when the requests 
	 * are closer to a full elevator
	 * 
	 */
	@Test
	public void distributedPansengers() {
		
	}

}
