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
	boolean SINGLEBALLTEST = false;
	boolean SINGLEBALLTESTREV = false;
	
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	private static final int OFFSET = 200;
	private static final double SCALE = 4; 
	private static final int NUMBALLS = 100;
	private static final double MINSIZE = 1.0;
	private static final double MAXSIZE = 10.0;
	private static final double EMIN = 0.1;
	private static final double EMAX = 0.6;
	private static final double VoMIN = 40.0;
	private static final double VoMAX = 50.0;
	private static final double ThetaMIN = 80.0;
	private static final double ThetaMAX = 100.0;
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
		rgen.setSeed( (long) 0.12345 );
		this.resize(WIDTH,HEIGHT);
		GLine flr = new GLine(0,HEIGHT-OFFSET,WIDTH,HEIGHT-OFFSET);
		add(flr);
		if (SINGLEBALLTEST) {
			aBall ball = new aBall(0,1,40,85,1,Color.blue,0.4);
			add(ball.getBall());
			ball.start();}
		if (SINGLEBALLTESTREV) {
				aBall ball = new aBall(95,1,40,95,1,Color.blue,0.4);
				add(ball.getBall());
				ball.start();}
		if(SINGLEBALLTEST == false && SINGLEBALLTESTREV ==false) {
		for (int i=1;i<=NUMBALLS;i++) {
			double Size = rgen.nextDouble(MINSIZE,MAXSIZE);
			Color Couleur = rgen.nextColor();
			double iLoss = rgen.nextDouble(EMIN,EMAX);
			double V = rgen.nextDouble(VoMIN,VoMAX);
			double Theta = rgen.nextDouble(ThetaMIN,ThetaMAX);
			aBall ball = new aBall(xSim(WIDTH)/2,2*Size,V,Theta,Size,Couleur,iLoss);
			add(ball.getBall());
			ball.start();
		}
	}}
	/**
	 * These four methods help convert pixel coordinates used in the simulation to screen coordinates,
	 * and vice-versa if needed.
	 * 
	 * @param x
	 * @return xSim to xScr
	 * 
	 * @param y
	 * @return ySim to yScr
	 * 
	 * @param x
	 * @return xScr to xSim
	 * 
	 * @param y
	 * @return yScr to ySim
	 */
	public double xScr(double x) {
		return SCALE*x;
	}
	public double yScr(double y) {
		return HEIGHT-y*SCALE;
	}
	public double xSim(double x) {
		return x/SCALE;
	}
	public double ySim(double y) {
		return (HEIGHT-y)/SCALE;
	}	
}
