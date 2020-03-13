import java.awt.Color;
import acm.graphics.*;
import acm.program.*;


/**
 * The aBall class creates and a ball for the bSim class to run in parallel threads. The parameters required in this
 * class, and the getBall() method are mostly taken from the Assignment 2 handout, courtesy of Prof. Frank P. Ferrie.
 * 
 * @author erenozturk
 */

public class aBall extends Thread {
	private double xi;
	private double yi;
	private double v0;
	private double theta;
	private double bSize;
	private Color bColor;
	private double bLoss;
	private GOval myBall;
	private boolean running=true;
	private bSim link;
	
	private boolean TEST = false;
	private boolean ONCE = false;
	
	
	
	/**
	 * The run() method of this class runs simultaneously for its every instance
	 * created by the bSim class. It manipulates the GOval created in the constructor
	 * with equations for motion with air drag.
	 * <p>
	 * After taking its data from the parameters of the class, this method simulates the
	 * drag-influenced projectile motion of a ball with said parameters. If it detects a collision
	 * with the floor, it corrects the ball position to be on the floor, recalculates the kinetic
	 * energy using a class-defined coefficient loss, resets the instance variables to
	 * reflect the new position and launch speed and restarts the simulation.
	 * <p>
	 * The simulation ends when the kinetic energy in either x or y direction is reduced to a small
	 * fraction of its initial value. The simulation also has two different variables for time,
	 * one of which is used for the actual calculations and the other giving the total flight time
	 * for testing purposes.
	 * <p>
	 * For Assignment 4, three new features controlled by three getter methods (getHalt(), getTrac()
	 * and getStop()) were added:
	 * <p>
	 * If the "halt" instance variable of bSim is set to true, the main simulation loop is broken out of,
	 * effectively stopping the simulation for the aBall.
	 * <p>
	 * If the "Trace" JToggleButton is toggled on, and if the halt signal wasn't sent in the middle of the
	 * simulation loop, the code to add trace GOvals is executed.
	 * <p>
	 * While the "Stop" JToggleButton is toggled on, the thread executes infinite 0-millisecond pauses until 
	 * the button is toggled off. This is a workaround to the fact that while loops are problematic in ActionEvents.
	 * <p>
	 *  
	 * Instance variables used by the run() method:
	 * 
	 * g: gravitational acceleration
	 * vx: initially equal to the horizontal component of 
	 * the initial throwing velocity, recalculated with KEx at every bounce
	 * vy: initially equal to the vertical component of 
	 * the initial throwing velocity, recalculated with KEy at every bounce
	 * vt: terminal velocity of a ball with radius bSize with a
	 * drag coefficient of 0.0001 under g
	 * x1: initially xi, changes with every bounce to be the x-coordinate
	 * of landing
	 * y1: initially yi, changes with every bounce to be 2*bSize
	 * t: simulation time to be used by the main kinematics equations,
	 * reset every bounce
	 * tforw: used to calculate the velocity of the ball, by dx/dt
	 * where dx = x(tforw)-x(t) and dt = tforw-t
	 * vx1: dx/dt --> x(tforw)-x(t)/(tforw-t)
	 * vy1: dy/dt --> y(tforw)-y(t)/(tforw-t)
	 * KEx0: initial kinetic energy of the ball in the x-direction,
	 * calculated with the horizontal component of the initial speed
	 * KEy0: initial kinetic energy of the ball in the y-direction,
	 * calculated with the vertical component of the initial speed
	 * KEx: initially equal to KEx0, recalculated at every bounce 
	 * using the loss coefficient.
	 * KEy: initially equal to KEy0, recalculated at every bounce 
	 * using the loss coefficient.
	 * floor: Used for bounce detection
	 */
	public void run() {
		double g = 9.81;
		double vx = v0*Math.cos(Math.toRadians(theta));
		double vy = v0*Math.sin(Math.toRadians(theta));
		double vt = g/(4*Math.PI*bSize*bSize*0.0001);
		double y1 = yi;
		double x1 = xi;
		double vx1 = vx;
		double vy1 = vy;
		double t = 0;
		double tforw = 0.1;
		double total = 0;
		double KEx0 = 0.5*vx*vx;
		double KEy0 = 0.5*vy*vy;
		double KEy = KEy0;
		double KEx = KEx0;
		double floor = 0;

		while(true) {
			if (link.getHalt()) break;
			if (TEST)
				System.out.printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f\n",total,x1,y1 ,vx1,vy1);
			x1 = ((vx*vt)/g)*(1-Math.exp(-g*t/vt))+xi;
			y1 = (vt/g)*(vy+vt)*(1-Math.exp(-g*t/vt))-vt*t+yi;
			vx1 = ((((vx*vt)/g)*(1-Math.exp(-g*tforw/vt))+xi)-x1)/0.1;
			vy1 = (((vt/g)*(vy+vt)*(1-Math.exp(-g*tforw/vt))-vt*tforw+yi)-(y1))/0.1;
			myBall.setLocation(xScr(x1),yScr(y1));
			if (link.getTrac()&&(link.getHalt()==false)) {
			GOval trace = new GOval(xScr(x1+bSize),yScr(y1-bSize),1,1);
			trace.setColor(bColor);
			link.add(trace);}
			t+=0.1;
			tforw+=0.1;
			total+=0.1;
			while (link.getStop()) {
				try {
					Thread.sleep(0);
					} catch (InterruptedException e) {
					e.printStackTrace(); };
				}
			try {
				// pause for 50 milliseconds
				Thread.sleep(50);
				} catch (InterruptedException e) {
				e.printStackTrace(); };
			if ((((vt/g)*(vy+vt)*(1-Math.exp(-g*t/vt))-vt*t+yi)<=2*bSize)&(vy1<0)) {
				if (link.getHalt()) break;
				myBall.setLocation(xScr(x1),(yScr(floor)-xScr(2*bSize)));
				if (ONCE) break;
				KEx = 0.5*vx1*vx1;
				KEy = 0.5*vy1*vy1;
				xi = x1;
				if (vx1>=0) {
				vx = Math.sqrt((1-bLoss)*(KEx*2));}else{vx = -Math.sqrt((1-bLoss)*(KEx*2));}
				vy = Math.sqrt((1-bLoss)*(KEy*2));
				yi = 2*bSize;
				t = 0;
				tforw = 0.1;
				total += 0.1;
				if (KEx <= 0.001*KEx0||KEy <= 0.001*KEy0) {
					if (link.getHalt()) break;
					myBall.setLocation(xScr(x1),(yScr(floor)-xScr(2*bSize)));
					if(TEST) {
					System.out.print("Set the final location to x: " + xScr(x1) + " "+"y: "+(yScr(bSize))+"\n" );
					System.out.print("Breaking out of program now."+"\n");}
			
					break;
				
			}
			
			}
			}this.running = false;
		
	}
	/**
	 * The constructor of this class constructs an aBall with the given parameters, plus a GOval in 
	 * order to link the simulation to the actual screen coordinates. The parameters in this constructor and
	 * the GOval constructor within it are mostly taken from the Assignment 2 handout, courtesy of Prof. Frank P. Ferrie. 
	 * @param running 
	 * 
	 * @param xi: initial x-position
	 * @param yi: initial y-position
	 * @param v0: initial velocity
	 * @param theta: throw angle
	 * @param bSize: radius of ball
	 * @param bColor: color of ball
	 * @param bLoss: coefficient of kinetic energy loss with each bounce
	 * @param myBall: GOval to send to bSim to add to screen
	 * @param link: Link to the bSim class, required for tracing functionality
	 */

	public aBall(double xi, double yi, double v0, double theta, double bSize, Color bColor, double bLoss, bSim link) {
		this.xi = xi;
		this.yi = yi;
		this.v0 = v0;
		this.theta = theta;
		this.bSize = bSize;
		this.bColor = bColor;
		this.bLoss = bLoss;
		this.link = link;
		
		myBall = new GOval(xScr(xi),yScr(yi)+2*xScr(bSize),2*xScr(bSize),2*xScr(bSize));
		myBall.setFilled(true);
		myBall.setFillColor(bColor);
		
    }
	/**
	 * This method returns the GOval myBall created by the constructor. This is so that the GOval can be
	 * accessed outside of the class.
	 * 
	 * @return GOVal myBall
	 */
	public GOval getBall() {
		return myBall;
	}
	public boolean isRunning() {
		return this.running;
	}

	public double getBallSize() {
		return bSize;
	}
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
		return bSim.getScale()*x;
	}
	public double yScr(double y) {
		return bSim.getHeigh()-y*bSim.getScale();
	}
	public double xSim(double x) {
		return x/bSim.getScale();
	}
	public double ySim(double y) {
		return (bSim.getHeigh()-y)/bSim.getScale();
	}	

	/**
	 * This method simplifies the moving operation for the sortBall method in the bTree class.
	 * @param x
	 * @param y
	 */
	public void moveTo(double x, double y) {
		myBall.setLocation(xScr(x),yScr(y));
		link.add(myBall);
	}

}
