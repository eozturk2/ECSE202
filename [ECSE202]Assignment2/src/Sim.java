import acm.graphics.GOval;

public class Sim {
	public double xScr(double x) {
		return 6*x;
	}
	public double yScr(double y) {
		return 600-y*6;
	}
	public double xSim(double x) {
		return x/6;
	}
	public double ySim(double y) {
		return (600-y)/6;
	}

}
