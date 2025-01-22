/* Stepper32.cpp
 * Kevin Dang
 * Controls stepper, turns stepper motor by angle or steps, keeps track of steps
 */ 


#include "Stepper32.h"


Stepper32::Stepper32(int dirPin, int stepPin, int stepsPerRevolution){
	this->dirPin = dirPin; //dirPin controls direction (high for clockwise, low for counterclockwise)
	this->stepPin = stepPin; //stepPin controls the step
	this->stepsPerRevolution = stepsPerRevolution;
	this->anglePerStep = 360.0 / stepsPerRevolution;
	this->delayPerStep = 1000;
	this->currSteps = 0;
	//initialize variables
	pinMode(dirPin, OUTPUT);
	pinMode(stepPin, OUTPUT);

}

void Stepper32::turnAngle(int angle){
	
	int numSteps = round(abs(angle)/anglePerStep);
	delay(1);

	//find nearest number of steps to turn
	if (angle > 0){ //if angle is positive, turn on dirPin
		digitalWrite(dirPin, HIGH);
		currSteps += numSteps;
	}
	else{
		digitalWrite(dirPin, LOW); //if angle is negative, turn counterclockwise
		currSteps -= numSteps;
	}//update currSteps

	currSteps = currSteps % stepsPerRevolution;

	for(int x = 0; x < numSteps; x++)
	{//turn the motor
		digitalWrite(stepPin, HIGH);
		delayMicroseconds(delayPerStep);
		digitalWrite(stepPin, LOW);
		delayMicroseconds(delayPerStep);
	}

}

void Stepper32::turnSteps(int steps){

	int numSteps = abs(steps);
	
	if (steps > 0){
		digitalWrite(dirPin, HIGH);
		currSteps += numSteps;
		
	}
	else{
		digitalWrite(dirPin, LOW);
		currSteps -= numSteps;
	}
	// delay(1);
	
	currSteps = currSteps % stepsPerRevolution; //keep value within 0 and stepsPerRevolution

	//same logic as turnAngle
	for(int x = 0; x < numSteps; x++){

		digitalWrite(stepPin, HIGH);
		delayMicroseconds(delayPerStep);
		digitalWrite(stepPin, LOW);
		delayMicroseconds(delayPerStep);
		
	}
}



int Stepper32::getSteps(){
	return currSteps;

}

double Stepper32::getAngle(){
	return currSteps*anglePerStep;
} //getter methods

void Stepper32::setDelayPerStep(int microseconds){
	delayPerStep = microseconds; //set delay per step
}

void Stepper32::setSpeed(int rpm) {
    delayPerStep = 60L * 1000L * 1000L / (rpm * stepsPerRevolution);
} //set speed in rpm

