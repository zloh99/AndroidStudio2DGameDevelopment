package com.example.androidstudio2dgamedevelopment.object;

import android.content.Context;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.GameLoop;
import com.example.androidstudio2dgamedevelopment.Joystick;
import com.example.androidstudio2dgamedevelopment.R;

/**
 * Player is a character in the game controllable by the player with a joystick
 * The Player class is an extension of the Circle class, which is an extension of the GameObject class
 */

public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS; //pixels/s divided by updates/s = pixels/update
    private final Joystick joystick;

    public Player(Context context, Joystick joystick, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY, radius);
        this.joystick = joystick;
        this.radius = radius;
        paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.player);
        paint.setColor(color);
    }

    public void update() {
        //update player velocity based on the actuator of the joystick
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        velocityY = joystick.getActuatorY()*MAX_SPEED;

        //update player position
        positionX += velocityX;
        positionY+= velocityY;
    }

    public void setPosition(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
}
