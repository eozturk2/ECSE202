/*
 * Bounce.c
 *
 *  Created on: Nov 12, 2019
 *      Author: erenozturk
 */

#include <stdio.h>
#include <stdlib.h>

#define PI 3.14159265
#define RAD 0.0174532925	//quick deg>rad conversion

int main(void) {
	double g = 9.8;
	double x,y,v,theta,n,r;	//variables to be scanned from terminal

	printf("x-coordinate(m): ");
	scanf("%lA",&x);
	printf("y-coordinate(m): ");
	scanf("%lA",&y);
	printf("Launch speed(m/s): ");
	scanf("%lA",&v);
	printf("Launch angle(deg): ");
	scanf("%lA",&theta);
	printf("Energy decay coefficient(0.0-1.0): ");
	scanf("%lA",&n);
	printf("Radius of ball(m): ");
	scanf("%lA",&r);

	double vt,vx,vy,KEx0,KEy0;	//variables dependent on input
	vt = g/(4*PI*r*r*0.0016);	//terminal velocity
	vx = v*cos(theta*RAD);
	vy = v*sin(theta*RAD);
	KEx0 = 0.5*vx*vx;			//initial kinetic energy in x-direction
	KEy0 = 0.5*vy*vy;			//initial kinetic energy in y-direction

	double x1,y1,vx1,vy1,t,tforw,total,KEx,KEy;	//variables used by the simulation
	x1 = x;										//current x, initialised to initial x
	y1 = y;										//   ""
	vx1 = vx;									//   ""
	vy1 = vy;									//   ""
	t = 0;										//actual t used in calculations
	tforw = 0.1;								//used to approximate velocity
	total = 0;									//total time elapsed
	KEx = KEx0;
	KEy = KEy0;

	while(1){
		printf("t: %.2f X: %.2f Y: %.2f Vx: %.2f Vy:%.2f\n",total,x1,y1 ,vx1,vy1);
		x1 = ((vx*vt)/g)*(1-exp(-g*t/vt))+x;									//motion
		y1 = (vt/g)*(vy+vt)*(1-exp(-g*t/vt))-vt*t+y;							//eq. s
		vx1 = ((((vx*vt)/g)*(1-exp(-g*tforw/vt))+x)-x1)/0.1;					//with
		vy1 = (((vt/g)*(vy+vt)*(1-exp(-g*tforw/vt))-(vt*tforw)+y)-(y1))/0.1;	//drag
		t+=0.1;
		tforw+=0.1;
		total+=0.1;
		if ((((vt/g)*(vy+vt)*(1-exp(-g*t/vt))-vt*t+y)<=r)&(vy1<0)){	//collision detection
			KEx = 0.5*vx1*vx1;
			KEy = 0.5*vy1*vy1;
			x = x1;
			vx = sqrt((1-n)*(KEx*2)); //recalculate kinetic energy
			vy = sqrt((1-n)*(KEy*2));
			y = r;
			t = 0;			//reset time that is
			tforw=0.1;		//used for calculations
		}
		if (KEx<=0.005*KEx0||KEy<=0.005*KEy0) {	//breaks if kinetic energy falls
			break;								//beneath a certain fraction of its
	}											//original value
}
}
