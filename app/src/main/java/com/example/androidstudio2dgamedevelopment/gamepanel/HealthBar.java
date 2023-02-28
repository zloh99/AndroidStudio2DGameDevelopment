package com.example.androidstudio2dgamedevelopment.gamepanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.GameDisplay;
import com.example.androidstudio2dgamedevelopment.R;
import com.example.androidstudio2dgamedevelopment.gameobject.Player;

/**
 * HealthBar displays player's health to the screen
 */

public class HealthBar {
    private Player player;
    private int width, height, margin; //margin: distance between outermost point of the border and health bar
    private Paint borderPaint, healthPaint;

    /**
     * Constructor for HealthBar
     */
    public HealthBar(Context context, Player player) {
        this.player = player;
        this.width = 100; //initialise width to _ pixels
        this.height = 20;
        this.margin = 2;

        this.borderPaint = new Paint();
        int borderColor = ContextCompat.getColor(context, R.color.healthBarBorder);
        borderPaint.setColor(borderColor);


        this.healthPaint = new Paint();
        int healthColor = ContextCompat.getColor(context, R.color.healthBarHealth);
        healthPaint.setColor(healthColor);
    }

    public void draw(Canvas canvas, GameDisplay gameDisplay) {
        float x = (float) player.getPositionX(); //getPosition method returns double but we convert result to a float since draw method takes in floats
        float y = (float) player.getPositionY();
        float distanceToPlayer = 30; //distance of healthbar to player in pixels
        float healthPointPercentage = (float) player.getHealthPoints()/player.MAX_HEALTH_POINTS;
        
        //Draw border
        float borderLeft, borderTop, borderRight, borderBottom;
        borderLeft = x - width/2;
        borderRight = x + width/2;
        borderBottom = y - distanceToPlayer; //topmost y coordinate starts from 0
        borderTop = borderBottom - height;
        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(borderLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(borderRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(borderBottom),
                borderPaint); //draw a rectangle that takes in 5 arguments

        //Draw health meter
        float healthLeft, healthTop, healthRight, healthBottom, healthWidth, healthHeight;
        healthWidth = width - 2*margin;
        healthHeight = height - 2*margin;
        healthLeft = borderLeft + margin;
        healthRight = healthLeft + healthWidth*healthPointPercentage; //to scale up or down the health bar based on health point %
        healthBottom = borderBottom - margin;
        healthTop = healthBottom - healthHeight;

        canvas.drawRect(
                (float) gameDisplay.gameToDisplayCoordinatesX(healthLeft),
                (float) gameDisplay.gameToDisplayCoordinatesY(healthTop),
                (float) gameDisplay.gameToDisplayCoordinatesX(healthRight),
                (float) gameDisplay.gameToDisplayCoordinatesY(healthBottom),
                healthPaint);
    }
}
