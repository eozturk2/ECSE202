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
public class bSimfluid extends GraphicsProgram {
	
	private static final Color COLOR = Color.blue;
	private static final int XPOSITION = 100;
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	private static final int OFFSET = 200;
	private static final double SIZE = 0.005;
	private static final double VoMIN = 35.0;
	private static final double VoMAX = 40.0;
	private static double ThetaMIN = 84.8;
	private static double ThetaMAX = 85.2;
	private static final double SWEEP = 5;
	RandomGenerator rgen = new RandomGenerator();
	
	/**
	 * After having set up the graphics environment and set the seed for the rgen, The run() method of bSim checks for two 
	 * things: whether the Single Ball Test instruction or the Reverse Single Ball Test instruction is received. 
	 * If so, the method will only generate and start the thread of the aBall with parameters specified in the Assignment 2 handout.
	 * <p>
	 * If both testing conditions are set to false, the method generates NUMBALL instances of the aBall class
	 * with random parameters in ranges specified by the Assignment 2 handout, then runs them in NUMBALLS simultaneous threads.
	 */
	public void run() {
		int t = 0;
		//rgen.setSeed( (long) 0.12345 );
		this.resize(WIDTH,HEIGHT);
		GLine flr = new GLine(0,HEIGHT-OFFSET,WIDTH,HEIGHT-OFFSET);
		add(flr);
		
		//if (t%100==0) {
		//	ThetaMIN+=SWEEP/100;
		//   ThetaMAX+=SWEEP/100;}
		
		while (true) {
			double V = rgen.nextDouble(VoMIN,VoMAX);
			double Theta = rgen.nextDouble(ThetaMIN,ThetaMAX);
			aBall ball = new aBall(XPOSITION,2*SIZE,V,Theta,1,COLOR,0.99);
			add(ball.getBall());
			ball.start();
			t+=50;
		}
	}
		}
	

