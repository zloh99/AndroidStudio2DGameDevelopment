package com.example.androidstudio2dgamedevelopment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;

import com.example.androidstudio2dgamedevelopment.object.Circle;
import com.example.androidstudio2dgamedevelopment.object.Enemy;
import com.example.androidstudio2dgamedevelopment.object.Player;
import com.example.androidstudio2dgamedevelopment.object.Spell;

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

    public Game(Context context) {
        super(context);

        //Get surface holder and add callback
        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameLoop = new GameLoop(this, surfaceHolder);

        //Initialise game objects
        joystick = new Joystick(275, 700, 70, 40); //create new joystick object
        player = new Player(context, joystick, 2*500, 500, 32);
        //enemy = new Enemy(context, player, 500, 200, 32);

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
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawUPS(canvas);
        drawFPS(canvas);

        joystick.draw(canvas);
        player.draw(canvas);
        for (Enemy enemy: enemyList) {
            enemy.draw(canvas);
        }
        for (Spell spell: spellList) {
            spell.draw(canvas);
        }
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: " + averageUPS, 100, 100, paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + averageFPS, 100, 200, paint);
    }

    public void update() {
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

        //Iterate through enemyList and check for collision between each enemy and the player and all spells
        Iterator<Enemy> iteratorEnemy = enemyList.iterator();
        while (iteratorEnemy.hasNext()) {
            Circle enemy = iteratorEnemy.next();
            if (Circle.isColliding(enemy, player)) {
                //Remove enemy if it is colliding with player
                iteratorEnemy.remove();
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
    }
}
