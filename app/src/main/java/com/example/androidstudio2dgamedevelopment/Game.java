package com.example.androidstudio2dgamedevelopment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.gameobject.Circle;
import com.example.androidstudio2dgamedevelopment.gameobject.Enemy;
import com.example.androidstudio2dgamedevelopment.gameobject.Player;
import com.example.androidstudio2dgamedevelopment.gameobject.Spell;
import com.example.androidstudio2dgamedevelopment.gamepanel.GameOver;
import com.example.androidstudio2dgamedevelopment.gamepanel.Joystick;
import com.example.androidstudio2dgamedevelopment.gamepanel.Performance;
import com.example.androidstudio2dgamedevelopment.graphics.SpriteSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Game manages all objects in the game and is responsible for updating all states and rendering all
 * objects to screen
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback {
    private final Player player;
    private final Joystick joystick;
    //private final Enemy enemy;
    private GameLoop gameLoop;
    private List<Enemy> enemyList = new ArrayList<Enemy>(); //array list of type Enemy
    private List<Spell> spellList = new ArrayList<Spell>();
    private int joystickPointerId = 0;
    private int numberOfSpellsToCast = 0;
    private GameOver gameOver;
    private Performance performance;
    private GameDisplay gameDisplay;

    public Game(Context context) {
        super(context);

        //Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

        //Initialise game panels (all graphical objects that do not interact with any game objects)
        performance = new Performance(context, gameLoop);
        gameOver = new GameOver(context);
        joystick = new Joystick(275, 700, 70, 40); //create new joystick object

        //Initialise game objects
        SpriteSheet spriteSheet = new SpriteSheet(context);
        player = new Player(context, joystick, 2*500, 500, 32, spriteSheet.getPlayerSprite());

        //Initialise game display and center it around the player
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        gameDisplay = new GameDisplay(displayMetrics.widthPixels, displayMetrics.heightPixels, player);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Handle different touch event actions
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: //pointer ID - individuates touch actions
                if(joystick.getIsPressed()) {
                    //This means joystick is alr pressed when another touch event happens -> cast spell
                    numberOfSpellsToCast ++;
                }
                else if(joystick.isPressed((double) event.getX(), (double) event.getY())){
                    //joystick is pressed -> setIsPressed(true) and store pointer ID
                    joystickPointerId = event.getPointerId(event.getActionIndex());
                    joystick.setIsPressed(true); //indicate that joystick is pressed
                }
                else {
                    //joystick not pressed previously or currently -> cast spell
                    numberOfSpellsToCast ++;
                }
                return true; //indicate that event has been handled

            case MotionEvent.ACTION_MOVE:
                //joystick was pressed previously and is now moved
                if(joystick.getIsPressed()){
                    joystick.setActuator((double) event.getX(), (double) event.getY());
                }
                return true; //indicate that event has been handled

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if(joystickPointerId == event.getPointerId(event.getActionIndex())) {
                    //joystick let go off -> setIsPressed(false) and resetActuator()
                    joystick.setIsPressed(false); //set the isPressed boolean to false
                    joystick.resetActuator();
                }
               return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceCreated()"); //put message in logcat
        if (gameLoop.getState().equals(Thread.State.TERMINATED)) { //check state of thread to see if it is terminated
            gameLoop = new GameLoop(this, holder);
            //then we instantiate a new gameLoop object, because a thread object can only be run once until it is destroyed,
            // so if we wanna run it again we have to create a new one
        }
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Game.java", "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceDestroyed()");
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        //Draw game objects
        player.draw(canvas, gameDisplay);

        for (Enemy enemy: enemyList) {
            enemy.draw(canvas, gameDisplay);
        }
        for (Spell spell: spellList) {
            spell.draw(canvas, gameDisplay);
        }

        //Draw game panels
        joystick.draw(canvas);
        performance.draw(canvas);

        // Draw Game Over if player healthPoints <= 0
        if(player.getHealthPoints() <= 0) {
            gameOver.draw(canvas);
        }
    }

    public void update() {

        //stop updating game is player is dead (aka pause the game on a game over screen)
        if (player.getHealthPoints() <= 0) {
            return; //breaking the loop to return nothing (since the return value is void)
        }


        // Update game state
        joystick.update();
        player.update();

        //Spawn enemy if it is time to spawn new enemies
        if(Enemy.readyToSpawn()) {
            enemyList.add(new Enemy(getContext(), player));
        }

        //Update state of each enemy by looping through the list
        while (numberOfSpellsToCast > 0) {
            spellList.add(new Spell(getContext(), player));
            numberOfSpellsToCast --;
        }
        for (Enemy enemy: enemyList) {
            enemy.update();
        }

        //Iterate through enemyList and check for collision between each enemy and the player and all spells in spellist
        Iterator<Enemy> iteratorEnemy = enemyList.iterator();
        while (iteratorEnemy.hasNext()) {
            Circle enemy = iteratorEnemy.next();
            if (Circle.isColliding(enemy, player)) {
                //Remove enemy if it is colliding with player, and deduct health
                iteratorEnemy.remove();
                player.setHealthPoints(player.getHealthPoints() - 1);
                continue;
            }

            Iterator<Spell> iteratorSpell = spellList.iterator();
            while (iteratorSpell.hasNext()) {
                Circle spell = iteratorSpell.next(); //increments iterator counter and returns next element and stores in in 'spell' variable
                //remove spell if it collides with enemy
                if (Circle.isColliding(spell, enemy)) {
                    iteratorSpell.remove();
                    iteratorEnemy.remove();
                    break;
                }
            }
        }

        //Update state of each spell
        for (Spell spell: spellList) {
            spell.update();
        }

        gameDisplay.update();
    }

    public void pause() {
        //pause game loop
        gameLoop.stopLoop();
    }
}
