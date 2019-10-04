
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

import cc.arduino.Arduino;
import processing.core.PApplet;
import processing.serial.*;

// This class will put into a .csv file the x, y, and z of the gyro sensor. 
public class Main extends PApplet{
	
	String path = "c:\\Users\\Alex\\Documents\\Processing\\";
	
	FileWriter gyroFileWriter; 
	BufferedWriter writeGyroFile;

	FileWriter stretchFileWriter; 
	BufferedWriter writeStretchFile;
	
	Serial port;
	Arduino arduino;
	
	boolean needsTowrite;
	boolean trigger;
	
	static final int START_WRITING_KEY = 65;
	static final int STOP_WRITING_KEY = 83;
	
	static final int TRIGGER = 16;
	
	int documentNumber = 0;
	String fileName;
	
	//The frame rate (How many times per second does it writes s
	float writeRate = 1;
	

	public static void main(String[] args) {
		PApplet.main("Main");
	}
	
	public void settings() {
		size(500,500);
		fileName = JOptionPane.showInputDialog(null, "File name, please.");
		
		
		port = new Serial(this, Serial.list()[6], 9600);
		//arduino = new Arduino(this, Arduino.list()[0], 9600);
	}
	
	public void draw() {
		String sth = port.readString();
		if(sth != null) {
			System.out.println(sth);
			if(needsTowrite) write(sth);
			}
		//System.out.println(Serial.list());
	}

	public void keyPressed() {
		
	     	     
		 switch(keyCode) {
		 
		 case START_WRITING_KEY:
			 needsTowrite = true;
			 setUpWrittingStuff();
			 break;
		 case STOP_WRITING_KEY:
			 needsTowrite = false;
			 finishWritting();
			 break;
		 case TRIGGER:
			 trigger = true;
			 text("TRIGGER", 100,100, 200,200);		 
			 break;
		 default:
			 break;
		 
		 }
		 text(keyCode+"", 20,20, 100,100);
		 
		System.out.println(keyCode);
	}

	public void keyReleased() {
		
		if(keyCode == TRIGGER) {
			trigger = false;
			text("False", 50,50, 100,100);		
		}
		
	}
	
	void setUpWrittingStuff() {
		//GyroFile
		ellipse(5,5,5,5);
		try {
			gyroFileWriter = new FileWriter(path + fileName + "Gyro" + documentNumber + ".csv");
			writeGyroFile = new BufferedWriter(gyroFileWriter);
			//Writes the Unix time stamp
			writeGyroFile.write("Timestamp: ,"+(int) (System.currentTimeMillis()/1000));
			writeGyroFile.newLine();
			writeGyroFile.write("rate: ," + writeRate);
			writeGyroFile.newLine();
			writeGyroFile.write("x,y,z");
			writeGyroFile.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Stretch file
		try {
			stretchFileWriter = new FileWriter(path + fileName + "Stretch" + documentNumber + ".csv");
			writeStretchFile = new BufferedWriter(stretchFileWriter);
			//Writes the Unix time stamp
			writeStretchFile.write("Timestamp: ,"+(int) (System.currentTimeMillis()/1000));
			writeStretchFile.newLine();
			writeStretchFile.write("rate: ," + writeRate);
			writeStretchFile.newLine();
			writeStretchFile.write("value");
			writeStretchFile.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		documentNumber++;
	}
	void finishWritting() {
		try {
			writeGyroFile.close();
			writeStretchFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	void write(String values) {
		
		String gyroValues = "";
		String stretchValues = "";
		
		//Check if it has finished writing the gyro values into the gyroValues string
		boolean gyroEnded = false;
		
		for(char c : values.toCharArray()) {
			if( c != '*') {
				
				if(!gyroEnded) stretchValues += c;
				//else gyroValues += c;
				
			}else {
				gyroEnded = true;
			}
		}
		
		gyroValues += "," + trigger;
		stretchValues += "," +trigger;
		
		try {
			writeGyroFile.write(gyroValues);
			writeGyroFile.newLine();
			writeStretchFile.write(stretchValues);
			writeStretchFile.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
