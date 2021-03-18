package assignment3Package;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String method;
	public String[] arguements;
	
	public Message(String method, String[] arguements) {
		this.method = method;
		this.arguements = arguements;
	}
	public Message() {
		
	}
	public byte[] toByteArray() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			oos.writeObject(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	
	public Message toMessage(byte[] byteArray) {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
	    ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try {
			return (Message) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
	
	public void print() {
		System.out.println("Method: " + this.method);
		int i = 0;
		for(String a : this.arguements){
			System.out.println("Arguement " + i + " : " + a);
			i++;
		}
	}
	
	
}
