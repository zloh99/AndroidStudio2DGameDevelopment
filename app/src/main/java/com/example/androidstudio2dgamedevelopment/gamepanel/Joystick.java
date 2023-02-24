package com.example.androidstudio2dgamedevelopment.gamepanel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.androidstudio2dgamedevelopment.Utils;

public class Joystick {

    private Paint innerCirclePaint;
    private Paint outerCirclePaint;
    private int innerCircleRadius;
    private int outerCircleRadius;
    private int outerCircleCenterPositionX;
    private int outerCircleCenterPositionY;
    private int innerCircleCenterPositionX;
    private int innerCircleCenterPositionY;
    private double joystickCenterToTouchDistance;
    private boolean isPressed;
    private double actuatorX;
    private double actuatorY;

    public Joystick(int centerPositionX, int centerPositionY, int outerCircleRadius, int innerCircleRadius) {

        //Outer and inner circle that comprise the thumbstick
        outerCircleCenterPositionX = centerPositionX;
        outerCircleCenterPositionY = centerPositionY;
        innerCircleCenterPositionX = centerPositionX;
        innerCircleCenterPositionY = centerPositionY;

        //Radii of each circle
        this.outerCircleRadius = outerCircleRadius;
        this.innerCircleRadius = innerCircleRadius;

        //paint object for the circles
        outerCirclePaint = new Paint(); //initialise a new paint object for the outer circle
        outerCirclePaint.setColor(Color.GRAY);
        outerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(Color.BLUE);
        innerCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

    }
    public void draw(Canvas canvas) {
        //Draw outer circle
        canvas.drawCircle(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                outerCircleRadius,
                outerCirclePaint
        );

        //Draw inner circle
        canvas.drawCircle(
                innerCircleCenterPositionX,
                innerCircleCenterPositionY,
                innerCircleRadius,
                innerCirclePaint
        );
    }

    public void update() {
        updateInnerCirclePosition();
    }

    private void updateInnerCirclePosition() {
        //draw a vector from outerCircleCenterPosition in the direction of the actuator to decide
        //what % of the outer circle we have moved
        innerCircleCenterPositionX = (int) (outerCircleCenterPositionX + actuatorX*outerCircleRadius);
        innerCircleCenterPositionY = (int) (outerCircleCenterPositionY + actuatorY *outerCircleRadius);
    }

    public boolean isPressed(double touchPositionX, double touchPositionY) {

        joystickCenterToTouchDistance = Utils.getDistanceBetweenPoints(
                outerCircleCenterPositionX,
                outerCircleCenterPositionY,
                touchPositionX,
                touchPositionY
        );
        //return a boolean based on whether a user has touched
        // anywhere inside the outer circle of the joystick
        return joystickCenterToTouchDistance < outerCircleRadius;
    }

    public void setIsPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }

    public boolean getIsPressed() {
        return isPressed; //returns the value (boolean) of isPressed from the setIsPressed method
    }

    public void setActuator(double touchPositionX, double touchPositionY) {
        //values of 0 to 1, 0: not pulling the joystick, 1: pulling as far as we can
        double deltaX = touchPositionX - outerCircleCenterPositionX;
        double deltaY = touchPositionY - outerCircleCenterPositionY;
        double deltaDistance = Utils.getDistanceBetweenPoints(0, 0, deltaX, deltaY);

        if(deltaDistance < outerCircleRadius){
            actuatorX = deltaX/outerCircleRadius; //how far we have pulled the joystick divided by distance from center of joystick to the border
            actuatorY = deltaY/outerCircleRadius;
        } else { //if deltaDistance > outerCircleRadius, then actuator is at its largest value and we cant increase it anymore
            actuatorX = deltaX/deltaDistance;
            actuatorY = deltaY/deltaDistance;
        }
    }

    public void resetActuator() {
        actuatorX = 0.0;
        actuatorY = 0.0;
    }

    public double getActuatorX() {
        return actuatorX;
    }

    public double getActuatorY() {
        return actuatorY;
    }
}
