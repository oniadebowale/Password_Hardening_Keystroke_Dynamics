import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This project focuses on creating a password - hardened system based on keystroke dynamics.
 * Simply run this file (Main.java) to exectue the program. You will be prompted to enter your 
 * password a couple times in order to gather data about your personal keystroke typing speed 
 * and pattern. Then, the program will allow you mimic "logging in" using this password hardened
 * system. The biometric data will be displayed in order to let you see what your typing speeds between
 * characters were, and the time range that determined if you passed or failed.
 * 
 * Note: As of right now, the system works fine if directions are followed precisely, but this is 
 * still a work in progress. I stil need to account for user errors, and I plan to continue refining
 * and adding features to this program in order to make it more efficient and accurate.
 * 
 * @author David_Ju
 * @date April 17, 2013
 */

public class Main {
	
	/** Area in which the system outputs messages to. */
	static JTextArea textArea = new JTextArea();
	/** Field in which the user enters his or her input. */
	static JTextField textField = new JTextField();
	/** Boolean switch to show if the ENTER key has been pressed or not. */
	static boolean enterPress = false;
	/** Boolean switch to show if there is at least one uppercase character in the user's password. */
	static boolean uppercase = false;
	/** Boolean switch to show if there is at least one number in the user's password. */
	static boolean number = false;
	/** Boolean switch to control how the system responds when the ENTER key is pressed. */
	static boolean analyze = false;
	/** Boolean switch to show if the current character the user is entering is the first or not. */
	static boolean firstPress = false;
	/** Boolean switch to control when the system starts parsing the next word from the user. */
	static boolean nextWord = false;
	/** Boolean switch to control how the system responds when the ENTER key is pressed. */
	static boolean repeatTest = false;
	/** ArrayList containing the elapsed times between characters when the user is entering his 
	 * or her password for data collection. */
	static ArrayList<Long> keyDiff = new ArrayList<Long>();
	/** 2D array that contains all the elapsed timing data gathered from the data collection phase. */
	static Long[][] keyTimes;
	/** Array used to store miscalleneous, temporary timing data. */
	static Long[] tempArray;
	/** Array used to store the averaged elapsed times between characters when the user is entering
	 * his or her password, caclulated from the data gathered in the data collection phase. */
	static Long[] keyTimesFinal;
	/** Array used to store the standard deviation of the average elapsed times between characters 
	 * when the user is entering his or her password. */
	static Long[] stdDevArray;
	/** Variable used to mark when time capture begins. */
	static Long start = (long) 0.0;
	/** Variable used to mark when time capture ends. */
	static Long end = (long) 0.0;
	/** Variable used to store calculated standard deviation values. */
	static Long stdDev = (long) 0.0;
	/** Variable used to store the user's password. */
	static String password = "";
	/** Variable used to store miscellaneous, temproary String data. */
	static String temp = "";
	/** Variable that represents the number of times the user will enter his password during the
	 * data collection phase to gather biometric keystroke data. */
	static int sampleSize;
	
	/** Constructor. */
	public Main() {

		/** Creating a new JFrame and configuring it. */
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setTitle("Password Hardening Based on Keystroke Dynamics");
		frame.setSize(700, 500);
		frame.setLocation(new Point(100, 200));

		/** Initializing a new JTextArea and configuring it. */
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		
		/** Initializing a new JTextField and configuring it. */
		textField = new JTextField();
		textField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				/** What ENTER does normally. */
				if (!analyze && e.getKeyCode() == KeyEvent.VK_ENTER) {
					/** Isolates the special event for ENTER when determining 
					 * if the user wants to test his or her password again. */
					if (!repeatTest) {
						password = textField.getText();
					}
					enterPress = true;
				/** What ENTER does during the data collection phase. */
				} else if (analyze && e.getKeyCode() == KeyEvent.VK_ENTER) {
					end = System.nanoTime();	
					firstPress = false;
					nextWord = true;
					temp = textField.getText();
					textField.setText("");
					/** What a key press excluding ENTER, SHIFT, and BACKSPACE 
					 * does during the data collection phase. */	
				} else if (analyze && e.getKeyCode() != KeyEvent.VK_ENTER 
						&& e.getKeyCode() != KeyEvent.VK_SHIFT
						&& e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
					if (!firstPress) {
						firstPress = true;
						start = System.nanoTime();
					} else {
						end = System.nanoTime();
						keyDiff.add(end - start);
						start = System.nanoTime();
					}
				/** What BACKSPACE does during the data collection phase. */
				} else if (analyze && e.getKeyCode() == KeyEvent.VK_BACK_SPACE 
						&& !keyDiff.isEmpty()) {
					keyDiff.remove(keyDiff.size() - 1);
					if (keyDiff.isEmpty()) {
						firstPress = false;
					}
					start = System.nanoTime();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				/** Not needed in this program. */
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				/** Not needed in this program. */
			}
		});
		
		frame.add(textArea, BorderLayout.CENTER);
		frame.add(textField, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	/** Executable. */
	public static void main (String[] args) throws NumberFormatException, IOException {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main();
			}
		});
		
		configure();
		collectData();
		analyzeData();
		startTest();
		
		while (continueTest()) {
			startTest();
		}
		System.exit(0);
	}
	
	/** Gets user input from the JTextField. */
	static String getInput() {
		String input = "";
		enterPress = false;
		while (true) {
			input = textField.getText();
			if (enterPress) {
				enterPress = false;
				break;
			}
		}
		textField.setText("");
		return input;
	}
	
	/** Gets the user's password and prepares the system for data collection. */
	static void configure() throws NumberFormatException, IOException {
		textArea.append("In order to analyze your keystroke, you will be required to " +
				"enter you password a certain amount of times to gather data.\n");
		delay(1000);
		textArea.append("You must enter you password at least five times.\n");
		delay(1500);
		textArea.append("Your password must be at least five characters long, and " +
				"must contain at least one uppercase letter and one number.\n");
		delay(1500);
		textArea.append("How many times would you like to enter your password? ");
		sampleSize = Integer.parseInt(getInput());
		
		while (sampleSize < 5) {
			textArea.append("You must enter your password at least 5 times.\n");
			delay(800);
			textArea.append("Please enter another number: ");
			sampleSize = Integer.parseInt(getInput());
		}

		textArea.append(Integer.toString(sampleSize));
		keyTimes = new Long[sampleSize][];
		textArea.append("\nPlease enter your password for reference: ");
		
		while(true) {
			password = getInput();
			if (password.length() > 5) {
				for (int i = 0; i < password.length(); i++) {
					if (Character.isUpperCase(password.charAt(i))) {
						uppercase = true;
					}
					if (Character.isDigit(password.charAt(i))) {
						number = true;
					}
				}
				if (uppercase == true && number == true) {
					break;
				} else if (uppercase == false) {
					textArea.append("\nYour password must contain at least one " +
							"uppercase character. Please enter your password again: ");
				} else {
					textArea.append("\nYour password must contain at least one " +
							"number. Please enter your password again: ");
				}
			} else {
				textArea.append("\nYour password must contain at least five " +
						"characters. Please enter your password again: ");
			}
		}
		
		textArea.append(password);
	}
	
	/** Collects keystroke dynamic - related data used to produce a unique and
	 * distinctive biometric value that is difficult for anyone but the user to 
	 * replicate. */
	static void collectData() {
		textArea.append("\nStarting data collection...");
		delay(500);
		for (int i = 0; i < sampleSize; i++) {
			textArea.append("\n" + (i + 1) + ". ");
			getKeystrokeTimes();

			if (!temp.equals(password)) {
				textArea.append(temp);
				textArea.append("\nThe password you just entered does not match the password " +
						"you provided. Please enter data piece number " + (i + 1) + " again: ");
				i = i - 1;
			} else {
				keyTimes[i] = tempArray.clone();
				textArea.append(temp);
			}
		}
		analyze = false;
		textArea.append("\nData collection complete.");
	}
	
	/** Method that calcuates the elapsed time betweeen keystrokes when the user
	 * is entering his or her password, and stores that data in an array. */
	static void getKeystrokeTimes() {
		analyze = true;
		
		while (!nextWord) {
			temp = textField.getText();
		}

		keyDiff.add(end - start);
		nextWord = false;
		tempArray = new Long[keyDiff.size()];
		
		for (int i = 0; i < keyDiff.size(); i++) {
			tempArray[i] = keyDiff.get(i);
		}
		
		keyDiff.clear();
		analyze = false;
	}
	
	/** Calculates the average elapsed time between keystrokes when the user 
	 * is entering his or her password during the data collection phase. */
	static void analyzeData() {
		textArea.append("\nAnalyzing data...");
		keyTimesFinal = new Long[keyTimes[0].length];
		Long total = (long) 0.0;
		
		for (int i = 0; i < keyTimes[0].length; i++) {
			for (int j = 0; j < keyTimes.length; j++) {
				total += keyTimes[j][i];
			}
			keyTimesFinal[i] = (total/keyTimes.length);
			total = (long) 0.0;		
		}
		
		DecimalFormat df = new DecimalFormat("#0.00");
		
		for (int k = 0; k < password.length(); k++) {
			if (k == password.length() - 1) {
				delay(800);
				textArea.append("\nTime between " + password.substring(k) + " and " +
						"the Enter key: " + df.format(keyTimesFinal[k]*Math.pow(10.0, -9.0)) + " seconds");
			} else {
				delay(800);
				textArea.append("\nTime between " + password.substring(k, k + 1) + " and " + 
						password.substring(k + 1, k + 2) + 
						": " + df.format(keyTimesFinal[k]*Math.pow(10.0, -9.0)) + " seconds");
			}
		}
		delay(500);
		calculateStdDev();
		textArea.append("\nData anaylsis complete");
		delay(500);
	}
	
	/** Calculates the standard deviation of the average elapsed time between
	 * keystrokes from the gathered data, and stores the standard deviation 
	 * values in a new array. */
	static void calculateStdDev() {
		Long diffTotal = (long) 0.0;
		stdDevArray = new Long[keyTimes[0].length];
		
		for (int i = 0; i < keyTimes[0].length; i++) {
			for (int j = 0; j < keyTimes.length; j++) {
				diffTotal += ((keyTimesFinal[i] - keyTimes[j][i])*(keyTimesFinal[i] - keyTimes[j][i]));
			}
			stdDevArray[i] = (long)Math.sqrt(diffTotal/keyTimes.length);
			diffTotal = (long) 0.0;		
		}
	}

	/** Checks if the keystroke timings from the user's input falls within the acceptable range of the
	 * average keystroke timing +/- standard deviation. */
	static void startTest() {
		int strikes = password.length() - (password.length() * 3 / 4);
		
		delay(1500);
		textArea.setText("");
		textArea.append("You needed at least " + (password.length()*3/4) + " characters in your"
				+ " password to match the recorded keystroke timings "
				+ "in order to pass.");
		textArea.append("\nNow enter your password to test it against this hardened password system: ");
		getKeystrokeTimes();
		textArea.append(temp);
		
		while (!temp.equals(password)) {
			textArea.append("\nYou have entered an incorrect password. Please try again: ");
			getKeystrokeTimes();
			textArea.append(temp);
		}
		
		textArea.append("\n");
		
		for (int i = 0; i < password.length(); i++) {
			textArea.append("\nRange needed: " + Math.pow(10.0, -9.0)*(keyTimesFinal[i] - stdDevArray[i]) +
					" - " + Math.pow(10.0, -9.0)*(keyTimesFinal[i] + stdDevArray[i]) + " secs. Your time: " + 
					Math.pow(10, -9) * tempArray[i] + " secs");
			if (tempArray[i]  <= (keyTimesFinal[i] + stdDevArray[i]) &&
					tempArray[i]  >= (keyTimesFinal[i] - stdDevArray[i])) {
				textArea.append(" Pass");
			} else {
				strikes--;
				textArea.append(" Fail");
			}
			delay(800);
		}
		
		if (strikes >= 0) {
			textArea.append("\n\nYou passed!\n");
		} else {
			textArea.append("\n\nYou failed.\n");
		}
	}
	
	/** Determines if the user wants to enter his password again and test the keystroke timing against 
	 * the average keystroke timing calculated from the collected data. */
	static boolean continueTest() {
		repeatTest = true;
		String inp = "";
		textArea.append("\nDo you want to try again (Y or N)? ");
		inp = getInput();
		textArea.append(inp);
		
		while (!inp.equalsIgnoreCase("Y") && !inp.equalsIgnoreCase("N")) {
			textArea.append("\nPlease enter Y or N: ");
			inp = getInput();
			textArea.append(inp);
		}
		
		repeatTest = false;
		
		if (inp.equalsIgnoreCase("Y")) {
			return true;
		}
		return false;
	}
	
	/**
	 * This method doesn't really do anything except for delay the system momentarily. This gives the
	 * user time to read messages from the system and makes it seem like the program is performing some
	 * tremendous complicated calculations.
	 * @param time The amount of time you want to delay the program.
	 */
	static void delay(int time) {
		try {
			Thread.sleep(time);
		} catch(Exception e) {}
	}
	
}