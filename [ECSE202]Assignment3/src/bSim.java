import java.awt.Color;
import acm.graphics.*;
import acm.program.GraphicsProgram;
import acm.util.*;

/**
 * This is the main class that runs the simulations, running instances of the aBall class
 * in parallel. The idea of parallel processing, the variables in the class, the random number generator
 * and its seed has been provided by the Assignment 2 handout, courtesy of Prof. Frank P. Ferrie.
 * 
 * @author erenozturk
 *
 */
public class bSim extends GraphicsProgram {
	private static boolean SINGLEBALLTEST = false;
	private static boolean SINGLEBALLTESTREV = false;
	private static boolean PAUSEDETECTIONTEST = false;
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 600;
	private static final int OFFSET = 200;
	private static final double SCALE = HEIGHT/100; 
	private static final int NUMBALLS = 60;
	private static final double MINSIZE = 1.0;
	private static final double MAXSIZE = 7.0;
	private static final double EMIN = 0.2;
	private static final double EMAX = 0.6;
	private static final double VoMIN = 40.0;
	private static final double VoMAX = 50.0;
	private static final double ThetaMIN = 80.0;
	private static final double ThetaMAX = 100.0;
	RandomGenerator rgen = new RandomGenerator();

	
	
	public void init() {addMouseListeners();}
	
	/**
	 * After having set up the graphics environment and set the seed for the rgen, The run() method of bSim checks for two 
	 * things: whether the Single Ball Test instruction or the Reverse Single Ball Test instruction is received. 
	 * If so, the method will only generate and start the thread of the aBall with parameters specified in the Assignment 2 handout.
	 * <p>
	 * If both testing conditions are set to false, the method generates NUMBALL instances of the aBall class
	 * with random parameters in ranges specified by the Assignment 2 handout, then runs them in NUMBALLS simultaneous threads.
	 * <p>
	 * As an extension of its functionalities in Assignment 2, this run() method adds aBalls to a binary tree, defined in the bTree class. Then,
	 * using the simIsRunning() method of the bTree class, the program checks continually if the simulation threads are still running, and prints
	 * the run status to console if PAUSEDETECTIONTEST is enabled. Then, it blocks execution until a click is received. Finally, it stacks the balls
	 * stored in the bTree in order of their size. 
	 */
	bTree myTree = new bTree();
	public void run() {
		
		rgen.setSeed( (long) 424242 );
		this.resize(WIDTH,HEIGHT+OFFSET);
		GLine flr = new GLine(0,HEIGHT,WIDTH,HEIGHT);
		add(flr);
		if (SINGLEBALLTEST) {
			aBall ball = new aBall(0,1,40,85,1,Color.blue,0.4);
			add(ball.getBall());
			ball.start();
			while (true) {
				System.out.print(ball.isRunning()+"\n");
			}}
		
		if (SINGLEBALLTESTREV) {
				aBall ball = new aBall(95,1,40,95,1,Color.blue,0.4);
				add(ball.getBall());
				ball.start();
				while (true) {
					System.out.print(ball.isRunning()+"\n");
				}}
		
		if(SINGLEBALLTEST == false && SINGLEBALLTESTREV ==false) {
		for (int i=1;i<=NUMBALLS;i++) {
			double Size = rgen.nextDouble(MINSIZE,MAXSIZE);
			Color Couleur = rgen.nextColor();
			double iLoss = rgen.nextDouble(EMIN,EMAX);
			double V = rgen.nextDouble(VoMIN,VoMAX);
			double Theta = rgen.nextDouble(ThetaMIN,ThetaMAX);
			aBall ball = new aBall((WIDTH/2)/SCALE,2*Size,V,Theta,Size,Couleur,iLoss);
			add(ball.getBall());
			ball.start();
			myTree.addNode(ball);
		}
		
		}
		
		while(true) {
			if (PAUSEDETECTIONTEST) System.out.print(myTree.simIsRunning()+"\n");
			if (myTree.simIsRunning()==false) break;
			}
		GLabel prompt = new GLabel("CR to continue",WIDTH-100,HEIGHT-OFFSET-200);
		add(prompt);
		waitForClick();
		myTree.sortBall();
		remove(prompt);
		GLabel prompt2 = new GLabel("All stacked",WIDTH-100,HEIGHT-OFFSET-200);
		add(prompt2);
		
		
	}
	public static int getHeigh() {return HEIGHT;}
	public static double getScale() {return SCALE;}

		
}
