import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import acm.graphics.*;
import acm.gui.*;
import acm.program.GraphicsProgram;
import acm.util.*;

/**
 * This is the main class that runs the simulations, running instances of the aBall class
 * in parallel. The idea of parallel processing, the variables in the class, the random number generator
 * and its seed has been provided by the Assignment 2 handout, courtesy of Prof. Frank P. Ferrie.
 * <p>
 * The structure of this class has been completely revamped for Assignment 4, given the new interactivity
 * requirements. Most significantly, the run() method was encapsulated in a doSim() method, delegating its
 * main controller role to the init() and actionPerformed() methods. The previously static instance variables
 * concering simulation parameters were replaced with Int and Double fields.
 * <p>
 * Another design issue was the window size and scaling, with the parameter panel changing the simulation area and 
 * the total screen width. Given a choice between asserting a specific pixel value to the fields and JLabels, I 
 * opted instead to get its preferred value and calculating the midpoint in a new instance variable called 
 * midscreen. This way, the program is more flexible and can recalculate the middle point at different screen resolutions,
 * instead of relying on a specific resolution.
 * <p>
 * I also decluttered many of the methods carried over from previous assignments, by removing testing options that are no longer
 * used.
 * 
 * @author erenozturk
 *
 */
public class bSim extends GraphicsProgram {

	private static final int WIDTH = 1200;
	private static final int HEIGHT = 600;
	private static final int OFFSET = 200;
	private static final double SCALE = HEIGHT/100; 
	
	private boolean halt = false;
	private RandomGenerator rgen = new RandomGenerator();
	private bTree myTree = new bTree();
	private JToggleButton Trace = new JToggleButton("Trace");
	private JToggleButton Stop = new JToggleButton("Stop");
	private IntField ballnum = new IntField();
	private DoubleField smin = new DoubleField();
	private DoubleField smax = new DoubleField();
	private DoubleField Vmin = new DoubleField();
	private DoubleField Vmax = new DoubleField();
	private DoubleField thetmin = new DoubleField();
	private DoubleField thetmax = new DoubleField();
	private DoubleField losmin = new DoubleField();
	private DoubleField losmax = new DoubleField();
	double size = losmax.getPreferredSize().getWidth();
	double midscreen = ((WIDTH/SCALE)-(size/SCALE))/2;
	
	/**
	 * The init() method of the bSim class is mostly responsible for setting up the graphical
	 * interface at the beginning.
	 */
	
	public void init() {
		System.out.print(size);
		add(Trace,SOUTH);
		add(new JButton("Run"),NORTH);
		add(new JButton("Stack"),NORTH);
		add(new JButton("Clear"),NORTH);
		add(Stop,NORTH);
		add(new JButton("Quit"),NORTH);
		addActionListeners();
		add(new JLabel("SIMULATION PARAMETERS"),EAST);
		add(new JLabel(""),EAST);
		add(new JLabel("Number of balls: "),EAST);
		add(ballnum, EAST);
		add(new JLabel("Minimum Size: "),EAST);
		add(smin,EAST);
		add(new JLabel("Maximum Size: "),EAST);
		add(smax,EAST);
		add(new JLabel("Minimum Energy Loss Coefficient: "),EAST);
		add(losmin,EAST);
		add(new JLabel("Maximum Energy Loss Coefficient: "),EAST);
		add(losmax,EAST);
		add(new JLabel("Minimum Initial Velocity: "),EAST);
		add(Vmin,EAST);
		add(new JLabel("Maximum Initial Velocity: "),EAST);
		add(Vmax,EAST);
		add(new JLabel("Minimum Throwing Angle: "),EAST);
		add(thetmin,EAST);
		add(new JLabel("Maximum Throwing Angle: "),EAST);
		add(thetmax,EAST);
		this.resize((int) (WIDTH+(size*SCALE/2)),HEIGHT+OFFSET);
		GLine flr = new GLine(0,HEIGHT,WIDTH-size,HEIGHT);
		add(flr);
		
	}
	/**
	 * The actionPerformed() method is the main controller for the user actions:
	 * <p>
	 * "Run": On receiving the command "Run", the program clears the screen, adds back the floor, sets the "halt" variable
	 * to false, and finally executes the doSim() methods with the arguments provided by the Int and Double fields that are
	 * defined as instance variables.
	 * <p>
	 * "Stack": When the "Stack" command is received, the program halts the simulation by setting the "halt" variable to true,
	 * removes every GObject from the screen, resets the stack counters used in the doStack() method of the bTree class, and then 
	 * calls it. Finally, the program adds back the floor which was cleared.
	 * <p>
	 * "Clear": The program halts the simulation by setting the "halt" variable to true, removes every GObject from the screen, 
	 * but also deletes myTree by using the delTree() method of the bTree class. It ends by adding the floor.
	 * <p>
	 * "Quit": The program sends an exit system call, terminating the applet.
	 */
	
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.contentEquals("Run")) {
			GLine flr = new GLine(0,HEIGHT,WIDTH-size,HEIGHT);
			add(flr);
			setHalt(false);
			doSim(ballnum.getValue(),smin.getValue(),smax.getValue(),losmin.getValue(),losmax.getValue(),Vmin.getValue(),Vmax.getValue(),thetmin.getValue(),thetmax.getValue());
			
		}else if (action.contentEquals("Stack")) {
			setHalt(true);
			removeAll();
			myTree.stackReset();
			myTree.doStack();
			GLine flr = new GLine(0,HEIGHT,WIDTH-size,HEIGHT);
			add(flr);
		}else if (action.contentEquals("Clear")) {
			setHalt(true);
			removeAll();
			myTree.delTree();
			GLine flr = new GLine(0,HEIGHT,WIDTH-size,HEIGHT);
			add(flr);
		}else if (action.contentEquals("Quit")) {
			System.exit(0);
		}
		
	}
	/**
	 * Getter method for the JToggleButton "Trace"
	 * @return the state of the JToggleButton controlling trace functionality
	 */
	public boolean getTrac() {return Trace.isSelected();}
	
	/**
	 * Getter method for the JToggleButton "Stop", required for the aBall implementation
	 * of the stopping functionality.
	 * 
	 * @return the state of the JToggleButton controlling stopping functionality
	 */
	public boolean getStop() {return Stop.isSelected();}
	
	/**
	 * Setter method for the instance variable "halt", used to get around the 
	 * limitations of ActionEvents.
	 */
	private void setHalt(boolean a) {halt = a;}

	/**
	 * Getter method for the instance variable "halt", required for the aBall
	 * implementation of the breaking functionality.
	 * 
	 * @return the state of the instance variable "halt"
	 */
	
	public boolean getHalt() {return halt;}
	
	/**
	 * Getter method for the height of the window
	 * @return HEIGHT
	 */
	
	public static int getHeigh() {return HEIGHT;}
	
	/**
	 * Getter method for the simulation scale
	 * @return SCALE
	 */
	public static double getScale() {return SCALE;}
	
	/**
	 * The doSim() method is the encapsulation of the run() method used in the previous assignments. This allows for
	 * an action-driven program that can be run multiple times instead of simply using a set of hardwired parameters and
	 * a predetermined program flow.
	 * 
	 * @param nballs: Number of balls to generate
	 * @param mins: Minimum sized ball in the set of balls generated
	 * @param maxs: Maximum sized ball
	 * @param emin: Ball with the minimum kinetic energy loss coefficient
	 * @param emax: Ball with the maximum kinetic energy loss coefficient
	 * @param vmin: Ball with the lowest initial velocity
	 * @param vmax: Ball with the highest initial velocity
	 * @param thetamin: Ball with the narrowest throwing angle
	 * @param thetamax: Ball with the widest throwing angle
	 */
	
	public void doSim(int nballs, double mins, double maxs, double emin, double emax, double vmin, double vmax, double thetamin, double thetamax) {
		rgen.setSeed((long) 424242);
		for (int i=1;i<=nballs;i++) {
			double Size = rgen.nextDouble(mins,maxs);
			Color Couleur = rgen.nextColor();
			double iLoss = rgen.nextDouble(emin,emax);
			double V = rgen.nextDouble(vmin,vmax);
			double Theta = rgen.nextDouble(thetamin,thetamax);
			aBall ball = new aBall(midscreen-(Size/SCALE),2*Size,V,Theta,Size,Couleur,iLoss,this);
			myTree.addNode(ball);
			add(ball.getBall());
			ball.start();
		}
	}
}
