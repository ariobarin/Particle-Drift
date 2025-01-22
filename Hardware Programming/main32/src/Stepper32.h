/* Stepper32.h
 * Kevin Dang
 * Header file for Stepper32 class
 */


#include <Arduino.h>

#ifndef Stepper32_H
#define Stepper32_H

class Stepper32{

    public:

        Stepper32(int dirPin, int stepPin, int stepsPerRevolution);
        void turnAngle(int angle);
        void turnSteps(int steps);
        void setDelayPerStep(int microseconds);
        int getSteps();
        double getAngle();
        void setSpeed(int rpm);
        //public methods for turning a certain angle, turning a certain number of steps, setting delay per step, getting steps, getting angle, and setting speed
    private:
        
        int dirPin;
        int stepPin;
        int stepsPerRevolution;
        float anglePerStep;
        int delayPerStep; //microseconds to wait betweens steps to make it run
        // int delay; //milliseconds delay to make it move smoothly and at a certain speed
        int currSteps;

};



#endif