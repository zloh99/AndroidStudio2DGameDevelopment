package com.example.androidstudio2dgamedevelopment.gameobject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.GameDisplay;
import com.example.androidstudio2dgamedevelopment.GameLoop;
import com.example.androidstudio2dgamedevelopment.Utils;
import com.example.androidstudio2dgamedevelopment.gamepanel.HealthBar;
import com.example.androidstudio2dgamedevelopment.gamepanel.Joystick;
import com.example.androidstudio2dgamedevelopment.R;
import com.example.androidstudio2dgamedevelopment.gamepanel.Performance;
import com.example.androidstudio2dgamedevelopment.graphics.Animator;
import com.example.androidstudio2dgamedevelopment.graphics.Sprite;

/**
 * Player is a character in the game controllable by the player with a joystick
 * The Player class is an extension of the Circle class, which is an extension of the GameObject class
 */

public class Player extends Circle {
    public static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS; //pixels/s divided by updates/s = pixels/update
    public static final int MAX_HEALTH_POINTS = 10;
    private final Joystick joystick;
    private HealthBar healthBar;
    private int healthPoints;
    private Animator animator;
    private PlayerState playerState;

    /**
     * Constructor for player class
     * @param context
     * @param joystick
     * @param positionX
     * @param positionY
     * @param radius
     */
    public Player(Context context, Joystick joystick, double positionX, double positionY, double radius, Animator animator) {
        super(context, ContextCompat.getColor(context, R.color.player), positionX, positionY, radius);
        this.joystick = joystick;
        this.radius = radius;
        this.animator = animator;
        this.playerState = new PlayerState(this);
        paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.player);
        paint.setColor(color);

        this.healthBar = new HealthBar(context,this);
        this.healthPoints = MAX_HEALTH_POINTS;
    }

    public void update() {
        //update player velocity based on the actuator of the joystick
        velocityX = joystick.getActuatorX()*MAX_SPEED;
        velocityY = joystick.getActuatorY()*MAX_SPEED;

        //update player position
        positionX += velocityX;
        positionY+= velocityY;

        //update player direction
        if(velocityX != 0 || velocityY != 0) {
            //Normalize velocity to get direction (aka find unit vector of velocity)
            double distance = Utils.getDistanceBetweenPoints(0, 0, velocityX, velocityX);
            directionX = velocityX/distance;
            directionY = velocityY/distance;
        }

        playerState.update();
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        animator.draw(canvas, gameDisplay, this);

        //override draw method to draw a health bar
        healthBar.draw(canvas, gameDisplay);
    }

    public void setPosition(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        if(healthPoints >= 0){
            this.healthPoints = healthPoints;
        }
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
}
