/*
 * Title: Snowplow Program
 * Author: Andy Su
 * Date completed: March 30,2016
 * Description: This program first takes inputs from users to determine its row size and column size.
 *              The program then displays 2 dimensional array based on the input the user entered. The program will randomly assign
 *              1's and 2's. 1 will be represented in red, and 2 will be represented in blue. When the start button is clicked, the
 *              program will look across the first row to find the first 1 value. If there isn't one on the first row, then it will
 *              output "no snow to be plowed." Otherwise, the program turns the first 1 found to 0, stores its index, and checks other
 *              values in other rows and columns by using recursion. It stops changing when blocked by 2's on all sides or the edges
 *              of array. The array is dynamically displayed as the numbers are being changed. The user may change the size or shuffle
 *              the values as well.
 */

//import necessary java libraries
import java.util.*;
import java.util.Timer;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/* class starts */
public class snowplow extends JFrame implements ActionListener {
	JFrame frame = new JFrame("Andy's SnowPlow Program"); // declare JFrame and
															// set its name
	Scanner input = new Scanner(System.in); // user Scanner to get user input
	JLabel[][] labels; // declare JLabels as 2 dimensional array
	int INITIAL_ROW; // declare the row variable
	int INITIAL_COL;// declare the column variable

	JPanel commandPanel = new JPanel(); // declare commandPanel to put buttons
	JPanel snowplowPanel = new JPanel(); // declare snowplowPanel to put labels
											// with numbers

	Random rand = new Random(); // declare random number generator
	JButton start = new JButton("start"); // declare a start button
	JButton shuffle = new JButton("shuffle"); // declare a shuffle button
	JButton resize = new JButton("resize"); // declare a resize button

	boolean canPlow = false; // set canPlow initially to false
	javax.swing.Timer t; // declare a java swing timer

	/* constructor starts */
	public snowplow() {

		initializer(); // call the initialier method to initialize the row and column

		frame.setSize(640, 480); // set the size of frame
		frame.setLayout(new GridLayout(1, 0)); // set the layout of frame to grid layout
		frame.setLocationRelativeTo(null); // set the location of frame so it
											// always appears in the center of
											// screen

		labels = new JLabel[INITIAL_ROW][INITIAL_COL]; // declare labels array
		commandPanel.setLayout(new GridBagLayout()); // set the commandPanel to gridbag layout
		snowplowPanel.setLayout(new GridLayout(INITIAL_ROW, INITIAL_COL)); // set the snowPanel to grid layout using the entered row value and column value
																			

		/*
		 * starts generating random numbers and assign these numbers to the 2
		 * dimensional array
		 */
		for (int r = 0; r < INITIAL_ROW; r++) { // the outer for loop controls the row
			for (int c = 0; c < INITIAL_COL; c++) { // the inner for loop controls the column
				labels[r][c] = new JLabel(Integer.toString(rand.nextInt(2) + 1)); // use random number generator to generate an integer either 1 or 2, and assign the number to label
																					
				if (labels[r][c].getText().equals("1")) { // if the number generated is 1
					labels[r][c].setForeground(Color.RED); // set the color to red
					labels[r][c].setFont(new Font("Arial", Font.PLAIN, 15)); // set the desired font and font size
																				
				} else { // if the value is not 1 (which is 2)
					labels[r][c].setForeground(Color.blue); // set the color to blue
					labels[r][c].setFont(new Font("Arial", Font.PLAIN, 15)); // set the desired font and font size
																			
				}

				snowplowPanel.add(labels[r][c]); // add the number to the snowplowPanel
			}
		}
		/* ends assigning numbers */
		

		commandPanel.add(start); // add the start button to commandPanel
		commandPanel.add(shuffle); // add the shuffle button to commandPanel
		commandPanel.add(resize); // add the resize button to commandPanel

		frame.add(snowplowPanel); // add the snowplowPanel to frame
		frame.add(commandPanel); // add the commandPanel to frame

		start.addActionListener(this); // add actionlistener to start button
		shuffle.addActionListener(this); // add actionlistener to shuffle button
		resize.addActionListener(this); // add actionlistener to resize button

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // set default
																// close
																// operation to
																// EXIT_ON_CLOSE

		frame.setResizable(false); // make the frame not resizable
		frame.setVisible(true); // make the frame visible

	}
	/* constructor ends */

	/* main method */
	public static void main(String[] args) {
		snowplow s = new snowplow();

	}
	/* main method ends */

	/*
	 * methods below are necessary to do the required task
	 */
	// this method is called to initialize the value of row and column
	private void initializer() {
		System.out.println("enter row, maximum 30"); // prompt the user to enter value for row
		INITIAL_ROW = input.nextInt(); // get user input and store it to INITIAL_ROW
		System.out.println("enter col, maximum 30"); // prompt the user to enter value for column
		INITIAL_COL = input.nextInt(); // get user input and store it to INITIAL_COL
		if (INITIAL_ROW > 30 || INITIAL_ROW < 0 || INITIAL_COL > 30 || INITIAL_COL < 0) { // if any of the input is not within the range of 0~20
																				
			System.out.println("enter the right row and column"); // prompt the user to enter the correct input
																
			initializer(); // call the method again
		}
	}
	
	
	 // this method gets the user action and mouse action
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand(); //use command to get user action

		if (command.equals("start")) { //if command equals start, which means the start button is pressed
			start.setEnabled(false); //set the start button to false to prevent from being clicked again
			shuffle.setEnabled(false); //set the shuffle button to false to prevent from being clicked in the middle of swapping
			resize.setEnabled(false); //set the resize button to false to prevent from being clicked in the middle of swapping
			frame.setTitle("Andy's SnowPlow Program - running"); //rename the title to inform user that the swapping is happening
			
			snowplowing(0, check()); // run the snowplowing method, 0 means row
										// 0, and check() method returns the
										// column where first 1 is found
		}
		
		if (command.equals("shuffle")) { //if command equals shuffle, which means the shuffle button is clicked
			start.setEnabled(true); //set the start button to true so user can start snowplowing
			shuffle(); //call shuffle method to shuffle the numbers
			canPlow = false; //set canPlow to false to tell program that it cannot be plowed yet
		}

		if (command.equals("resize")) { //if command equals resize, which means resize button is clicked
			DeleteOriginal(); //delete the original labels to allow new labels being entered
			frame.setVisible(false); //set the frame not visible
			initializer(); //call the initializer method to allow user enter new value for row and column
			ResizeMethod(); //call the resize method to resize the frame
		}

	}

	//this method is used to resize the frame
	private void ResizeMethod() {
		labels = new JLabel[INITIAL_ROW][INITIAL_COL]; //creates new labels using the new row value and column value
	
		snowplowPanel.setLayout(new GridLayout(INITIAL_ROW, INITIAL_COL)); //set the new layout in accordance with the new row value and column value
		
		/*use nested for loop to assign random numbers to 2 dimensional array*/
		for (int r = 0; r < INITIAL_ROW; r++) { //outer for loop controls row
			for (int c = 0; c < INITIAL_COL; c++) { //innner for loop controls column
				labels[r][c] = new JLabel(Integer.toString(rand.nextInt(2) + 1)); //use random number generator to create and assign random integers to labels
				if (labels[r][c].getText().equals("1")) { //if the number generated is 1
					labels[r][c].setForeground(Color.RED); //set the color to red
					labels[r][c].setFont(new Font("Arial", Font.PLAIN, 15)); //set the desired font and font size
				} else { //if the number is not 1
					labels[r][c].setForeground(Color.blue); //set the color to blue
					labels[r][c].setFont(new Font("Arial", Font.PLAIN, 15)); //set the desired font and font size 
				}

				snowplowPanel.add(labels[r][c]); //add the labels to snowplowPanel
			}
		}
		/* ends assigning number*/

		/*reset all buttons, enable them*/
		start.setEnabled(true); 
		shuffle.setEnabled(true); 
		resize.setEnabled(true);
		/*ends resetting all buttons*/

		frame.setVisible(true); //set the frame visible 

		canPlow = false; //set canPlow to false to tell program that the snowplow cannot be plowed yet
	}

	//this method deletes the original labels
	private void DeleteOriginal() {
		
		for (int r = 0; r < INITIAL_ROW; r++) { //outer for loop controls row
			for (int c = 0; c < INITIAL_COL; c++) { //inner for loop controls column
				snowplowPanel.remove(labels[r][c]); //remove all labels from panel
			}
		}
	}
	
	//this method shuffles the labels
	private void shuffle() {

		for (int r = 0; r < INITIAL_ROW; r++) { //outer for loop controls row
			for (int c = 0; c < INITIAL_COL; c++) { //inner for loop controls column
				labels[r][c].setText(Integer.toString(rand.nextInt(2) + 1)); //use random number generator to create random integers and assign to labels
				if (labels[r][c].getText().equals("1")) { //if number generated is 1
					labels[r][c].setForeground(Color.RED); //set the color to red
					labels[r][c].setFont(new Font("Arial", Font.PLAIN, 15));//set the desired font and font size
				} else { //if the number is not 1
					labels[r][c].setForeground(Color.blue); //set the color to blue
					labels[r][c].setFont(new Font("Arial", Font.PLAIN, 15)); //set the desired font and font size
				}
			}
		}
	}

	//this method is the most important one. It uses recursion to plow the snow
	private void snowplowing(final int row, final int col) {

		ActionListener task = new ActionListener() { //create an actionlistener called task. It is later used for timer purposes
			public void actionPerformed(ActionEvent e) { 
				if (canPlow) { //if the snow can be plowed
					
					//the nested for loop should check for all 8 directions surrounding the number
					//so row and column should both start from the current-1 , and they both end at current+1
					for (int start_row = row - 1; start_row <= row + 1; start_row++) { //outer for loop controls row
						for (int start_col = col - 1; start_col <= col + 1; start_col++) { //inner for loop controls column 
							if (start_row >= 0 && start_col >= 0 && start_row < INITIAL_ROW
									&& start_col < INITIAL_COL) { //if it is within the boundary of frame and it is greater than 0

								if (labels[start_row][start_col].getText().equals("1")) { //if the text is 1
									labels[start_row][start_col].setText("0"); //set the text to 0
									display(); //call the display method to display the labels
									snowplowing(start_row, start_col); //recursively call this method until all the 1's that should be changed are changed to 0

								} else { //if the text is not 1 , meaning there is no more 1's that need to be changed
									if (!t.isRunning()) { //if the timer is not running, meaning this code finishes running
										shuffle.setEnabled(true); //enable the shuffle button
										resize.setEnabled(true);//enable the resize button
										frame.setTitle("Andy's SnowPlow Program"); //reset the frame title to tell user that the program has finished swapping
									}

								}

							}
						}

					}

				} else { //if the snow cannot be plowed, meaning there is no 1 found in the first row
					System.out.println("no snow to be plowed"); //prompt the user that there is no snow to be plowed
					shuffle.setEnabled(true); //enable the shuffle button so user can shuffle
					resize.setEnabled(true); //enable the resize button so user can resize

				}
			}

		};
		t = new javax.swing.Timer(100, task); //set the timer to 100 miliseconds (0.1 second) so it delays 100 miliseconds before swapping to show as the 1's are being changed to 0
		t.setRepeats(false); //do not let the timer repeat
		t.start(); //start the timer
		shuffle.setEnabled(false); //when the timer is still running, disable the shuffle button
		resize.setEnabled(false); //when the timer is still running, disable the resize button
		frame.setTitle("Andy's SnowPlow Program - running");// change the frame title to tell user that the program is still running
	}

	//this method checks if there is a 1 in the first row
	private int check() {
		int col; //declare a variable called col
		for (col = 0; col < INITIAL_COL; col++) { //for loop runs every value in the first column
			if (labels[0][col].getText().equals("1")) { //if there is a 1 found in the first column
				canPlow = true; // indicates that there is a 1 in the first row,
								// and therefore it can be plowed
				labels[0][col].setText("0"); //set the 1 to 0
				display(); //display the labels
				break; //break
			}
		}

		return col; //return the index of the first 1 found

	}

	// method to display the snow plow
	private void display() {
		//use nested loop to display 2 dimensional array
		for (int r = 0; r < INITIAL_ROW; r++) { //outer loop controls row
			for (int c = 0; c < INITIAL_COL; c++) { //inner loop controls column

				if (labels[r][c].getText().equals("1")) { //if the label found is 1
					labels[r][c].setForeground(Color.RED); //set the color to red
				} else if (labels[r][c].getText().equals("2")) { //if the label found is 2
					labels[r][c].setForeground(Color.blue); //set the color to blue

				} else if (labels[r][c].getText().equals("0")) { //if the label found is 0
					labels[r][c].setForeground(Color.black); //set the color to black
				}

				labels[r][c].setText(labels[r][c].getText()); //set the text according to its own value
			}
		}
	}

}

/* class ends */