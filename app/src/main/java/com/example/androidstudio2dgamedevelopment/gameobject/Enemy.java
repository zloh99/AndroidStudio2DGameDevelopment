package com.example.androidstudio2dgamedevelopment.gameobject;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.GameLoop;
import com.example.androidstudio2dgamedevelopment.R;

/**
 * Enemy is a character which always moves in the direction of the player
 * The enemy class is an extension of a Circle, which is an extension of GameObject
 */
public class Enemy extends Circle {
    private static final double SPEED_PIXELS_PER_SECOND = Player.SPEED_PIXELS_PER_SECOND*0.6;
    private static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private static final double SPAWNS_PER_MINUTE = 20;
    private static final double SPAWNS_PER_SECOND = SPAWNS_PER_MINUTE/60.0;
    private static final double UPDATES_PER_SPAWN = GameLoop.MAX_UPS/SPAWNS_PER_SECOND;
    private static double updatesUntilNextSpawn = UPDATES_PER_SPAWN;
    private final Player player;

    public Enemy(Context context, Player player, double positionX, double positionY, double radius) {
        super(context, ContextCompat.getColor(context, R.color.enemy), positionX, positionY, radius);
        this.player = player;
    }

    public Enemy(Context context, Player player) {
        super(context,
                ContextCompat.getColor(context, R.color.enemy),
                Math.random()*1000, //random x coordinate between 1 and 1000
                Math.random()*1000, //random y coordinate between 1 and 1000
                30);
        this.player = player;
    }

    /**
     * readyToSpawn checks if a new enemy should spawn, according to the decided number of spawns
     * per minute (see SPAWNS_PER_MINUTE above)
     * @return
     */
    public static boolean readyToSpawn() {
        if(updatesUntilNextSpawn <= 0) {
            updatesUntilNextSpawn += UPDATES_PER_SPAWN;
            return true; //return true to let the update method in the Game class know to add 1 enemy
        } else {
            updatesUntilNextSpawn --;
            return false; //return false to let the update method in Game class to know NOT to spawn any enemy
        }
    }

    @Override
    public void update() {
        //Update velocity of the enemy to be in the direction of player
        //1: Calculate vector (coordinates) from enemy to player (x, y)
        double distanceToPlayerX = player.getPositionX() - positionX;
        double distanceToPlayerY = player.getPositionY() - positionY;

        //2: Calculate absolute distance between enemy (this) and player
        double distanceToPlayer = GameObject.getDistanceBetweenObjects(this, player);

        //3: Calculate direction from enemy to player
        double directionX = distanceToPlayerX/distanceToPlayer;
        double directionY = distanceToPlayerY/distanceToPlayer;

        //4: Set velocity in the direction of the player
        if(distanceToPlayer > 0) { //avoid division by 0
            velocityX = directionX*MAX_SPEED;
            velocityY = directionY*MAX_SPEED;
        } else {
            velocityX = 0;
            velocityY = 0;
        }

        //5: Update position of enemy
        positionX += velocityX;
        positionY += velocityY;
    }
}
